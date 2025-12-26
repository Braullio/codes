package com.github.braullio.codes.core.log;

import com.github.braullio.codes.core.log.enums.EventType;
import com.github.braullio.codes.core.log.enums.LogSource;

import java.util.Map;

@SuppressWarnings("all")
public final class LogEventBuilder {
    final long timestamp = System.currentTimeMillis();
    final String traceId;
    final String correlationId;
    final LogSource source;
    final EventType eventType;
    String message;
    Object detail;
    Throwable error;
    Long counterSuccess;
    Long counterError;
    Long durationMs;
    Long size;
    Map<String, Object> extras;
    LogLevel levelOverride;

    LogEventBuilder(String traceId, String correlationId, LogSource source, EventType eventType) {
        this.traceId = traceId;
        this.correlationId = correlationId;
        this.source = source;
        this.eventType = eventType;
    }

    public LogEventBuilder message(String message) {
        this.message = message;
        return this;
    }

    public LogEventBuilder detail(Object detail) {
        this.detail = detail;
        return this;
    }

    public LogEventBuilder error(Throwable error) {
        this.error = error;
        this.countError();
        return this;
    }

    public LogEventBuilder countSuccess() {
        this.counterSuccess = addCounter(this.counterSuccess);
        return this;
    }

    public LogEventBuilder countError() {
        this.counterError = addCounter(this.counterError);
        return this;
    }

    public LogEventBuilder durationMs(long durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    public LogEventBuilder size(long size) {
        this.size = size;
        return this;
    }

    public LogEventBuilder extras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    public LogEventBuilder warn() {
        this.levelOverride = LogLevel.WARN;
        return this;
    }

    public LogEventBuilder info() {
        this.levelOverride = LogLevel.INFO;
        return this;
    }

    public LogEvent build() {
        return new LogEvent(this);
    }

    public void commit() {
        new LogEvent(this).commit();
    }

    private Long addCounter(Long counter) {
        if (counter == null) {
            counter = 0L;
        }
        return ++counter;
    }
}
