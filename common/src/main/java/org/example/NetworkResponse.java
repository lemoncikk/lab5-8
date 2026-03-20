package org.example;

import org.example.command.CommandArgs;
import org.example.command.CommandResult;

import java.io.Serial;
import java.io.Serializable;

public sealed interface NetworkResponse extends Serializable
        permits NetworkResponse.CommandSuccess, NetworkResponse.ModelSuccess, NetworkResponse.Error
{
    record CommandSuccess(CommandResult result) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    record ModelSuccess(CommandArgs model) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    record Error(String code, String message) implements NetworkResponse, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
