package com.github.braullio.codes.core.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.braullio.codes.core.log.annotation.LogIgnore;
import com.github.braullio.codes.core.log.annotation.LogMask;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/* Serializa LogEvent para JSON de forma segura */
@SuppressWarnings("all")
final class SafeLogJsonSerializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ConcurrentHashMap<Class<?>, Field[]> CACHE = new ConcurrentHashMap<Class<?>, Field[]>();

    private SafeLogJsonSerializer() {
    }

    static String toJson(LogEvent event) {
        ObjectNode json = MAPPER.createObjectNode();

        json.put("timestamp", event.getTimestamp());
        json.put("traceId", event.getTraceId());
        json.put("correlationId", event.getCorrelationId());
        json.put("source", event.getSource().getCode());
        json.put("sourceDescription", event.getSource().getDescription());
        json.put("eventType", event.getEventType().name());

        putIfNotNull(json, "message", event.getMessage());
        putIfNotNull(json, "countSuccess", event.getCounterSuccess());
        putIfNotNull(json, "countError", event.getCounterError());
        putIfNotNull(json, "durationMs", event.getDurationMs());
        putIfNotNull(json, "size", event.getSize());

        if (event.getError() != null) {
            putIfNotNull(json, "error", event.getError().getClass().getName());
            putIfNotNull(json, "errorMessage", event.getError().getMessage());
        }
        if (event.getDetail() != null) {
            json.set("detail", serializeDetail(event.getDetail()));
        }

        try {
            return MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"log_serialization_failed\"}";
        }
    }

    public static void putIfNotNull(ObjectNode node, String field, Object value) {
        if (value == null) return;

        if (value instanceof String) {
            node.put(field, (String) value);
        } else if (value instanceof Integer) {
            node.put(field, (Integer) value);
        } else if (value instanceof Long) {
            node.put(field, (Long) value);
        } else if (value instanceof Boolean) {
            node.put(field, (Boolean) value);
        } else {
            node.set(field, MAPPER.valueToTree(value));
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
