package org.example.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class PooledConnection implements InvocationHandler {
    private final Connection conn;
    private final ConnectionPool pool;
    volatile boolean isClosed = false;

    public PooledConnection(Connection conn, ConnectionPool pool) {
        this.conn = conn;
        this.pool = pool;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("close".equals(name)) {
            if (!isClosed) {
                isClosed = true;
                pool.release(conn);
            }
            return null;
        }
        if ("isClosed".equals(name)) {
            return isClosed || conn.isClosed();
        }
        if (isClosed) {
            throw new SQLException("Connection is closed");
        }
        try {
            return method.invoke(conn, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

    }

    public static Connection wrap(Connection conn, ConnectionPool pool) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                new PooledConnection(conn, pool)
        );
    }
}
