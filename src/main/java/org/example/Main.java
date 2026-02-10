package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    static void main() {
        System.out.println("Start!");
        boolean stopFlag = false;
        System.out.println(new EchoCommand().getName());
        while(!stopFlag) {
            var br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String[] args = Parser.parse(br.readLine());
                var ctrl = new Controller(new Context());
                var result = ctrl.handle(args[0], Arrays.copyOfRange(args, 1, args.length));
                if (result instanceof CommandResult.Success) {
                    System.out.println(((CommandResult.Success) result).msg());
                    stopFlag = ((CommandResult.Success) result).stopFlag();
                }
                if (result instanceof CommandResult.Failure) {
                    System.out.println("Произошла ошибка!");
                    System.out.println(((CommandResult.Failure) result).msg());
                    stopFlag = ((CommandResult.Failure) result).stopFlag();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
