package model.territory.exceptions;

public abstract class ActorException extends RuntimeException {

    protected ActorException(String message) {
        super(message);
    }
}
