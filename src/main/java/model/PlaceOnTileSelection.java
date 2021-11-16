package model;

import util.Observable;

public class PlaceOnTileSelection extends Observable {

    private static final int ACTOR = 0;
    private static final int SHELF = 1;
    private static final int CART = 2;
    private static final int PRESENT = 3;
    private static final int REMOVE = 4;

    private int selected;

    public PlaceOnTileSelection() {
        this.selected = ACTOR;
    }

    public void setSelected(int selected) {
        this.selected = selected;

        setChanged();
        notifyObservers();
    }

    public int getSelected() {
        return selected;
    }
}
