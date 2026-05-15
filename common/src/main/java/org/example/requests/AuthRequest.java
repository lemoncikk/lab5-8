package org.example.requests;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

public class AuthRequest implements NetworkRequest {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    @Getter
    private final String login;
    @Getter
    private final String password;
    @Getter
    private final AuthOperation operation;

    public enum AuthOperation {LOGIN, REGISTER}
    public AuthRequest(String login, String password, AuthOperation operation) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.operation = operation;
    }

    public AuthRequest(String login, String password, AuthOperation operation, UUID id) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.operation = operation;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
