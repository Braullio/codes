package com.github.braullio.codes.logImpl;

import com.github.braullio.codes.core.log.annotation.LogIgnore;
import com.github.braullio.codes.core.log.annotation.LogMask;

class TestRequestDTO {

    private String nome;

    @LogMask
    private String cpf;

    @LogIgnore
    private String apiToken;

    private TestRequestDTO(String nome, String cpf, String apiToken) {
        this.nome = nome;
        this.cpf = cpf;
        this.apiToken = apiToken;
    }

    static TestRequestDTO of(String nome, String cpf, String apiToken) {
        return new TestRequestDTO(nome, cpf, apiToken);
    }
}
