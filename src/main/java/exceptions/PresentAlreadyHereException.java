package exceptions;

public class PresentAlreadyHereException extends RuntimeException {

    public PresentAlreadyHereException(String message) {
        super(message);
    }
}
