package com.github.braullio.codes.logImpl;

import com.github.braullio.codes.core.log.LogEvent;

import static com.github.braullio.codes.logImpl.ProjectLogSource.GENERIC_JOB;
import static com.github.braullio.codes.logImpl.ProjectLogSource.API_CLIENT;
import static com.github.braullio.codes.core.log.enums.EventType.*;

class LoggingTestRunner {

    public static void main(String[] args) {
        String cpf = "12345678900";
        String traceId = "TRACE-TEST-001";
        TestRequestDTO request = TestRequestDTO.of("João da Silva", cpf, "TOKEN-SECRETO-123");

        System.out.println("=== INICIO TESTE LOGGING ===");

        /* INFO simples (default) */
        LogEvent.builder(traceId, cpf, GENERIC_JOB, PROCESS_START).message("Processo iniciado com sucesso").commit();

        /* WARN explícito */
        LogEvent.builder(traceId, cpf, GENERIC_JOB, BUSINESS_RULE_VIOLATION).warn()
                .message("Campo opcional ausente, usando valor padrão").commit();

        /* INFO com detail */
        LogEvent.builder(traceId, cpf, API_CLIENT, HTTP_REQUEST).detail(request).commit();

        /* ERROR automático (exception) */
        try {
            simularErro();
        } catch (Exception e) {
            LogEvent.builder(traceId, cpf, API_CLIENT, HTTP_ERROR).detail(request).error(e).commit();
        }

        /* ERROR automático (exception) em um job */
        LogEvent.LogEventBuilder log = LogEvent.builder(traceId, cpf, GENERIC_JOB, HTTP_ERROR);
        try {
            String[] arrayDeStrings = {"Banana", "Maçã", "Pera"};
            for (String novaString : arrayDeStrings) {
                if ("Pera".equals(novaString)) {
                    simularErro();
                }
                log.countSuccess();
            }
        } catch (Exception e) {
            log.detail(request).error(e);
        } finally {
            log.commit();
        }

        /* INFO final com contador */
        LogEvent.LogEventBuilder logEvent = LogEvent.builder(traceId, cpf, GENERIC_JOB, PROCESS_END);
        String[] arrayDeStrings = {"Banana", "Maçã", "Pera"};
        for (String novaString : arrayDeStrings) {
            if ("Pera".equals(novaString)) {
                logEvent.countError();
            } else {
                logEvent.countSuccess();
            }
        }
        logEvent.commit();

        System.out.println("=== FIM TESTE LOGGING ===");
    }

    private static void simularErro() {
        throw new IllegalStateException("Erro simulado para teste de log");
    }
}