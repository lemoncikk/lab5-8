package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.requests.NetworkRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.TimeoutException;
@Slf4j
public class UdpChanellClient implements UdpClient {
    private static final int BUFFER_SIZE = 8192;
    private static final long DEFAULT_TIMEOUT = 10000;
    private final InetSocketAddress address;
    private final DatagramChannel channel;
    private final ByteBuffer buffer;
    private final JavaSerializableCodec<NetworkRequest> requestCodec;
    private final JavaSerializableCodec<NetworkResponse> responseCodec;

    public UdpChanellClient(int port, String hostName) throws IOException {
        address = new InetSocketAddress(hostName, port);
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(null);
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        requestCodec = new JavaSerializableCodec<>(NetworkRequest.class);
        responseCodec = new JavaSerializableCodec<>(NetworkResponse.class);
    }

    public NetworkResponse send(NetworkRequest req, long timeoutMs) throws TimeoutException, InterruptedException, IOException, ClassNotFoundException {
        byte[] reqData = requestCodec.encode(req);
        var sendBuffer = ByteBuffer.wrap(reqData);
        channel.send(sendBuffer, address);
        log.info("Package sent {}", address);
        long deadline = System.currentTimeMillis() + timeoutMs;
        while(System.currentTimeMillis() < deadline) {
            buffer.clear();
            SocketAddress sender = channel.receive(buffer);
            if (sender != null) {
                buffer.flip();
                byte[] res = new byte[buffer.remaining()];
                buffer.get(res);

                var decoded = responseCodec.decode(res);
                if (decoded instanceof NetworkResponse.CommandSuccess s) {
                    if (s.id().equals(req.getId())) return s;
                } else if (decoded instanceof NetworkResponse.ModelSuccess s) {
                    if (s.id().equals(req.getId())) return s;
                } else if (decoded instanceof NetworkResponse.Error s) {
                    return s;
                }

            }
        }
        throw new TimeoutException("Timeout!");
    }
    public NetworkResponse send(NetworkRequest req) throws IOException, InterruptedException, ClassNotFoundException, TimeoutException {
        return send(req, DEFAULT_TIMEOUT);
    }
}
