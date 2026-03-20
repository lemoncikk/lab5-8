package org.example;

import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;

public interface Controller {
    CommandResult handle(String commandName, CommandArgs args) throws AppException;
    CommandArgs getCommandModel(String commandName) throws AppException;
}
