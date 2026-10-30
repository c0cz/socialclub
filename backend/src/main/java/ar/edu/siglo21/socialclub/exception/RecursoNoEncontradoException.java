//se lanza cuando no existe el socio o la actividad.
package ar.edu.siglo21.socialclub.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}