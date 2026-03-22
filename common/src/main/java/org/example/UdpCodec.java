package org.example;

import java.io.IOException;

public interface UdpCodec<T> {
    byte[] encode(T message) throws IOException;
    T decode(byte[] data) throws IOException, ClassNotFoundException;
    Class<T> messageType();
}