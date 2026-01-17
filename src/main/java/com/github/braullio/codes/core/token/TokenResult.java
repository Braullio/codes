package com.github.braullio.codes.core.token;

class TokenResult {

    private final String accessToken;
    private final long expiresAt;

    TokenResult(String accessToken, long expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
