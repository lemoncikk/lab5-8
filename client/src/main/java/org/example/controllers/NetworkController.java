package org.example.controllers;

import org.example.Controller;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkController implements Controller {
    private DatagramSocket soket;
    private InetAddress address;
    private int port;

    public NetworkController(String host, int port) throws UnknownHostException, SocketException {
        address = InetAddress.getByName(host);
        soket = new DatagramSocket();
        this.port = port;
    }

    @Override
    public CommandResult handle(String commandName, CommandArgs args) throws AppException {

        return null;
    }

    @Override
    public CommandArgs getCommandModel(String commandName) throws AppException {

        return null;
    }
}
