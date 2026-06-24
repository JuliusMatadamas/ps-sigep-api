package com.sigep.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sigep.utils.UUIDvs7;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class MetaResponseDTO {
	private String status;
	private int statusCode;
	private final String timestamp = LocalDateTime.now().toString();
	private final String transactionID = UUIDvs7.randomUUID();
	private String devMessage;
    private String message;
    private Object errorDetails;
    private Object upstreamError;
    private boolean rollback;
    private int retryAfter;
}
