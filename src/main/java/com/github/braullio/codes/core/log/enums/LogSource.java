package com.github.braullio.codes.core.log.enums;

/**
 * Contrato para identificar a origem do log.
 * Cada projeto pode definir seu próprio enum implementando esta interface.
 */
public interface LogSource {

    /* Código técnico da origem do log */
    String getCode();

    /* Descrição humana da origem do log */
    String getDescription();
}