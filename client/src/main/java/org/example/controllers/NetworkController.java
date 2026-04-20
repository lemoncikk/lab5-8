package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.*;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;
import org.example.exceptions.NetworkException;
import org.example.requests.ExecuteRequest;
import org.example.requests.ModelRequest;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
@Slf4j
public class NetworkController implements Controller {
    private final UdpClient client;

    public NetworkController(int port, String hostName) throws IOException {
        client = new UdpDatagramClient(port, hostName);
    }

    @Override
    public CommandResult handle(String commandName, CommandArgs args) throws AppException {
        try {
            UUID id = UUID.randomUUID();
            var res = client.send(new ExecuteRequest(id, commandName, args));
            if (res instanceof NetworkResponse.CommandSuccess s) {
                return s.result();
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
            UUID id = UUID.randomUUID();
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
}
