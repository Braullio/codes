package com.github.braullio.codes.usecase.tokenCase;

import com.github.braullio.codes.core.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProcessoService {

    @Autowired
    @Qualifier("tokenV1")
    private TokenService tokenService;

    public void executar() throws Exception {
        String token = tokenService.obterToken();
        System.out.println("Token usado: " + token);
    }
}