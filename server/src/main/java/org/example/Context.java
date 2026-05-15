package org.example;

import org.example.command.CommandRegistry;
import org.example.model.MusicBand;
import org.example.requests.CommandRequest;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

public interface Context {
    CommandRegistry getRegistry();
    Optional<MusicBand> getById(int id);
    ArrayList<MusicBand> getAll();
    ArrayList<MusicBand> getAll(int limit);
    void add(MusicBand mb) throws Exception;
    void removeById(int id) throws Exception;
    void insertAt(int index, MusicBand mb) throws Exception;
    void sort(boolean reverse_order);
    void sort();
    void update(int id, MusicBand mb) throws Exception;
    String[] getArgs();
    void setArgs(String[] args);
    void clear() throws Exception;
}
