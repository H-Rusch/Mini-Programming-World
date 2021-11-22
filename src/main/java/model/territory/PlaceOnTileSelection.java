package model.territory;

public class PlaceOnTileSelection {

    public static final int ACTOR = 0;
    public static final int SHELF = 1;
    public static final int CART = 2;
    public static final int PRESENT = 3;
    public static final int REMOVE = 4;

    private int selected;

    public PlaceOnTileSelection() {
        this.selected = ACTOR;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }
}
