package com.sigep.config;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String tracingId = MDC.get("tracingId");

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status("FAILURE")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("No autorizado. Credenciales inválidas o ausentes.")
                .devMessage(authException.getMessage())
                .errorDetails(Map.of("path", request.getRequestURI()))
                .build();

        if (tracingId != null) {
            meta.setTransactionID(tracingId);
        }

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
