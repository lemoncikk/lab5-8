package org.example;

import com.williamcallahan.tui4j.compat.bubbletea.Program;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Context;

import java.util.Arrays;
@Slf4j
public class Main {
    public static void main(String[] args) {
        Context ctx;
        try {
            ctx = Context.loadFromFile(Arrays.stream(args).findFirst().get());
        }catch (Exception e) {
            log.warn("Не удалось загрузить коллекцию из файла. Проверьте существует ли файл и есть ли у пользователя права на его чтение", e);
            ctx = new Context();
        }
        ctx.setArgs(args);
        int port = 2222;
        if (args.length >= 2) {
            try {
                var temp = Integer.parseInt(args[1]);
                if (temp>1024) port = temp;
            } catch (Exception e) {
                log.warn("Error while commandline args parsing", e);
            }
        }
        var ctrl = new ServerController(ctx);
        try (var server = new UdpServer(port, ctrl)) {
            Runtime.getRuntime().addShutdownHook(new Thread(server::close, "shutdown-hook"));
            server.run();
        } catch (Exception e) {
            log.error("Critical error", e);
        }
    }
}
