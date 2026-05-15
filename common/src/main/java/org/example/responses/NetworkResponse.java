package org.example.responses;

import org.example.command.CommandArgs;
import org.example.command.CommandResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public sealed interface NetworkResponse extends Serializable
        permits NetworkResponse.CommandSuccess, NetworkResponse.ModelSuccess, NetworkResponse.Error, NetworkResponse.AuthSuccess
{
    record CommandSuccess(CommandResult result, UUID id) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    record ModelSuccess(CommandArgs model, UUID id) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    record AuthSuccess(String token, UUID id) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    record Error(String code, String message, UUID id) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
