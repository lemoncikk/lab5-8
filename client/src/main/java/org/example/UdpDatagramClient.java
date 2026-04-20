package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.AppException;
import org.example.exceptions.NetworkException;
import org.example.requests.NetworkRequest;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;
@Slf4j
public class UdpDatagramClient implements UdpClient {
    DatagramSocket socket;
    InetAddress addr;
    int port;
    final static int TIME_OUT_MS = 10000;
    private final JavaSerializableCodec<NetworkRequest> requestCodec;
    private final JavaSerializableCodec<NetworkResponse> responseCodec;
    private static final int BUFFER_SIZE = 8192;

    public UdpDatagramClient(int port, String hostname) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        addr = InetAddress.getByName(hostname);
        this.port = port;
        requestCodec = new JavaSerializableCodec<>(NetworkRequest.class);
        responseCodec = new JavaSerializableCodec<>(NetworkResponse.class);
        socket.setSoTimeout(TIME_OUT_MS);
    }

    @Override
    public NetworkResponse send(NetworkRequest req, long timeoutMs) throws Exception {
        byte[] reqData = requestCodec.encode(req);
        DatagramPacket reqPacket = new DatagramPacket(reqData, reqData.length, addr, port);
        socket.send(reqPacket);
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket resPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(resPacket);

        var decoded = responseCodec.decode(buffer);
        if (decoded instanceof NetworkResponse.CommandSuccess s) {
            if (s.id().equals(req.getId())) return s;
        } else if (decoded instanceof NetworkResponse.ModelSuccess s) {
            if (s.id().equals(req.getId())) return s;
        } else if (decoded instanceof NetworkResponse.Error s) {
            return s;
        }
        log.warn("Wrong type:{}", decoded);
        throw new Exception("Wrong type");
    }

    @Override
    public NetworkResponse send(NetworkRequest req) throws Exception {
        return send(req, TIME_OUT_MS);
    }
}
