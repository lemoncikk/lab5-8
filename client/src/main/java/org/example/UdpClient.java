package org.example;

import org.example.exceptions.AppException;
import org.example.requests.NetworkRequest;

public interface UdpClient {
    NetworkResponse send(NetworkRequest req, long timeoutMs) throws Exception;
    NetworkResponse send(NetworkRequest req) throws Exception;
}
