package com.sigep.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class StructuredLog {

    private String level;
    private String logType;
    private String sourceIP;
    private String status;
    private String message;
    private String logOrigin;
    private String timestamp;
    private String tracingId;
    private String hostname;
    private String eventType;
    private ApplicationMetadata application;
    private MeasurementMetadata measurement;
    private Map<String, Object> additionalInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ApplicationMetadata {
        private String name;
        private String version;
        private String env;
        private String kind;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class MeasurementMetadata {
        private String method;
        private String elapsedTime;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = JsonMapper.builder().build();
            return mapper.writeValueAsString(this);
        } catch (JacksonException e) {
            return "{\"message\":\"Error serializing log to JSON\"}";
        }
    }
}
