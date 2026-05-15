package org.example;

import org.example.exceptions.AppException;
import org.example.requests.AuthRequest;
import org.example.responses.NetworkResponse;

import java.util.UUID;

public class SessionManager {
    private final UdpClient client;
    private String token;

    public SessionManager(UdpClient client) {
        this.client = client;
    }

    public void register(String login, String password) throws Exception {
        var res = client.send(new AuthRequest(login, password, AuthRequest.AuthOperation.REGISTER));
        if (res instanceof NetworkResponse.AuthSuccess s) {
            token = s.token();
        }
        if (res instanceof NetworkResponse.Error e) {
            throw new AppException(e.message());
        }
    }
    public void login(String login, String password) throws Exception {
        var res = client.send(new AuthRequest(login, password, AuthRequest.AuthOperation.LOGIN));
        if (res instanceof NetworkResponse.AuthSuccess s) {
            token = s.token();
        }
        if (res instanceof NetworkResponse.Error e) {
            throw new AppException(e.message());
        }
    }

    public String getToken() {
        if (token == null || token.isEmpty()) {
            throw new AppException("Please log in your account or register new");
        }
        return token;
    }
}
