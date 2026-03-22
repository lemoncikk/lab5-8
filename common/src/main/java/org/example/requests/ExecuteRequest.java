package org.example.requests;

import lombok.Getter;
import org.example.command.CommandArgs;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ExecuteRequest extends NetworkRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final CommandArgs args;

    public ExecuteRequest(UUID id, String commandName, CommandArgs args) {
        super(id, commandName);
        this.args = args;
    }
    public ExecuteRequest(String commandNme, CommandArgs args) {
        this(UUID.randomUUID(), commandNme, args);
    }

}
