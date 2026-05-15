package org.example;

public class SessionContext {
    private static final ThreadLocal<Integer> currentUser = new ThreadLocal<>();

    public static void set(int userId) {
        currentUser.set(userId);
    }

    public static int get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}