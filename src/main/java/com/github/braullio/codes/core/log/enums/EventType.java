package com.github.braullio.codes.core.log.enums;

/* Classificação do evento */
public enum EventType {

    /* Ciclo de execução */
    PROCESS_START,
    PROCESS_END,
    PROCESS_ERROR,

    /* Jobs / Lote */
    JOB_START,
    JOB_PROGRESS,
    JOB_END,
    JOB_ERROR,

    /* HTTP / Integrações */
    HTTP_REQUEST,
    HTTP_RESPONSE,
    HTTP_ERROR,

    /* Validação / Negócio */
    VALIDATION_ERROR,
    BUSINESS_RULE_VIOLATION,

    /* Segurança */
    SECURITY_EVENT,

    /* Fallback */
    GENERIC_EVENT
}
