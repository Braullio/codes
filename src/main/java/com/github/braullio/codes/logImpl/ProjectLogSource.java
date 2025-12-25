package com.github.braullio.codes.logImpl;

import com.github.braullio.codes.core.log.enums.LogSource;

/* Origens de log específicas do projeto */
public enum ProjectLogSource implements LogSource {
    CREATE_USER("CREATE_USER", "Criando usuario no sistema"),
    API_CLIENT("API_CLIENT", "Erro ao comunicar com a API"),
    GENERIC_JOB("GENERIC_JOB", "Job genérico");

    private final String code;
    private final String description;

    ProjectLogSource(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
