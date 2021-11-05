package exceptions;

/**
 * Exception to be thrown, when a tile which can not be entered is attempted to be entered.
 */
public class TileBlockedException extends RuntimeException {

    public TileBlockedException(String message) {
        super(message);
    }
}
