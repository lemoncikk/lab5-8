package org.example;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.example.command.Command;
import org.example.command.CommandRegistry;

import java.lang.reflect.Constructor;
import java.util.List;

public class CommandLoader {
    static void load(CommandRegistry cr, String path) {
        try (ScanResult sc = new ClassGraph()
                .enableClassInfo()
                .acceptPackages(path)
                .scan()) {
            var list = sc.getClassesImplementing(org.example.command.Command.class.getName());
            for (var ci : list) {
                if (ci.isAbstract() || ci.isInterface()) {
                    continue;
                }
                Class<?> clazz = ci.loadClass();
                try {
                    Command instance = (Command) clazz.getDeclaredConstructor().newInstance();
                    if (cr.containsCommand(instance.getName())) {
                        continue;
                    }
                    cr.registry(instance);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate command: " + clazz.getName(), e);
                }
            }
        }
    }

}
