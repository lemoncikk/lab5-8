package org.example.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.model.Context;
import org.example.model.MusicBand;

public class SumCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var sum = ctx.getStore().stream().mapToLong(MusicBand::getNumberOfParticipants).sum();
        return new CommandResult(String.format("%d", sum), null, false);
    }

    @Override
    public String getDescription() {
        return "Выводит суммарно число участников в группах";
    }

    @Override
    public String getName() {
        return "sum_of_number_of_participants";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
