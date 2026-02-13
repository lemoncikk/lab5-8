package org.example.Commands;

import org.example.model.MusicBand;

import java.util.ArrayList;

public record CommandResult(String msg, ArrayList<MusicBand> data, boolean stopFlag) {}
