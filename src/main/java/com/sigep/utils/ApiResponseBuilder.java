package com.sigep.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sigep.constants.AppConstants;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;

/**
 * Utilitario que centraliza la construccion de respuestas HTTP exitosas
 * estandarizadas para toda la aplicacion. Aplica el principio DRY al
 * eliminar la duplicacion del ensamblado de MetaResponseDTO y ApiResponseDTO
 * en los controladores.
 *
 * Las respuestas de error son responsabilidad exclusiva del
 * AppExceptionsHandler mediante el lanzamiento de excepciones de negocio.
 */
public final class ApiResponseBuilder {

    private ApiResponseBuilder() {
        throw new IllegalStateException("Clase de utilidad, no debe ser instanciada.");
    }

    /**
     * Construye una respuesta HTTP 200 OK con datos.
     *
     * @param data    Cuerpo de datos a incluir en la respuesta.
     * @param message Mensaje descriptivo del resultado exitoso.
     * @return ResponseEntity envolviendo un ApiResponseDTO estandarizado.
     */
    public static ResponseEntity<ApiResponseDTO> ok(Object data, String message) {
        return buildSuccessResponse(HttpStatus.OK, data, message);
    }

    /**
     * Construye una respuesta HTTP 201 CREATED con datos del recurso creado.
     *
     * @param data    Recurso recien creado a incluir en la respuesta.
     * @param message Mensaje descriptivo del resultado exitoso.
     * @return ResponseEntity envolviendo un ApiResponseDTO estandarizado.
     */
    public static ResponseEntity<ApiResponseDTO> created(Object data, String message) {
        return buildSuccessResponse(HttpStatus.CREATED, data, message);
    }

    /**
     * Construye una respuesta HTTP 200 OK sin cuerpo de datos, util para
     * operaciones de eliminacion o acciones que no retornan informacion.
     *
     * @param message Mensaje descriptivo del resultado exitoso.
     * @return ResponseEntity envolviendo un ApiResponseDTO estandarizado.
     */
    public static ResponseEntity<ApiResponseDTO> ok(String message) {
        return buildSuccessResponse(HttpStatus.OK, null, message);
    }

    private static ResponseEntity<ApiResponseDTO> buildSuccessResponse(
            HttpStatus httpStatus,
            Object data,
            String message
    ) {
        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(AppConstants.SUCCESS)
                .statusCode(httpStatus.value())
                .message(message)
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .data(data)
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }
}
