package model;

import model.exceptions.NoPresentInBasketException;
import model.exceptions.NoPresentOnTileException;
import model.exceptions.PresentAlreadyOnTileException;
import model.exceptions.TileBlockedException;

public class Territory {

    private Actor actor;
    private Position actorPosition;

    private int height;
    private int width;
    private Tile[][] market;

    public Territory(int height, int width) {
        this.height = height;
        this.width = width;

        // initialize all tiles
        this.market = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                market[y][x] = new Tile();
            }
        }

        // spawn the actor
        this.actor = new Actor(this);
        this.actorPosition = new Position(0, 0);
    }

    /** Get a specific tile based on its x and y coordinate. */
    public Tile getTile(int x, int y) {
        return market[y][x];
    }

    /** Get a specific tile based on its x and y coordinate. */
    public Tile getTile(Position post) {
        return market[post.getY()][post.getX()];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Position getActorPosition() {
        return actorPosition;
    }

    public Direction getActorDirection() {
        return actor.getDirection();
    }

    /**
     * Try to move the actor one tile forwards. Fails, if the actor tries to run into a shelf/ wall or into a blocked
     * cart. Carts which are not blocked are pushed along onto the next tile.
     *
     * @throws TileBlockedException if the tile, the actor tries to walk on, is blocked by a wall or a blocked cart
     */
    public void forward() {
        Position pos = getTilesFurther(1);
        if (wallAhead()) {
            throw new TileBlockedException("The tile is blocked by a wall.");
        }
        if (cartAhead()) {
            if (!pushable()) {
                throw new TileBlockedException("The cart can not be pushed because it is blocked itself.");
            } else {
                Tile adjacent = getTile(getTilesFurther(1));
                Tile furtherAway = getTile(getTilesFurther(2));

                // remove the cart from the adjacent tile
                if (adjacent.containsOnlyCart()) {
                    adjacent.setState(TileState.EMPTY);
                } else {
                    adjacent.setState(TileState.PRESENT);
                }
                // push the cart onto the tile one tile further away
                if (furtherAway.containsNothing()) {
                    furtherAway.setState(TileState.CART);
                } else {
                    furtherAway.setState(TileState.PRESENT_AND_CART);
                }
            }
        }
        // move actor onto the adjacent tile
        actorPosition = pos;
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
        Tile currentTile = getTile(actorPosition);
        if (!currentTile.containsPresent()) {
            throw new NoPresentOnTileException("There is no present at the current location.");
        } else {
            currentTile.setState(TileState.EMPTY);
            actor.setPresents(actor.getPresents() + 1);
        }
    }

    /**
     * Tries to put down a present at the position the actor is standing at. If a present is put down, the tile it was
     * laying on will be empty and the counter of presents will be incremented.
     *
     * @throws PresentAlreadyOnTileException if there already is a present at the current location.
     * @throws NoPresentInBasketException    if the actor tries to place presents while the basket is empty.
     */
    public void putDown() {
        Tile currentTile = getTile(actorPosition);
        if (basketEmpty()) {
            throw new NoPresentInBasketException("There are no presents to place in the basket.");
        } else {
            if (currentTile.containsPresent()) {
                throw new PresentAlreadyOnTileException("There is already a present laying at the current position.");
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
        Position pos = getTilesFurther(1);
        if (!isInbounds(pos)) {
            return true;
        }

        return getTile(pos).containsShelf();
    }

    /**
     * Checks whether a cart is in front of the actor based on the direction the actor is facing in.
     *
     * @return true if there is a cart one space ahead of the actor. false otherwise.
     */
    public boolean cartAhead() {
        Position pos = getTilesFurther(1);
        if (!isInbounds(pos)) {
            return false;
        }
        return getTile(pos).containsCart();
    }

    /**
     * Checks whether a cart in front of the actor is pushable. A cart is pushable, if the tile two tiles further away
     * does not contain a shelf/ wall or another cart.
     *
     * @return true if there is a pushable cart one space ahead of the actor. false otherwise.
     */
    public boolean pushable() {
        Position pos1 = getTilesFurther(1);
        Position pos2 = getTilesFurther(2);

        if (!(isInbounds(pos2) && isInbounds(pos1))) {
            return false;
        }

        return getTile(pos1).containsCart() && (getTile(pos2).containsNothing() || getTile(pos2).containsOnlyPresent());
    }

    /** Checks whether a present is laying at the current position of the actor. */
    public boolean presentHere() {
        return getTile(actorPosition).containsPresent();
    }

    /** Checks whether the actor's basket is empty. */
    public boolean basketEmpty() {
        return actor.getPresents() == 0;
    }

    /**
     * Get the position of the tile 'amount'-steps further into the direction the actor is currently facing in.
     *
     * @param amount the amount of tiles further
     */
    private Position getTilesFurther(int amount) {
        int x = actorPosition.getX();
        int y = actorPosition.getY();
        switch (actor.getDirection()) {
            case EAST:
                x += amount;
                break;

            case NORTH:
                y -= amount;
                break;

            case WEST:
                x -= amount;
                break;

            default: // SOUTH
                y += amount;
        }

        return new Position(x, y);
    }

    /**
     * Checks whether a position is in the bounds of the market.
     *
     * @param pos the position which should be checked
     * @return true if the position is in bounds, false otherwise
     */
    private boolean isInbounds(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        return x >= 0 && x < width && y >= 0 && y < height;
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

        if (actorPosition.getX() >= width || actorPosition.getY() >= height) {
            placeActor(0, 0);
        }
    }

    /** Set the position of the actor. Shelves and carts on the tile will be removed, but presents stay. */
    public void placeActor(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile.containsShelf() || tile.containsOnlyCart()) {
            tile.setState(TileState.EMPTY);
        } else if (tile.containsPresent() && tile.containsCart()) {
            tile.setState(TileState.PRESENT);
        }

        actorPosition = new Position(x, y);
    }

    /** Set a tile specified by its x and y coordinate to be a shelf. */
    public void placeShelf(int x, int y) {
        if (x != actorPosition.getX() || y != actorPosition.getY()) {
            getTile(x, y).setState(TileState.SHELF);
        }
    }

    /**
     * Set a tile specified by its x and y coordinate to be a cart. If a present is already present, the present will
     * be added onto the tile without removing the present.
     */
    public void placeCart(int x, int y) {
        if (x != actorPosition.getX() || y != actorPosition.getY()) {
            Tile tile = getTile(x, y);
            if (tile.containsOnlyPresent()) {
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
        Tile tile = getTile(x, y);
        if (tile.containsOnlyCart()) {
            tile.setState(TileState.PRESENT_AND_CART);
        } else {
            tile.setState(TileState.PRESENT);
        }
    }

    /** Clear the content of a tile specified by its x and y coordinate. The actor will not be removed. */
    public void clearTile(int x, int y) {
        getTile(x, y).setState(TileState.EMPTY);
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

                if (this.actorPosition.getX() == x && this.actorPosition.getY() == y) {
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
