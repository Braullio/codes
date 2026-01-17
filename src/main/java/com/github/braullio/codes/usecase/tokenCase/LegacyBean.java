package com.github.braullio.codes.usecase.tokenCase;

import com.github.braullio.codes.core.token.TokenService;
import javax.inject.Inject;
import javax.inject.Named;

public class LegacyBean {

    @Inject
    @Named("tokenV1")
    private TokenService tokenService;

    public void executar() throws Exception {
        String token = tokenService.obterToken();
        System.out.println("Token usado (CDI): " + token);
    }
}