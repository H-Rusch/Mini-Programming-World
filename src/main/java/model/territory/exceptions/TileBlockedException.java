package model.territory.exceptions;

/** Exception to be thrown, when a tile which can not be entered is attempted to be entered. */
public class TileBlockedException extends ActorException {

    public TileBlockedException(String message) {
        super(message);
    }
}
