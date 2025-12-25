package com.github.braullio.codes.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Único ponto que escreve o log */
final class LogDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger("STRUCTURED_LOG");

    private LogDispatcher() {
    }

    static void dispatch(LogEvent event) {
        if (event == null) {
            return;
        }

        String json = SafeLogJsonSerializer.toJson(event);
        LogLevel level = resolveLevel(event);
        switch (level) {
            case ERROR:
                LOGGER.error(json);
                break;
            case WARN:
                LOGGER.warn(json);
                break;
            case INFO:
            default:
                LOGGER.info(json);
        }
    }

    private static LogLevel resolveLevel(LogEvent event) {
        if (event.getError() != null) {
            return LogLevel.ERROR;
        }

        if (event.getEventType().name().endsWith("_ERROR")) {
            return LogLevel.ERROR;
        }

        if (event.getLevelOverride() != null) {
            return event.getLevelOverride();
        }

        return LogLevel.INFO;
    }
}
