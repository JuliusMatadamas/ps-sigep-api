package com.sigep.exceptions;

/**
 * Excepcion de negocio lanzada cuando una operacion de consulta no
 * arroja resultados. Es mapeada por el AppExceptionsHandler a una
 * respuesta HTTP 204 NO_CONTENT estandarizada, preservando la
 * semantica original del sistema.
 */
public class NoContentException extends RuntimeException {

    public NoContentException(String message) {
        super(message);
    }

    public NoContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
