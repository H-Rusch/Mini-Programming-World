package model.territory;

/** Actor in the territory. The actor faces in one of the directions and carries a number of presents. */
public class Actor {

    private Territory territory;
    private Direction direction;
    private int presents;

    protected Actor() {
        this.direction = Direction.EAST;
        this.presents = 0;
    }

    protected Actor(Territory territory) {
        this.territory = territory;
        this.direction = Direction.EAST;
        this.presents = 0;
    }

    /* Getters and Setters */
    protected int getPresents() {
        return presents;
    }

    protected void setPresents(int presents) {
        this.presents = presents;
    }

    protected Direction getDirection() {
        return direction;
    }

    protected void setDirection(Direction direction) {
        this.direction = direction;
    }

    protected void setTerritory(Territory territory) {
        this.territory = territory;
    }

    /* Methods */
    public void main() {
        // to be overwritten by custom classes
    }

    public void forward() {
        territory.forward();
    }

    public void turnLeft() {
        territory.turnLeft();
    }

    public void turnRight() {
        territory.turnRight();
    }

    public void pickUp() {
        territory.pickUp();
    }

    public void putDown() {
        territory.putDown();
    }

    public boolean wallAhead() {
        return territory.wallAhead();
    }

    public boolean cartAhead() {
        return territory.cartAhead();
    }

    public boolean pushable() {
        return territory.pushable();
    }

    public boolean presentHere() {
        return this.territory.presentHere();
    }

    public boolean basketEmpty() {
        return territory.basketEmpty();
    }
}
