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


    public boolean containsNothing() {
        return state == TileState.EMPTY;
    }

    public boolean containsOnlyPresent() {
        return state == TileState.PRESENT;
    }

    public boolean containsOnlyCart() {
        return state == TileState.CART;
    }

    public boolean containsShelf() {
        return state == TileState.SHELF;
    }

    /** Checks whether this tile contains a present. The tile can also contain a present and a cart. */
    public boolean containsPresent() {
        return state == TileState.PRESENT || state == TileState.PRESENT_AND_CART;
    }

    /** Checks whether this tile contains a cart. The tile can also contain a present. */
    public boolean containsCart() {
        return state == TileState.CART || state == TileState.PRESENT_AND_CART;
    }
}
