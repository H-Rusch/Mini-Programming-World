package model;

public class Field {

    private FieldState state;

    public Field() {
        this.state = FieldState.EMPTY;
    }

    public Field(FieldState state) {
        this.state = state;
    }

    /* Getters and Setters */
    public FieldState getState() {
        return state;
    }

    public void setState(FieldState state) {
        this.state = state;
    }

    /**
     * Checks whether this field contains a present.
     */
    public boolean containsPresent() {
        return state == FieldState.PRESENT || state == FieldState.PRESENT_AND_CART;
    }

    /**
     * Checks whether this field contains a cart.
     */
    public boolean containsCart() {
        return state == FieldState.CART || state == FieldState.PRESENT_AND_CART;
    }
}
