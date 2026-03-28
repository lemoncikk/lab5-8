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
        int port = 2222;
        if (args.length >= 2) {
            try {
                var temp = Integer.parseInt(args[1]);
                if (temp>1024) port = temp;
            } catch (Exception e) {}
        }
        var ctrl = new ServerController(ctx);
        try (var server = new UdpServer(port, ctrl)) {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
