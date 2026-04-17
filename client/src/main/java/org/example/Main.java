package org.example;

import com.williamcallahan.tui4j.compat.bubbletea.Program;
import lombok.extern.slf4j.Slf4j;
import org.example.cli.CLI;
import org.example.controllers.NetworkController;
import org.example.tui.view.BandList;

import java.io.IOException;
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        int port = 2222;
        String hostname = "localhost";
        if (args.length >= 3) {
            try {
                var temp = Integer.parseInt(args[1]);
                if (temp>1024) port = temp;
            } catch (Exception e) {
                log.warn("Error while commandline args parsing", e);
            }
            if (args[2] != null && !args[2].isEmpty()) {
                hostname = args[2];
            }
        }
        log.debug(hostname);
        log.debug(String.valueOf(port));
        var ntCtrl = new NetworkController(port, hostname);
        if (args.length >= 1 && args[0].trim().equalsIgnoreCase("tui")) {
            BandList model = new BandList(ntCtrl);
            Program program = new Program(model);
            program.run();
            log.debug("App started via TUI");
            return;
        }
        var client = new CLI(ntCtrl);
        log.debug("App started via CLI");
        try {
            client.exec();
        } catch (Exception e) {
            log.warn("Exception:", e);
        }
    }
}
