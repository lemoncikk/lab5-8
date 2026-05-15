package org.example;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.AuthException;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class TokenManager {
    private static class TokenGenerator {
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();
        private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

        private static final int TOKEN_BYTES = 32;

        public static String generate() {
            byte[] bytes = new byte[TOKEN_BYTES];
            SECURE_RANDOM.nextBytes(bytes);
            return ENCODER.encodeToString(bytes);
        }
    }

    private record TokenEntry(User user, long expire) {
        private static final long EXPIRE_TIME_MS = 10*3600*1000;
        public boolean isExpired() {
            return expire + EXPIRE_TIME_MS < System.currentTimeMillis();
        }
    }
    private final ConcurrentHashMap<String,TokenEntry> store = new ConcurrentHashMap<>();
    private final Db db = Db.getInstance();

    public void register(String login, String hash) throws SQLException, InterruptedException {
        try {
            db.addUser(login, hash);
        } catch (SQLException | InterruptedException e) {
            log.warn("Error while registration.", e);
            throw new AuthException("Db error");
        }
    }

    public String add(String login, String hash) throws AuthException {
        try {
            var user = db.getUserByLogin(login);
            if (user.isPresent() && user.get().getHash().equals(hash)) {
                String token = TokenGenerator.generate();
                store.putIfAbsent(token, new TokenEntry(user.get(), System.currentTimeMillis()));
                return token;
            }
        } catch (SQLException | InterruptedException e) {
            log.warn("Error while getting token for user.", e);
            throw new AuthException("User isn't registered or db error");
        }
        throw new AuthException("User isn't registered or db error");
    }

    public String add(int id, String hash) {
        try {
            var user = db.getUserById(id);
            if (user.isPresent() && java.security.MessageDigest.isEqual(
                    user.get().getHash().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    hash.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                String token;
                do {
                    token = TokenGenerator.generate();
                } while (store.putIfAbsent(token, new TokenEntry(user.get(), System.currentTimeMillis())) == null);
                return token;
            }
        } catch (SQLException | InterruptedException e) {
            log.warn("Error while getting token for user.", e);
            throw new AuthException("User isn't registered or db error");
        }
        throw new AuthException("User isn't registered or db error");
    }
    public Optional<User> get(String token) {
        if (token == null) return Optional.empty();
        return Optional.ofNullable(
                store.computeIfPresent(token, (k, entry) -> {
                    if (entry.isExpired()) return null;
                    return entry;
                })
        ).map(TokenEntry::user);
    }
}
