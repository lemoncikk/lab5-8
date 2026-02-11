package org.example;

import org.example.Commands.CommandArgs;
import org.example.Commands.exceptions.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI {
    public void exec(Controller ctrl) throws IOException {
        System.out.println("Start!");
        boolean stopFlag = false;
        while(!stopFlag) {
            var br = new BufferedReader(new InputStreamReader(System.in));
            String[] args = Parser.parse(br.readLine());
            try {
                CommandArgs model = ctrl.getCommandModel(args[0]);
                if (model != null) {
                    //need realization
                }
                ctrl.handle(args[0], model);
            }
            catch (CommandExecutionException e) {
                stopFlag = e.isStopFlag();
                System.out.println(e.getMessage());
            }
        }
    }
}
