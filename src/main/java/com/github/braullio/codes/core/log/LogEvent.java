package com.github.braullio.codes.core.log;

import com.github.braullio.codes.core.log.enums.EventType;
import com.github.braullio.codes.core.log.enums.LogSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Evento imutável de log.
 * O envio só ocorre ao chamar commit().
 */
@SuppressWarnings("all")
public final class LogEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private final long timestamp;
    private final String traceId;
    private final String correlationId;
    private final LogSource source;
    private final EventType eventType;
    private final String message;
    private final Object detail;
    private final Throwable error;
    private final Long counterSuccess;
    private final Long counterError;
    private final Long durationMs;
    private final Long size;
    private final Map<String, Object> extras;
    private final LogLevel levelOverride;

    LogEvent(LogEventBuilder b) {
        this.timestamp = b.timestamp;
        this.traceId = b.traceId;
        this.correlationId = b.correlationId;
        this.source = b.source;
        this.eventType = b.eventType;
        this.message = b.message;
        this.detail = b.detail;
        this.error = b.error;
        this.counterSuccess = b.counterSuccess;
        this.counterError = b.counterError;
        this.durationMs = b.durationMs;
        this.size = b.size;
        this.extras = b.extras == null ? null : Collections.unmodifiableMap(b.extras);
        this.levelOverride = b.levelOverride;
    }

    public void commit() {
        LogDispatcher.dispatch(this);
    }

    /* ===== Getters ===== */
    public long getTimestamp() { return timestamp; }
    public String getTraceId() { return traceId; }
    public String getCorrelationId() { return correlationId; }
    public LogSource getSource() { return source; }
    public EventType getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Object getDetail() { return detail; }
    public Throwable getError() { return error; }
    public Long getCounterSuccess() { return counterSuccess; }
    public Long getCounterError() { return counterError; }
    public Long getDurationMs() { return durationMs; }
    public Long getSize() { return size; }
    public Map<String, Object> getExtras() { return extras; }
    public LogLevel getLevelOverride() { return levelOverride; }

    /* ===== Builder ===== */
    public static LogEventBuilder builder(String traceId, String correlationId, LogSource source, EventType eventType) {
        return new LogEventBuilder(traceId, correlationId, source, eventType);
    }
}