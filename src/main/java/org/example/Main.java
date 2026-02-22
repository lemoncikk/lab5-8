package org.example;

import org.example.model.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws IOException {
        Context ctx;
        try {
            ctx = Context.loadFromFile(Arrays.stream(args).findFirst().get());
            ctx.setArgs(args);
        }catch (Exception e) {
            System.out.println("Не удалось загрузить коллекцию из файла. Проверьте существует ли файл и есть ли у пользователя права на его чтение");
            ctx = new Context();
        }
        var ctrl = new Controller(ctx);
        var client = new CLI(ctrl);
        client.exec();
    }
}
