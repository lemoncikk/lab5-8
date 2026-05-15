package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.*;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.StringField;
import org.example.exceptions.AppException;
import org.example.exceptions.NetworkException;
import org.example.requests.ExecuteRequest;
import org.example.requests.ModelRequest;
import org.example.responses.NetworkResponse;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
@Slf4j
public class NetworkController implements Controller {
    private final UdpClient client;
    private final SessionManager sm;

    public NetworkController(int port, String hostName) throws IOException {
        client = new UdpDatagramClient(port, hostName);
        sm = new SessionManager(client);
    }

    @Override
    public CommandResult handle(String commandName, CommandArgs args) throws AppException {
        try {
            if (commandName.toLowerCase().trim().equals("login") ||
                    commandName.toLowerCase().trim().equals("register")) return authCommandHandler(commandName, args);
            UUID id = UUID.randomUUID();
            var res = client.send(new ExecuteRequest(id, commandName, args, sm.getToken()));
            if (res instanceof NetworkResponse.CommandSuccess s) {
                return s.result();
            }
            if (res instanceof NetworkResponse.AuthSuccess s) {

            }
            if (res instanceof NetworkResponse.Error e) {
                throw new AppException(e.message());
            }
            throw new NetworkException("Wrong answer from server");

        } catch (TimeoutException | InterruptedException | IOException | ClassNotFoundException e) {
            throw new NetworkException(e.getMessage());
        } catch (Exception e) {
            throw new NetworkException(e.getMessage());
        }
    }

    @Override
    public CommandArgs getCommandModel(String commandName) throws AppException {
        try {
            if (commandName.toLowerCase().trim().equals("login") ||
                    commandName.toLowerCase().trim().equals("register")) return authCommandModel(commandName);
            UUID id = UUID.randomUUID();
            sm.getToken();
            var res = client.send(new ModelRequest(id, commandName));
            if (res instanceof NetworkResponse.ModelSuccess s) {
                return s.model();
            }
            if (res instanceof NetworkResponse.Error e) {
                throw new AppException(e.message());
            }
            throw new NetworkException("Wrong answer from server");

        } catch (TimeoutException | InterruptedException | IOException | ClassNotFoundException e) {
            throw new NetworkException(e.getMessage());
        } catch (Exception e) {
            throw new NetworkException(e.getMessage());
        }
    }

    private CommandArgs authCommandModel(String commandName) {
        var prepare = new CommandArgs().addField(new StringField("Login", "login", true))
                .addField(new StringField("Password", "Password", true));
        return prepare;
    }
    private CommandResult authCommandHandler(String commandName, CommandArgs args) throws Exception {
        if (commandName.toLowerCase().trim().equals("login")) {
            if (args == null || args.getFields().size() < 2) {
                throw new AppException("Bad args");
            }
            try {
                var fields = args.getFields();
                sm.login(((StringField) (fields.get(0))).getValue(), ((StringField) (fields.get(1))).getValue());
                return new CommandResult("Success!", null, false);
            }
            catch (Exception e) {
                throw e;
            }
        } else {
            if (args == null || args.getFields().size() < 2) {
                throw new AppException("Bad args");
            }
            try {
                var fields = args.getFields();
                sm.register(((StringField) (fields.get(0))).getValue(), ((StringField) (fields.get(1))).getValue());
                return new CommandResult("Success!", null, false);
            }
            catch (Exception e) {
                throw e;
            }
        }
    }
}
