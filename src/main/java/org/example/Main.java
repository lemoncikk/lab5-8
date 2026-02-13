package org.example;

import org.example.model.Context;

import java.io.IOException;

public class Main {
    static void main() throws IOException {
        var client = new CLI();
        var ctx = new Context();
        var ctrl = new Controller(ctx);
        client.exec(ctrl);
    }
}
