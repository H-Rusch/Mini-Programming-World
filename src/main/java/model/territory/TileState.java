package model.territory;

/** Enumeration describing the state a tile can be in. */
public enum TileState {
    EMPTY,
    SHELF,
    CART,
    PRESENT,
    PRESENT_AND_CART;

    /** Get the fitting element for a given string. */
    public static TileState getElementByString(String str) {
        switch (str) {
            case "EMPTY":
                return TileState.EMPTY;
            case "SHELF":
                return TileState.SHELF;
            case "CART":
                return TileState.CART;
            case "PRESENT":
                return TileState.PRESENT;
            case "PRESENT_AND_CART":
                return TileState.PRESENT_AND_CART;
            default:
                return null;
        }
    }
}
