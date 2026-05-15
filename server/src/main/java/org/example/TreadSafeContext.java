package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.command.CommandRegistry;
import org.example.db.ConnectionPool;
import org.example.exceptions.AuthException;
import org.example.model.MusicBand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
@Slf4j
public class TreadSafeContext implements Context{
    private ConcurrentHashMap<Integer, MusicBand> store = new ConcurrentHashMap<>();
    public final TokenManager TkManager;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Db db = Db.getInstance();
    @Getter
    public final CommandRegistry registry = new CommandRegistry();
    @Getter
    @Setter
    private String[] args = null;
    public TreadSafeContext() throws SQLException, InterruptedException{
        TkManager = new TokenManager();
        loadFromDb();
    }

    public Optional<MusicBand> getById(int id) {
        rwLock.readLock().lock();
        try {
            return Optional.ofNullable(store.get(id));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public ArrayList<MusicBand> getAll() {
        rwLock.readLock().lock();
        try {
            return new ArrayList<>(store.values());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public ArrayList<MusicBand> getAll(int limit) {
        rwLock.readLock().lock();
        try {
            return store.values().stream().limit(limit).collect(Collectors.toCollection(ArrayList::new));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void add(MusicBand mb) throws SQLException, InterruptedException {
        int newId = db.addMusicBand(mb, SessionContext.get());
        rwLock.writeLock().lock();
        try {
            mb.setOwnerId(SessionContext.get());
            store.put(newId, mb);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private boolean isOwner(int bandId, int userId) {
        rwLock.readLock().lock();
        try {
            return store.get(bandId).getOwnerId() == userId;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void removeById(int id) throws SQLException, InterruptedException {
        if (!isOwner(id, SessionContext.get())) {
            throw new AuthException("You can remove only your bands");
        }
        db.removeById(id, SessionContext.get());
        rwLock.writeLock().lock();
        try {
            store.remove(id);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void insertAt(int index, MusicBand mb) throws SQLException, InterruptedException {
        add(mb);
    }

    public void sort(boolean reverse_order) {

    }

    public void sort() {
        sort(false);
    }

    public void update(int id, MusicBand mb) throws SQLException, InterruptedException{
        if (!isOwner(id, SessionContext.get())) {
            throw new AuthException("You can update only your bands");
        }
        db.updateMusicBand(id, mb,SessionContext.get());
        rwLock.writeLock().lock();
        try {
            store.put(id, mb);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void clear() throws SQLException, InterruptedException {
        int userId = SessionContext.get();
        db.clearByUserId(userId);
        rwLock.writeLock().lock();
        try {
            store.entrySet().removeIf(entry -> entry.getValue().getOwnerId() == userId);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void loadFromDb() throws SQLException, InterruptedException{
        rwLock.writeLock().lock();
        try {
            var allBands = db.getAllMusicBands(); // Метод в Db, который делает SELECT * FROM band
            for (var mb : allBands) {
                store.put(mb.getId(), mb);
            }
            log.info("Loaded {} bands from database", store.size());
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
