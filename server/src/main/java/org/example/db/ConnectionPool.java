package org.example.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
public class ConnectionPool {
    private final BlockingQueue<Connection> idle = new LinkedBlockingQueue<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String url, login, password;
    private final int maxSize;
    private final int timeoutMs;

    public ConnectionPool(String url, String login, String password, int maxSize, int timeoutMs) {
        this.url = url;
        this.login = login;
        this.password = password;
        this.maxSize = maxSize;
        this.timeoutMs = timeoutMs;
    }

    public Connection get() throws SQLException, InterruptedException {
        while (true) {
            Connection conn = idle.poll();
            if (conn != null) {
                if (conn.isClosed() || !conn.isValid(2)) {
                    counter.decrementAndGet();
                    continue;
                }
            }
            if (counter.incrementAndGet() <= maxSize) {
                try {
                    return PooledConnection.wrap(DriverManager.getConnection(url, login, password), this);
                } catch (SQLException e) {
                    counter.decrementAndGet();
                    throw e;
                }

            }
            counter.decrementAndGet();
            conn = idle.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new SQLException("Timeout waiting for db connection");
            }
            if (conn.isValid(2)) return conn;
            conn.close();
            counter.decrementAndGet();
        }
    }

    public void release(Connection conn) {
        try {
            if (conn == null) return;
            conn.setAutoCommit(true);
            conn.clearWarnings();
            if (conn.isClosed() || !conn.isValid(2)) {
                conn.close();
                counter.decrementAndGet();
                return;
            }
            idle.offer(conn);
        } catch (SQLException e) {
            log.error("", e);
            try { conn.close(); } catch (SQLException ignored) {}
            counter.decrementAndGet();
        }
    }
}
