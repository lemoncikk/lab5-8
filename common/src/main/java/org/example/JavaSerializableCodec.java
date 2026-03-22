package org.example;

import java.io.*;

public class JavaSerializableCodec<T> implements UdpCodec<T> {
    private final Class<T> type;

    public JavaSerializableCodec(Class<T> type) {
        this.type = type;
    }
    @Override
    public byte[] encode(T message) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(message);
            oos.flush();
            return baos.toByteArray();
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T decode(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bios = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bios)) {
            Object obj = ois.readObject();
            if (!(type.isInstance(obj))) {
                throw new ClassCastException("Expected %s, but got %s".formatted(
                        type.getName(), obj.getClass().getName()));
            }
            return (T) obj;
        }
    }

    @Override
    public Class<T> messageType() {
        return type;
    }
}
