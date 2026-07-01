package com.sigep.exceptions;

/**
 * Excepcion de negocio lanzada cuando un recurso solicitado no existe
 * en el sistema. Es mapeada por el AppExceptionsHandler a una respuesta
 * HTTP 404 NOT_FOUND estandarizada.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
