package org.example.requests;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class CommandRequest implements Serializable, AutoCloseable, NetworkRequest {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    @Getter
    protected final String commandName;

    protected CommandRequest(UUID id, String commandName) {
        this.id = id;
        this.commandName = commandName;
    }
    protected CommandRequest(String commandName) {
        this(UUID.randomUUID(), commandName);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void close() throws Exception {

    }
}