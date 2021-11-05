package model;

import exceptions.NoPresentOnTileException;
import exceptions.NoPresentInBasketException;
import exceptions.PresentAlreadyOnTileException;
import exceptions.TileBlockedException;

public class Territory {

    private Actor actor;
    private int actorX;
    private int actorY;

    private int height;
    private int width;
    private Tile[][] market;

    public Territory(int height, int width) {
        this.height = height;
        this.width = width;

        // initialize all tiles
        market = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                market[y][x] = new Tile();
            }
        }

        // spawn the actor
        this.actor = new Actor(this);
        this.actorX = 0;
        this.actorY = 0;
    }

    /**
     * Try to move the actor one tile forwards. Fails, if the actor tries to run into a shelf/ wall or into a blocked
     * cart. Carts which are not blocked are pushed along onto the next tile.
     *
     * @throws TileBlockedException if the tile, the actor tries to walk on, is blocked by a wall or a blocked cart
     */
    public void forward() {
        if (wallAhead()) {
            throw new TileBlockedException("The tile is blocked by a wall.");
        }
        if (cartAhead()) {
            if (!pushable()) {
                throw new TileBlockedException("The cart can not be pushed because it is blocked itself.");
            } else {
                // move cart
                Tile adjacent = getTilesFurther(1);
                Tile furtherAway = getTilesFurther(2);

                // remove the cart from the adjacent tile
                if (adjacent.getState() == TileState.CART) {
                    adjacent.setState(TileState.EMPTY);
                } else {
                    adjacent.setState(TileState.PRESENT);
                }
                // push the cart onto the next tile
                if (furtherAway.getState() == TileState.EMPTY) {
                    furtherAway.setState(TileState.CART);
                } else {
                    furtherAway.setState(TileState.PRESENT_AND_CART);
                }
            }
        }
        // move actor
        switch (actor.getDirection()) {
            case EAST:
                actorX++;
                break;

            case NORTH:
                actorY--;
                break;

            case WEST:
                actorX--;
                break;

            default:
                actorY++;
                break;
        }
    }

    /** Turn the actor 90° to the left. */
    public void turnLeft() {
        actor.setDirection(actor.getDirection().getDirectionLeft());
    }

    /** Turn the actor 90° to the right. */
    public void turnRight() {
        actor.setDirection(actor.getDirection().getDirectionRight());
    }

    /**
     * Tries to pick up a present at the position the actor is standing at. If a present is picked up, the tile it was
     * laying on will be empty and the counter of presents will be incremented.
     *
     * @throws NoPresentOnTileException if there is no present at the current location.
     */
    public void pickUp() {
        Tile currentTile = market[actorY][actorX];
        if (!currentTile.containsPresent()) {
            throw new NoPresentOnTileException("There is no present at the current location.");
        } else {
            currentTile.setState(TileState.EMPTY);
            actor.setPresents(actor.getPresents() + 1);
        }
    }

    /**
     * Tries to lay down a present at the position the actor is standing at. If a present is put down, the tile it was
     * laying on will be empty and the counter of presents will be incremented.
     *
     * @throws PresentAlreadyOnTileException if there already is a present at the current location.
     * @throws NoPresentInBasketException    if the actor tries to place presents while the basket is empty.
     */
    public void putDown() {
        Tile currentTile = market[actorY][actorX];
        if (currentTile.containsPresent()) {
            throw new PresentAlreadyOnTileException("There is already a present laying at the current position.");
        } else {
            if (basketEmpty()) {
                throw new NoPresentInBasketException("There are no presents to place in the basket.");
            } else {
                currentTile.setState(TileState.PRESENT);
                actor.setPresents(actor.getPresents() - 1);
            }
        }
    }

    /**
     * Checks whether a wall/ shelf is in front of the actor based on the direction the actor is facing in.
     *
     * @return true if there is a wall one space ahead of the actor. false otherwise.
     */
    public boolean wallAhead() {
        switch (actor.getDirection()) {
            case EAST:
                if (actorX == width - 1) { // edge of the territory
                    return true;
                }
                break;

            case NORTH:
                if (actorY == 0) {
                    return true;
                }
                break;

            case WEST:
                if (actorX == 0) {
                    return true;
                }
                break;

            default: // SOUTH
                if (actorY == height - 1) {
                    return true;
                }
        }
        return getTilesFurther(1).getState() == TileState.SHELF;
    }

    /**
     * Checks whether a cart is in front of the actor based on the direction the actor is facing in.
     *
     * @return true if there is a cart one space ahead of the actor. false otherwise.
     */
    public boolean cartAhead() {
        switch (actor.getDirection()) {
            case EAST:
                if (actorX == width - 1) {
                    return false;
                }
                break;

            case NORTH:
                if (actorY == 0) {
                    return false;
                }
                break;

            case WEST:
                if (actorX == 0) {
                    return false;
                }
                break;

            default: // SOUTH
                if (actorY == height - 1) {
                    return false;
                }
        }
        return getTilesFurther(1).containsCart();
    }

    /**
     * Checks whether a cart in front of the actor is pushable. A cart is pushable, if pushing it would not push it onto
     * the tile of a wall/ shelf or another cart.
     *
     * @return true if there is a pushable cart one space ahead of the actor. false otherwise.
     */
    public boolean pushable() {
        TileState state;
        switch (actor.getDirection()) {
            case EAST:
                // actor is at the edge, or the cart is at the edge
                if (actorX >= width - 2) {
                    return false;
                }
                break;

            case NORTH:
                if (actorY <= 1) {
                    return false;
                }
                break;

            case WEST:
                if (actorX <= 1) {
                    return false;
                }
                break;

            default: // SOUTH
                if (actorY >= height - 2) {
                    return false;
                }
        }
        // cart on the tile ahead
        Tile adjacent = getTilesFurther(1);
        if (adjacent.containsCart()) {
            state = getTilesFurther(2).getState();
            return state == TileState.EMPTY || state == TileState.PRESENT;
        }
        return false;
    }

    /** Checks whether a present is laying at the current position of the actor. */
    public boolean presentHere() {
        return market[actorY][actorX].getState() == TileState.PRESENT;
    }

    /** Checks whether the actor's basket is empty. */
    public boolean basketEmpty() {
        return actor.getPresents() == 0;
    }

    /**
     * Get the tile 'amount'-steps further into the direction the actor is currently facing in. No security regarding
     * array indices.
     *
     * @param amount the amount of tiles further
     */
    private Tile getTilesFurther(int amount) {
        switch (actor.getDirection()) {
            case EAST:
                return market[actorY][actorX + amount];

            case NORTH:
                return market[actorY - amount][actorX];

            case WEST:
                return market[actorY][actorX - amount];

            default: // SOUTH
                return market[actorY + amount][actorX];
        }
    }

    /**
     * Resize the territory to the specified dimensions. The minimum dimension is a 1x1 territory.
     * If the new territory is smaller than before, the bottom-most rows and right-most columns are removed first. If
     * the new territory is bigger than before, new empty rows are created at the bottom and new columns are created on
     * the right. If the actor is on a position which is removed, the actor will be spawned at (0, 0) again.
     */
    public void resizeTerritory(int height, int width) {
        if (height == this.height && width == this.width) {
            return;
        }
        if (height < 1) {
            height = 1;
        }
        if (width < 1) {
            width = 1;
        }

        Tile[][] tmpMarket = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y < this.height && x < this.width) {
                    tmpMarket[y][x] = new Tile(market[y][x].getState());
                } else {
                    tmpMarket[y][x] = new Tile();
                }
            }
        }
        market = tmpMarket;
        this.height = height;
        this.width = width;

        if (actorX >= width || actorY >= height) {
            placeActor(0, 0);
        }
    }

    /** Set the position of the actor. Shelves and carts on the tile will be removed, but presents stay. */
    public void placeActor(int x, int y) {
        Tile tile = market[y][x];
        if (tile.getState() == TileState.SHELF || tile.getState() == TileState.CART) {
            tile.setState(TileState.EMPTY);
        } else if (tile.getState() == TileState.PRESENT_AND_CART) {
            tile.setState(TileState.PRESENT);
        }

        actorY = y;
        actorX = x;
    }

    /** Set a tile specified by its x and y coordinate to be a shelf. */
    public void placeShelf(int x, int y) {
        if (x != actorX || y != actorY) {
            market[y][x].setState(TileState.SHELF);
        }
    }

    /**
     * Set a tile specified by its x and y coordinate to be a cart. If a present is already present, the present will
     * be added onto the tile without removing the present.
     */
    public void placeCart(int x, int y) {
        if (x != actorX || y != actorY) {
            Tile tile = market[y][x];
            if (tile.getState() == TileState.PRESENT) {
                tile.setState(TileState.PRESENT_AND_CART);
            } else {
                tile.setState(TileState.CART);
            }
        }
    }

    /**
     * Set a tile specified by its x and y coordinate to be a present. If a cart is already present, the present will
     * be added onto the tile without removing the cart.
     */
    public void placePresent(int x, int y) {
        Tile tile = market[y][x];
        if (tile.getState() == TileState.CART) {
            tile.setState(TileState.PRESENT_AND_CART);
        } else {
            tile.setState(TileState.PRESENT);
        }
    }

    /** Clear the content of a tile specified by its x and y coordinate. The actor will not be removed. */
    public void clearTile(int x, int y) {
        market[y][x].setState(TileState.EMPTY);
    }

    public void print() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileState state = market[y][x].getState();
                String output;

                switch (state) {
                    case EMPTY:
                        output = ". ";
                        break;
                    case SHELF:
                        output = "# ";
                        break;
                    case PRESENT:
                        output = "P ";
                        break;
                    case CART:
                        output = "C ";
                        break;
                    default: // present and cart
                        output = "D ";
                }

                if (this.actorX == x && this.actorY == y) {
                    Direction direction = actor.getDirection();
                    switch (direction) {
                        case EAST:
                            output = "→ ";
                            break;
                        case NORTH:
                            output = "↑ ";
                            break;
                        case WEST:
                            output = "← ";
                            break;
                        default: // south
                            output = "↓ ";
                            break;
                    }
                }
                System.out.print(output);
            }
            System.out.println();
        }
    }
}
