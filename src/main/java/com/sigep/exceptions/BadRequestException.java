package com.sigep.exceptions;

/**
 * Excepcion de negocio lanzada cuando los datos de entrada de una peticion
 * no cumplen con las reglas de validacion sintactica o semantica. Es mapeada
 * por el AppExceptionsHandler a una respuesta HTTP 400 BAD_REQUEST
 * estandarizada.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
