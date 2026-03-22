package org.example;

import com.williamcallahan.tui4j.compat.bubbletea.Program;
import org.example.model.Context;

import java.util.Arrays;

public class Main {
    static void main(String[] args) {
        Context ctx;
        try {
            ctx = Context.loadFromFile(Arrays.stream(args).findFirst().get());
            ctx.setArgs(args);
        }catch (Exception e) {
            System.out.println("Не удалось загрузить коллекцию из файла. Проверьте существует ли файл и есть ли у пользователя права на его чтение");
            ctx = new Context();
        }
        var ctrl = new ServerController(ctx);
        try (var server = new UdpServer(2222, ctrl)) {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
