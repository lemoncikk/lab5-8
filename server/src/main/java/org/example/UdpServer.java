package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.CommandExecutionException;
import org.example.requests.ExecuteRequest;
import org.example.requests.ModelRequest;
import org.example.requests.NetworkRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.UUID;
@Slf4j
public class UdpServer implements AutoCloseable {
    private final DatagramChannel channel = DatagramChannel.open();
    private final Selector selector = Selector.open();
    private boolean running = true;
    private final ByteBuffer buffer = ByteBuffer.allocate(8196);
    private final ServerController ctrl;
    private final JavaSerializableCodec<NetworkRequest> requestCodec;
    private final JavaSerializableCodec<NetworkResponse> responseCodec;

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
                    handleKey(key);
                }
            }
        }
    }


    private NetworkResponse getRequestResponse(NetworkRequest request, SocketAddress sender) {
        if (request instanceof ModelRequest mr) {
            return handleModelRequest(mr);
        } else if (request instanceof ExecuteRequest er) {
            return handleExecuteRequest(er);
        } else {
            return new NetworkResponse.Error("SERVER_ERROR_001", "Undefined request type", request.getId());
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        SocketAddress sender;
        DatagramChannel channel = (DatagramChannel) key.channel();
        buffer.clear();
        sender = channel.receive(buffer);
        if (sender != null) {
            log.info("Request from: {} (toString: {})", sender, sender.toString());
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            try {
                var request = requestCodec.decode(data);
                sendResponse(
                        getRequestResponse(request, sender),
                        sender
                );

            } catch (CommandExecutionException e) {
                log.info("Failed to execute command", e);
                sendResponse(new NetworkResponse.Error("EXECUTION_ERROR", e.getMessage(), UUID.randomUUID()), sender);
            }

            catch (Exception e) {
                log.warn("Server error", e);
                sendResponse(new NetworkResponse.Error("SERVER_ERROR", e.getMessage(), UUID.randomUUID()), sender);
            }

        }
    }

    private void sendResponse(NetworkResponse res, SocketAddress recipient) throws IOException {
        buffer.clear();
        buffer.put(responseCodec.encode(res));
        buffer.flip();
        channel.send(buffer, recipient);
        log.debug("Package sent {}", recipient);
    }

    private NetworkResponse.CommandSuccess handleExecuteRequest(ExecuteRequest req) {
        log.debug("Command {} is handled", req.getCommandName());
        CommandResult result = ctrl.handle(req.getCommandName(), req.getArgs());
        return new NetworkResponse.CommandSuccess(result, req.getId());
    }

    private NetworkResponse.ModelSuccess handleModelRequest(ModelRequest req) {
        CommandArgs result = ctrl.getCommandModel(req.getCommandName());
        return new NetworkResponse.ModelSuccess(result, req.getId());
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
