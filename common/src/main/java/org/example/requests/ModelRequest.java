package org.example.requests;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ModelRequest extends NetworkRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ModelRequest(UUID id, String commandName) {
        super(id, commandName);
    }
    public ModelRequest(String commandName) {
        super(commandName);
    }
}
