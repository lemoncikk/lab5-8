package org.example;

import org.example.model.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Main {
    static void main(String[] args) throws IOException {
        var client = new CLI();
        Context ctx = null;
        try {
            ctx = Context.loadFromFile(Arrays.stream(args).findFirst().get());
            ctx.setArgs(args);
        }catch (Exception e) {
            System.out.println("Не удалось загрузить коллекцию из файла");
            ctx = new Context();
        }

        var ctrl = new Controller(ctx);
        client.exec(ctrl);
    }
}
