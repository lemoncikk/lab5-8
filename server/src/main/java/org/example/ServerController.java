package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.command.CommandArgs;
import org.example.command.CommandLoader;
import org.example.command.CommandRegistry;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;
import org.example.exceptions.AuthException;
import org.example.exceptions.CommandNotFoundException;
import org.example.model.CycledStack;
import org.example.requests.AuthRequest;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

@Slf4j
public class ServerController implements org.example.Controller {
    private final TreadSafeContext ctx;
    private final ConcurrentHashMap<Integer, CycledStack<String>> history = new ConcurrentHashMap<>();
    private final static int HISTORY_SIZE = 12;
    private final CommandRegistry serverCommands = new CommandRegistry();

    public ServerController(TreadSafeContext ctx) {
        this.ctx = ctx;
        CommandLoader.load(this.ctx.registry, "org.example.commands");
        CommandLoader.load(serverCommands, "org.example.serverCommands");
    }

    public void validateToken(String token) {
        var user = ctx.TkManager.get(token);
        if (user.isPresent()) {
            SessionContext.set(user.get().getId());
        } else {
            throw new AuthException("Token expired, try to login");
        }
    }

    public String auth (String login, String hash) {
        try {
            return ctx.TkManager.add(login, hash);
        } catch (AuthException e) {
            throw e;
        }
    }
    public String register (String login, String hash) {
        try {
            ctx.TkManager.register(login, hash);
            return ctx.TkManager.add(login, hash);
        } catch (SQLException | InterruptedException e) {
            log.warn("Register error", e);
            throw new AuthException("Register error. Try next time");
        }
    }

    private CycledStack<String> getHistory() {
        int userId = SessionContext.get();
        return history.computeIfAbsent(userId, k -> new CycledStack<>(HISTORY_SIZE));
    }

    public CommandResult handle(String commandName, CommandArgs args) throws Exception {
        try {
            if (commandName.equals("history")) {
                var s = new StringBuilder();
                s.append("Введённые ранее команды:\n");
                for (var cmd : getHistory()) {
                    s.append(cmd).append("\n");
                }
                return new CommandResult(s.toString(), null, false);
            }
            if (!ctx.registry.containsCommand(commandName)) {
                throw new CommandNotFoundException("Команда не найдена");
            }
            getHistory().push(commandName);
            return ctx.registry.get(commandName).execute(ctx, args);
        } finally {
            SessionContext.clear();
        }
    }

    public CommandResult specialHandle(String commandName, CommandArgs args) throws Exception {
        return serverCommands.get(commandName).execute(ctx, args);
    }

    public CommandArgs getCommandModel(String commandName) throws AppException{
        if (commandName.equals("history")) {
            return null;
        }
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandNotFoundException("Команда не найдена");
        }
        return ctx.registry.get(commandName).getModel();
    }
}
