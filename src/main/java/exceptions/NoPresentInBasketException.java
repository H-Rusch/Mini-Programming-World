package exceptions;

/**
 * Exception to be thrown, when the actor tries to put down a present while there is no present in the actor's basket.
 */
public class NoPresentInBasketException extends RuntimeException {

    public NoPresentInBasketException(String message) {
        super(message);
    }
}
