package org.example;

import org.example.requests.CommandRequest;
import org.example.requests.NetworkRequest;
import org.example.responses.NetworkResponse;

public interface UdpClient {
    NetworkResponse send(NetworkRequest req, long timeoutMs) throws Exception;
    NetworkResponse send(NetworkRequest req) throws Exception;
}
