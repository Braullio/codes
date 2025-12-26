package com.github.braullio.codes.core.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.braullio.codes.core.log.annotation.LogIgnore;
import com.github.braullio.codes.core.log.annotation.LogMask;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/* Serializa LogEvent para JSON de forma segura */
final class SafeLogJsonSerializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ConcurrentHashMap<Class<?>, Field[]> CACHE = new ConcurrentHashMap<Class<?>, Field[]>();

    private SafeLogJsonSerializer() {
    }

    static String toJson(LogEvent event) {
        ObjectNode root = MAPPER.createObjectNode();

        root.put("timestamp", event.getTimestamp());
        root.put("traceId", event.getTraceId());
        root.put("correlationId", event.getCorrelationId());
        root.put("source", event.getSource().getCode());
        root.put("sourceDescription", event.getSource().getDescription());
        root.put("eventType", event.getEventType().name());

        if (event.getMessage() != null) {
            root.put("message", event.getMessage());
        }
        if (event.getCounterSuccess() != null) {
            root.put("countSuccess", event.getCounterSuccess());
        }
        if (event.getCounterError() != null) {
            root.put("countError", event.getCounterError());
        }
        if (event.getSize() > 0) {
            root.put("size", event.getSize());
        }
        if (event.getDurationMs() != null) {
            root.put("durationMs", event.getDurationMs());
        }
        if (event.getError() != null) {
            root.put("error", event.getError().getClass().getName());
            root.put("errorMessage", event.getError().getMessage());
        }
        if (event.getDetail() != null) {
            root.set("detail", serializeDetail(event.getDetail()));
        }

        try {
            return MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"log_serialization_failed\"}";
        }
    }

    private static ObjectNode serializeDetail(Object obj) {
        ObjectNode node = MAPPER.createObjectNode();
        Field[] fields = CACHE.computeIfAbsent(obj.getClass(), SafeLogJsonSerializer::resolveFields);

        for (Field f : fields) {
            try {
                if (f.getAnnotation(LogIgnore.class) != null) {
                    continue;
                }
                Object value = f.get(obj);
                if (value == null) {
                    continue;
                }

                LogMask mask = f.getAnnotation(LogMask.class);
                if (mask != null) {
                    node.put(f.getName(), mask(String.valueOf(value), mask.visibleLast()));
                } else {
                    node.put(f.getName(), String.valueOf(value));
                }
            } catch (Exception ignored) {
            }
        }
        return node;
    }

    private static Field[] resolveFields(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
        }
        return fields;
    }

    private static String mask(String value, int visibleLast) {
        if (value.length() <= visibleLast) {
            return "***";
        }
        int maskSize = value.length() - visibleLast;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maskSize; i++) {
            sb.append('*');
        }
        sb.append(value.substring(maskSize));
        return sb.toString();
    }
}
