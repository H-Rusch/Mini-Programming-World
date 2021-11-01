package exceptions;

public class NoPresentInBasketException extends RuntimeException {

    public NoPresentInBasketException(String message) {
        super(message);
    }
}
