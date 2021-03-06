package model.territory;

/** Enumeration describing the four cardinal directions. */
public enum Direction {
    EAST,
    NORTH,
    WEST,
    SOUTH;

    /**
     * Get the direction after turning 90° left from the current direction.
     *
     * @return the direction after turning
     */
    public Direction getDirectionLeft() {
        switch (this) {
            case EAST:
                return NORTH;
            case NORTH:
                return WEST;
            case WEST:
                return SOUTH;
            default: // SOUTH
                return EAST;
        }
    }

    /**
     * Get the direction after turning 90° right from the current direction.
     *
     * @return the direction after turning
     */
    public Direction getDirectionRight() {
        switch (this) {
            case EAST:
                return SOUTH;
            case NORTH:
                return EAST;
            case WEST:
                return NORTH;
            default: // SOUTH
                return WEST;
        }
    }

    /** Get the fitting element for a given string. */
    public static Direction getElementByString(String str) {
        switch (str) {
            case "EAST":
                return Direction.EAST;
            case "NORTH":
                return Direction.NORTH;
            case "WEST":
                return Direction.WEST;
            case "SOUTH":
                return Direction.SOUTH;
            default:
                return null;
        }
    }
}


