package model;

/** Single tile of the territory. The tile can be in one of the TileStates and is initially empty. */
public class Tile {

    private TileState state;

    public Tile() {
        this.state = TileState.EMPTY;
    }

    public Tile(TileState state) {
        this.state = state;
    }

    /* Getters and Setters */
    public TileState getState() {
        return state;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    /** Checks whether this tile contains a present. */
    public boolean containsPresent() {
        return state == TileState.PRESENT || state == TileState.PRESENT_AND_CART;
    }

    /** Checks whether this tile contains a cart. */
    public boolean containsCart() {
        return state == TileState.CART || state == TileState.PRESENT_AND_CART;
    }
}
