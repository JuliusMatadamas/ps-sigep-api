package com.sigep.exceptions;

/**
 * Excepcion de negocio lanzada cuando se intenta registrar o actualizar
 * un recurso que ya existe en el sistema, provocando un conflicto de
 * unicidad. Es mapeada por el AppExceptionsHandler a una respuesta
 * HTTP 409 CONFLICT estandarizada.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
