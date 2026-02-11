package org.example;

import org.example.Commands.CommandArgs;
import org.example.Commands.CommandResult;
import org.example.Commands.exceptions.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    static void main() throws IOException {
        var client = new CLI();
        var ctx = new Context();
        var ctrl = new Controller(ctx);
        client.exec(ctrl);
    }
}
