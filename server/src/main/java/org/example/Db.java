package org.example;

import org.example.db.ConnectionPool;
import org.example.model.Album;
import org.example.model.Coordinates;
import org.example.model.MusicBand;
import org.example.model.MusicGenre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Db {
    final private ConnectionPool pool;
    private static volatile Db instance = null;

    static public Db getInstance() {
        if (instance == null) {
            synchronized (Db.class) {
                if (instance == null) {
                    instance = new Db(new ConnectionPool(
                            "jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "1111",
                            10, 5000));
                }
            }
        }
        return instance;
    }

    private Db() {
        pool = new ConnectionPool(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "1111",
                10,
                5000);
    }
    public Db(ConnectionPool pool) {
        this.pool = pool;
    }

    public int addMusicBand(MusicBand mb, int userId) throws SQLException, InterruptedException {
        String sqlCoordinates = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        String sqlAlbum = "INSERT INTO album (name, tracks) VALUES (?, ?) RETURNING id";
        String sqlBand = "INSERT INTO band (user_id, name, coordinates_id, creation_date, number_of_participants, " +
                "singles_count, albums_count, genre_id, best_album_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (var conn = pool.get()) {
            conn.setAutoCommit(false);
            try {
                // 1. Сохраняем координаты (обязательное поле)
                int coordinatesId;
                try (var psCoords = conn.prepareStatement(sqlCoordinates)) {
                    psCoords.setLong(1, mb.getCoordinates().getX());
                    psCoords.setInt(2, mb.getCoordinates().getY());
                    try (var rs = psCoords.executeQuery()) {
                        rs.next();
                        coordinatesId = rs.getInt("id");
                    }
                }

                // 2. Сохраняем жанр (если есть)
                Integer genreId = null;
                if (mb.getGenre() != null) {
                    // Сначала пытаемся вставить (игнорируем если уже есть)
                    try (var psGenreInsert = conn.prepareStatement(
                            "INSERT INTO genre (name) VALUES (?) ON CONFLICT (name) DO NOTHING")) {
                        psGenreInsert.setString(1, mb.getGenre().name());
                        psGenreInsert.executeUpdate();
                    }

                    // Получаем ID жанра
                    try (var psGenreSelect = conn.prepareStatement("SELECT id FROM genre WHERE name = ?")) {
                        psGenreSelect.setString(1, mb.getGenre().name());
                        try (var rs = psGenreSelect.executeQuery()) {
                            if (rs.next()) {
                                genreId = rs.getInt("id");
                            }
                        }
                    }
                }

                // 3. Сохраняем лучший альбом (если есть)
                Integer bestAlbumId = null;
                if (mb.getBestAlbum() != null) {
                    try (var psAlbum = conn.prepareStatement(sqlAlbum)) {
                        psAlbum.setString(1, mb.getBestAlbum().getName());
                        psAlbum.setLong(2, mb.getBestAlbum().getTracks());
                        try (var rs = psAlbum.executeQuery()) {
                            rs.next();
                            bestAlbumId = rs.getInt("id");
                        }
                    }
                }

                // 4. Сохраняем саму группу
                int bandId;
                try (var psBand = conn.prepareStatement(sqlBand)) {
                    psBand.setInt(1, userId);
                    psBand.setString(2, mb.getName());
                    psBand.setInt(3, coordinatesId);
                    psBand.setTimestamp(4, Timestamp.from(mb.getCreationDate().toInstant()));
                    psBand.setLong(5, mb.getNumberOfParticipants());
                    psBand.setLong(6, mb.getSinglesCount());

                    if (mb.getAlbumsCount() != null) {
                        psBand.setInt(7, mb.getAlbumsCount());
                    } else {
                        psBand.setNull(7, Types.INTEGER);
                    }

                    if (genreId != null) {
                        psBand.setInt(8, genreId);
                    } else {
                        psBand.setNull(8, Types.INTEGER);
                    }

                    if (bestAlbumId != null) {
                        psBand.setInt(9, bestAlbumId);
                    } else {
                        psBand.setNull(9, Types.INTEGER);
                    }

                    try (var rs = psBand.executeQuery()) {
                        rs.next();
                        bandId = rs.getInt("id");
                    }
                }

                conn.commit();
                return bandId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void addUser(String login, String hash) throws SQLException, InterruptedException {
        String sql = "INSERT INTO person (login, hash) VALUES (?, ?)";
        try(var conn = pool.get()) {
            conn.setAutoCommit(false);
            try (var st = conn.prepareStatement(sql)) {
                st.setString(1, login);
                st.setString(2, hash);
                st.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Optional<User> getUserById(int id) throws SQLException, InterruptedException {
        String sql = "SELECT id, login, hash FROM person WHERE id = ?";
        try (var conn = pool.get();
             var st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (var rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<User> getUserByLogin(String login) throws SQLException, InterruptedException {
        String sql = "SELECT id, login, hash FROM person WHERE login = ?";

        try (var conn = pool.get();
             var st = conn.prepareStatement(sql)) {
            st.setString(1, login);
            try (var rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        }
    }
    public Optional<MusicBand> getMusicBandById(int id) throws SQLException, InterruptedException {
        String sql = """
            SELECT b.id, b.user_id, b.name, b.creation_date, 
                   b.number_of_participants, b.singles_count, b.albums_count,
                   c.x, c.y,
                   g.id as genre_id, g.name as genre_name,
                   a.id as album_id, a.name as album_name, a.tracks
            FROM band b
            JOIN coordinates c ON b.coordinates_id = c.id
            LEFT JOIN genre g ON b.genre_id = g.id
            LEFT JOIN album a ON b.best_album_id = a.id
            WHERE b.id = ?
            """;

        try (var conn = pool.get();
             var st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (var rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMusicBand(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<MusicBand> getMusicBandsByUserId(int userId) throws SQLException, InterruptedException {
        String sql = """
            SELECT b.id, b.user_id, b.name, b.creation_date, 
                   b.number_of_participants, b.singles_count, b.albums_count,
                   c.x, c.y,
                   g.id as genre_id, g.name as genre_name,
                   a.id as album_id, a.name as album_name, a.tracks
            FROM band b
            JOIN coordinates c ON b.coordinates_id = c.id
            LEFT JOIN genre g ON b.genre_id = g.id
            LEFT JOIN album a ON b.best_album_id = a.id
            WHERE b.user_id = ?
            ORDER BY b.id
            """;

        List<MusicBand> bands = new ArrayList<>();
        try (var conn = pool.get();
             var st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    bands.add(mapResultSetToMusicBand(rs));
                }
            }
        }
        return bands;
    }

    public boolean removeById(int id, int userId) throws SQLException, InterruptedException {
        String sql = "DELETE FROM band WHERE id = ? AND user_id = ?";
        try (var conn = pool.get(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateMusicBand(int id, MusicBand mb, int userId) throws SQLException, InterruptedException {
        String sqlBand = "UPDATE band SET name=?, coordinates_id=?, creation_date=?, " +
                "number_of_participants=?, singles_count=?, albums_count=?, genre_id=?, best_album_id=? " +
                "WHERE id=? AND user_id=?";
        String sqlCoords = "UPDATE coordinates SET x=?, y=? WHERE id=?";
        String sqlAlbum = "UPDATE album SET name=?, tracks=? WHERE id=?";

        try (var conn = pool.get()) {
            conn.setAutoCommit(false);
            try {
                // 1. Получаем старые ID связей
                int coordsId = getRelationId(conn, "coordinates_id", id);
                int albumId = getRelationId(conn, "best_album_id", id);

                // 2. Обновляем координаты (всегда, даже если не менялись)
                try (var ps = conn.prepareStatement(sqlCoords)) {
                    ps.setLong(1, mb.getCoordinates().getX());
                    ps.setInt(2, mb.getCoordinates().getY());
                    ps.setInt(3, coordsId);
                    ps.executeUpdate();
                }

                // 3. Обновляем альбом (если есть)
                if (mb.getBestAlbum() != null && albumId != -1) {
                    try (var ps = conn.prepareStatement(sqlAlbum)) {
                        ps.setString(1, mb.getBestAlbum().getName());
                        ps.setLong(2, mb.getBestAlbum().getTracks());
                        ps.setInt(3, albumId);
                        ps.executeUpdate();
                    }
                }

                // 4. Жанр: получаем ID (вставляем если нет)
                Integer genreId = null;
                if (mb.getGenre() != null) {
                    try (var ps = conn.prepareStatement(
                            "INSERT INTO genre (name) VALUES (?) ON CONFLICT (name) DO NOTHING")) {
                        ps.setString(1, mb.getGenre().name());
                        ps.executeUpdate();
                    }
                    try (var ps = conn.prepareStatement("SELECT id FROM genre WHERE name = ?")) {
                        ps.setString(1, mb.getGenre().name());
                        try (var rs = ps.executeQuery()) {
                            if (rs.next()) genreId = rs.getInt("id");
                        }
                    }
                }

                // 5. Обновляем саму группу
                try (var ps = conn.prepareStatement(sqlBand)) {
                    ps.setString(1, mb.getName());
                    ps.setInt(2, coordsId);
                    ps.setTimestamp(3, Timestamp.from(mb.getCreationDate().toInstant()));
                    ps.setLong(4, mb.getNumberOfParticipants());
                    ps.setLong(5, mb.getSinglesCount());
                    ps.setObject(6, mb.getAlbumsCount(), Types.INTEGER);
                    ps.setObject(7, genreId, Types.INTEGER);
                    ps.setObject(8, mb.getBestAlbum() != null ? albumId : null, Types.INTEGER);
                    ps.setInt(9, id);
                    ps.setInt(10, userId);

                    boolean updated = ps.executeUpdate() > 0;
                    conn.commit();
                    return updated;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private int getRelationId(Connection conn, String column, int bandId) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT " + column + " FROM band WHERE id = ?")) {
            ps.setInt(1, bandId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private MusicBand mapResultSetToMusicBand(ResultSet rs) throws SQLException {
        Coordinates coordinates = new Coordinates(
                rs.getLong("x"),
                rs.getInt("y")
        );

        // Жанр (может быть null)
        MusicGenre genre = null;
        int genreId = rs.getInt("genre_id");
        if (!rs.wasNull()) {
            String genreName = rs.getString("genre_name");
            genre = MusicGenre.valueOf(genreName);
        }

        // Лучший альбом (может быть null)
        Album bestAlbum = null;
        int albumId = rs.getInt("album_id");
        if (!rs.wasNull()) {
            String albumName = rs.getString("album_name");
            long tracks = rs.getLong("tracks");
            bestAlbum = new Album(albumName, tracks);
        }

        // Основная группа
        MusicBand mb = new MusicBand();
        mb.setId(rs.getInt("id"));
        mb.setOwnerId(rs.getInt("user_id"));
        mb.setName(rs.getString("name"));
        mb.setCoordinates(coordinates);
        mb.setCreationDate(rs.getTimestamp("creation_date").toInstant()
                .atZone(java.time.ZoneId.of("UTC")));
        mb.setNumberOfParticipants(rs.getLong("number_of_participants"));
        mb.setSinglesCount(rs.getLong("singles_count"));

        int albumsCount = rs.getInt("albums_count");
        if (!rs.wasNull()) {
            mb.setAlbumsCount(albumsCount);
        }

        mb.setGenre(genre);
        mb.setBestAlbum(bestAlbum);

        return mb;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("hash")
        );
    }

    public void clearByUserId(int userId) throws SQLException, InterruptedException {
        String sql = "DELETE FROM band WHERE user_id = ?";
        try (var conn = pool.get(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public List<MusicBand> getAllMusicBands() throws SQLException, InterruptedException {
        String sql = """
            SELECT b.id, b.user_id, b.name, b.creation_date, 
                   b.number_of_participants, b.singles_count, b.albums_count,
                   c.x, c.y,
                   g.id as genre_id, g.name as genre_name,
                   a.id as album_id, a.name as album_name, a.tracks
            FROM band b
            JOIN coordinates c ON b.coordinates_id = c.id
            LEFT JOIN genre g ON b.genre_id = g.id
            LEFT JOIN album a ON b.best_album_id = a.id
            ORDER BY b.id
            """;

        List<MusicBand> bands = new ArrayList<>();
        try (var conn = pool.get();
             var st = conn.prepareStatement(sql);
             var rs = st.executeQuery()) {
            while (rs.next()) {
                bands.add(mapResultSetToMusicBand(rs));
            }

        }
        return bands;
    }
}
