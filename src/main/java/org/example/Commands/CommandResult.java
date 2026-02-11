package org.example;

import org.example.model.MusicBand;

import java.util.ArrayList;

public sealed interface CommandResult permits CommandResult.Success, CommandResult.Failure {

    public record Success(String msg, ArrayList<MusicBand> data, boolean stopFlag) implements CommandResult {}

    public record Failure(String msg, boolean stopFlag) implements CommandResult {}

}
