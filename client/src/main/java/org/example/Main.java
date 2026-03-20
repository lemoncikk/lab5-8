package org.example;

import com.williamcallahan.tui4j.compat.bubbletea.Program;
import org.example.cli.CLI;
import org.example.controllers.LocalController;
import org.example.model.Context;
import org.example.tui.view.BandList;

import java.io.IOException;
import java.util.Arrays;

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
        var ctrl = new LocalController(ctx);
        if (args.length >= 2 && args[1].trim().equalsIgnoreCase("tui")) {
            BandList model = new BandList(ctrl);
            Program program = new Program(model);
            program.run();
            return;
        }
        var client = new CLI(ctrl);
        client.exec();
    }
}
