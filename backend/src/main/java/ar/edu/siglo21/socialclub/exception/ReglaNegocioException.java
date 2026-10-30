//se lanza cuando una regla rechaza la operación.
package ar.edu.siglo21.socialclub.exception;

public class ReglaNegocioException extends RuntimeException {

    private final String regla;

    public ReglaNegocioException(String regla, String mensaje) {
        super(mensaje);
        this.regla = regla;
    }

    public String getRegla() {
        return regla;
    }
}
