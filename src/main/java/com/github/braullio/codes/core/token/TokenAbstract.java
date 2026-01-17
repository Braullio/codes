package com.github.braullio.codes.core.token;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

abstract class TokenAbstract {

    private static final String UTF_8 = "UTF-8";
    private static final String FIELD_EXPIRES_IN = "expires_in";
    private static final String FIELD_ACCESS_TOKEN = "access_token";
    private static final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";

    protected abstract String getTokenUrl();

    protected abstract String getUsername();

    protected abstract String getPassword();

    protected TokenResult requestToken() throws Exception {
        HttpResponse<String> response = Unirest.post(getTokenUrl())
                .header("Content-Type", HEADER_CONTENT_TYPE)
                .header("Authorization", "Basic " + basicAuth(getUsername(), getPassword()))
                .asString();

        int status = response.getStatus();
        String body = response.getBody();

        if (status != 200)
            throw new RuntimeException("Erro ao obter token. Status=" + status + " | Response=" + body);

        return buildTokenRequest(body);
    }

    private TokenResult buildTokenRequest(String json) {
        JSONObject obj = new JSONObject(json);

        if (!obj.has(FIELD_ACCESS_TOKEN))
            throw new RuntimeException("Resposta não contém '" + FIELD_ACCESS_TOKEN + "': " + json);
        if (!obj.has(FIELD_EXPIRES_IN))
            throw new RuntimeException("Resposta não contém '" + FIELD_EXPIRES_IN + "': " + json);

        long expiresInSegundos = obj.getLong(FIELD_EXPIRES_IN);
        long margem = (long) (expiresInSegundos * 1000 * 0.9);
        long expiresAt = System.currentTimeMillis() + margem;

        return new TokenResult(obj.getString(FIELD_ACCESS_TOKEN), expiresAt);
    }

    private String basicAuth(String user, String pass) throws Exception {
        String credentials = user + ":" + pass;
        return Base64.encodeBase64String(credentials.getBytes(UTF_8));
    }
}
