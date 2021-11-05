package exceptions;

/**
 * Exception to be thrown, when the actor tries to put a present down at a position where there already is a present on
 * the ground.
 */
public class PresentAlreadyOnTileException extends RuntimeException {

    public PresentAlreadyOnTileException(String message) {
        super(message);
    }
}
