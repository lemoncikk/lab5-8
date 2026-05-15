package org.example.requests;

import lombok.Getter;
import org.example.command.CommandArgs;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ExecuteRequest extends CommandRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final CommandArgs args;
    @Getter
    private final String token;

    public ExecuteRequest(UUID id, String commandName, CommandArgs args, String token) {
        super(id, commandName);
        this.args = args;
        this.token = token;
    }
    public ExecuteRequest(String commandNme, CommandArgs args, String token) {
        this(UUID.randomUUID(), commandNme, args, token);
    }

}
