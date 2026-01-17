package com.github.braullio.codes.core.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.inject.Named;

@Named("tokenV1")
@Service("tokenV1")
public class TokenV1Service extends TokenAbstract implements TokenService {

    @Value("${local.token.endpoint}")
    private String tokenUrl;

    @Value("${local.token.username}")
    private String username;

    @Value("${local.token.password}")
    private String password;

    private volatile String cachedToken;
    private volatile long expiresAt;

    @Override
    protected String getTokenUrl() { return tokenUrl; }

    @Override
    protected String getUsername() { return username; }

    @Override
    protected String getPassword() { return password; }

    @Override
    public String obterToken() throws Exception {
        long agora = System.currentTimeMillis();
        if (cachedToken != null && agora < expiresAt) {
            return cachedToken;
        }

        synchronized (this) {
            agora = System.currentTimeMillis();
            if (cachedToken == null || agora >= expiresAt) {
                TokenResult result = requestToken();
                this.cachedToken = result.getAccessToken();
                this.expiresAt = result.getExpiresAt();
            }
            return cachedToken;
        }
    }
}
