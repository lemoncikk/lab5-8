package org.example;

import com.williamcallahan.tui4j.compat.bubbletea.Program;
import org.example.cli.CLI;
import org.example.controllers.NetworkController;
import org.example.tui.view.BandList;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 2222;
        String hostname = "localhost";
        if (args.length >= 4) {
            try {
                var temp = Integer.parseInt(args[2]);
                if (temp>1024) port = temp;
            } catch (Exception e) {}
            if (args[3] != null && !args[3].isEmpty()) {
                hostname = args[3];
            }
        }
        var ntCtrl = new NetworkController(port, hostname);
        if (args.length >= 2 && args[1].trim().equalsIgnoreCase("tui")) {
            BandList model = new BandList(ntCtrl);
            Program program = new Program(model);
            program.run();
            return;
        }
        var client = new CLI(ntCtrl);
        client.exec();
    }
}
