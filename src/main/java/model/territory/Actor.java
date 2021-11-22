package model.territory;

/** Actor in the territory. The actor faces in one of the directions and carries a number of presents. */
public class Actor {

    private Territory market;
    private Direction direction;
    private int presents;

    public Actor() {

    }

    public Actor(Territory market) {
        this.market = market;
        this.direction = Direction.EAST;
        this.presents = 0;
    }

    /* Getters and Setters */
    public int getPresents() {
        return presents;
    }

    public void setPresents(int presents) {
        this.presents = presents;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /* Methods */
    public void forward() {
        market.forward();
    }

    public void turnLeft() {
        market.turnLeft();
    }

    public void turnRight() {
        market.turnRight();
    }

    public void pickUp() {
        market.pickUp();
    }

    public void putDown() {
        market.putDown();
    }

    public boolean wallAhead() {
        return market.wallAhead();
    }

    public boolean cartAhead() {
        return market.cartAhead();
    }

    public boolean pushable() {
        return market.pushable();
    }

    public boolean presentHere() {
        return this.market.presentHere();
    }

    public boolean basketEmpty() {
        return market.basketEmpty();
    }
}
