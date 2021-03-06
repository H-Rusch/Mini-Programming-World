package model.territory.exceptions;

/** Exception to be thrown, when the actor tries to pick up a present on a tile where no present is available. */
public class NoPresentOnTileException extends ActorException {

    public NoPresentOnTileException(String message) {
        super(message);
    }
}
