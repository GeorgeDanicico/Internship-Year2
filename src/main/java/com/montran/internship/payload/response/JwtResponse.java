package com.montran.internship.payload.response;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserResponse user;

    public JwtResponse(String accessToken, UserResponse userResponse) {
        this.token = accessToken;
        this.user = userResponse;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse userResponse) {
        this.user = userResponse;
    }
}
