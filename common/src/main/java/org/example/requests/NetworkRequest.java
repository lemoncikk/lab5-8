package org.example.requests;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class NetworkRequest implements Serializable, AutoCloseable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    protected final UUID id;
    @Getter
    protected final String commandName;

    protected NetworkRequest(UUID id, String commandName) {
        this.id = id;
        this.commandName = commandName;
    }
    protected NetworkRequest(String commandName) {
        this.id = UUID.randomUUID();
        this.commandName = commandName;
    }

    @Override
    public void close() throws Exception {

    }
}