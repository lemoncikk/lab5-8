package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.AuthException;
import org.example.exceptions.CommandExecutionException;
import org.example.requests.*;
import org.example.responses.NetworkResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class UdpServer implements AutoCloseable {
    private final DatagramChannel channel = DatagramChannel.open();
    private final Selector selector = Selector.open();
    private volatile boolean running = true;
    private final ServerController ctrl;
    private final JavaSerializableCodec<NetworkRequest> requestCodec;
    private final JavaSerializableCodec<NetworkResponse> responseCodec;
    private final ExecutorService readPool = Executors.newCachedThreadPool();
    private final ExecutorService handlePool = Executors.newCachedThreadPool();
    private final ExecutorService writePool = Executors.newCachedThreadPool();

    public UdpServer(int port, ServerController ctrl) throws IOException {
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));
        channel.register(selector, SelectionKey.OP_READ);
        this.ctrl = ctrl;
        requestCodec = new JavaSerializableCodec<>(NetworkRequest.class);
        responseCodec = new JavaSerializableCodec<>(NetworkResponse.class);
    }

    public void run() throws IOException {
        log.info("Сервер запущен на порту: {}", channel.getLocalAddress());
        while(running) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext() && running) {
                SelectionKey key = keys.next();
                keys.remove();
                if (key.isReadable()) {
                    readPool.submit(() -> {
                        handleKey(key);
                    });
                }
            }
        }
    }


    private NetworkResponse getRequestResponse(NetworkRequest request) throws Exception {
        if (request instanceof CommandRequest cr) {
            return handleCommandRequest(cr);
        }
        if (request instanceof AuthRequest ar) {
            return handleAuthRequest(ar);
        }  else {
            return new NetworkResponse.Error("SERVER_ERROR_001", "Undefined request type", request.getId());
        }
    }
    private NetworkResponse handleCommandRequest(CommandRequest request) throws Exception {
        if (request instanceof ModelRequest mr) {
            return handleModelRequest(mr);
        } else if (request instanceof ExecuteRequest er) {
            return handleExecuteRequest(er);
        } else {
            return new NetworkResponse.Error("SERVER_ERROR_001", "Undefined request type", request.getId());
        }
    }

    private void handleKey(SelectionKey key) {
        try {
            SocketAddress sender;
            DatagramChannel channel = (DatagramChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(8196);
            sender = channel.receive(buffer);
            if (sender != null) {
                log.info("Request from: {} (toString: {})", sender, sender.toString());
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                handlePool.submit(() -> {
                    try {
                        var request = requestCodec.decode(data);
                        var response = getRequestResponse(request);
                        writePool.submit(() -> sendResponse(response, sender));
                    } catch (CommandExecutionException e) {
                        log.info("Failed to execute command", e);
                        writePool.submit(() -> {
                            sendResponse(new NetworkResponse.Error("EXECUTION_ERROR",
                                    e.getMessage(), UUID.randomUUID()), sender);
                        });
                    }
                    catch (AuthException e) {
                        log.error("Failed authentication", e);
                        writePool.submit(() -> {
                            sendResponse(new NetworkResponse.Error("AUTH_ERROR",
                                    e.getMessage(), UUID.randomUUID()), sender);
                        });
                    }
                    catch (Exception e) {
                        log.error("Server error", e);
                        writePool.submit(() -> {
                            sendResponse(new NetworkResponse.Error("SERVER_ERROR",
                                    e.getMessage(), UUID.randomUUID()), sender);
                        });
                    }
                });
            }
        } catch (IOException e) {

        }
    }

    private void sendResponse(NetworkResponse res, SocketAddress recipient) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8196);
            buffer.clear();
            buffer.put(responseCodec.encode(res));
            buffer.flip();
            channel.send(buffer, recipient);
            log.debug("Package sent {}", recipient);
        } catch (IOException e) {
            log.error("Error trying send response to {}", recipient, e);
        }

    }

    private NetworkResponse.CommandSuccess handleExecuteRequest(ExecuteRequest req) throws Exception {
        log.debug("Command {} is handled", req.getCommandName());
        ctrl.validateToken(req.getToken());
        CommandResult result = ctrl.handle(req.getCommandName(), req.getArgs());
        return new NetworkResponse.CommandSuccess(result, req.getId());
    }

    private NetworkResponse.ModelSuccess handleModelRequest(ModelRequest req) {
        CommandArgs result = ctrl.getCommandModel(req.getCommandName());
        return new NetworkResponse.ModelSuccess(result, req.getId());
    }

    private NetworkResponse.AuthSuccess handleAuthRequest(AuthRequest req) {
        try {
            String token = "";
            if (req.getOperation() == AuthRequest.AuthOperation.LOGIN) {
                token = ctrl.auth(req.getLogin(), PasswordHasher.hash(req.getPassword()));
            }
            if (req.getOperation() == AuthRequest.AuthOperation.REGISTER) {
                token = ctrl.register(req.getLogin(), PasswordHasher.hash(req.getPassword()));
            }
            if (token.isEmpty()) {
                throw new AuthException("Wrong operation or internal server error");
            }
            return new NetworkResponse.AuthSuccess(token, UUID.randomUUID());
        } catch (AuthException e) {
            log.warn("Auth error", e);
            throw e;
        }
    }

    @Override
    public void close() {
        try {
        running = false;
        selector.wakeup();
        selector.close();
        channel.close();
        ctrl.specialHandle("save", null);
        } catch (Exception e) {
            log.error("Exception while closing", e);
        }
    }
}
