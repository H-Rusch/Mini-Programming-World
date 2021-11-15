package controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.Direction;
import model.Position;
import model.Territory;
import model.Tile;

public class TerritoryPanel extends Canvas {

    private static final double TILE_DIMENSION = 30.0;
    private static final double OFFSET = 3.0;
    private double height;
    private double width;

    private Territory territory;

    private Image shelfImg;
    private Image presentImg;
    private Image cartImg;
    private Image presentCartImg;
    private Image rightCustomerImg;
    private Image upCustomerImg;
    private Image leftCustomerImg;
    private Image downCustomerImg;
    private Image rightPresentImg;
    private Image upPresentImg;
    private Image leftPresentImg;
    private Image downPresentImg;

    public TerritoryPanel() {
        super();
    }

    public TerritoryPanel(Territory territory) {
        this.territory = territory;

        init();
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public void init() {
        loadImages();

        draw();
    }

    private void calculateSize() {
        this.height = TILE_DIMENSION * territory.getHeight();
        super.setHeight(height);
        this.width = TILE_DIMENSION * territory.getWidth();
        super.setWidth(width);
    }

    /** Cache the images. */
    private void loadImages() {
        this.shelfImg = new Image(String.valueOf(getClass().getResource("/img/24x24/AltShelf24.png")));
        this.presentImg = new Image(String.valueOf(getClass().getResource("/img/24x24/Present24.png")));
        this.cartImg = new Image(String.valueOf(getClass().getResource("/img/24x24/ShoppingCart24.png")));
        this.presentCartImg = new Image(String.valueOf(getClass().getResource("/img/24x24/PresentAndCart24.png")));
        this.rightCustomerImg = new Image(String.valueOf(getClass().getResource("/img/24x24/RightCustomer24.png")));
        this.upCustomerImg = new Image(String.valueOf(getClass().getResource("/img/24x24/UpCustomer24.png")));
        this.leftCustomerImg = new Image(String.valueOf(getClass().getResource("/img/24x24/LeftCustomer24.png")));
        this.downCustomerImg = new Image(String.valueOf(getClass().getResource("/img/24x24/DownCustomer24.png")));
        this.rightPresentImg = new Image(String.valueOf(getClass().getResource("/img/24x24/RightPresent24.png")));
        this.upPresentImg = new Image(String.valueOf(getClass().getResource("/img/24x24/UpPresent24.png")));
        this.leftPresentImg = new Image(String.valueOf(getClass().getResource("/img/24x24/LeftPresent24.png")));
        this.downPresentImg = new Image(String.valueOf(getClass().getResource("/img/24x24/DownPresent24.png")));
    }

    /**
     * Draw the territory. Only draws the background once, but when the territory's size changes, the background is
     * redrawn and the new size is calculated.
     */
    public void draw() {
        if (height == 0.0 || width == 0.0 || height != territory.getHeight() * TILE_DIMENSION
                || width != territory.getWidth() * TILE_DIMENSION) {
            calculateSize();
            drawBackground();
        }
        drawImages();
    }

    private void drawBackground() {
        GraphicsContext background = this.getGraphicsContext2D();

        // list of javafx colors: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html
        background.setLineWidth(1);
        background.setFill(Color.BISQUE);
        background.setStroke(Color.BLACK);
        background.rect(0, 0, width, height);
        background.fillRect(0, 0, width, height);
        background.strokeRect(0, 0, width, height);

        for (int y = 0; y < territory.getHeight(); y++) {
            background.strokeLine(0.0, y * TILE_DIMENSION, width, y * TILE_DIMENSION);
        }

        for (int x = 0; x < territory.getWidth(); x++) {
            background.strokeLine(x * TILE_DIMENSION, 0.0, x * TILE_DIMENSION, height);
        }
    }

    private void drawImages() {
        GraphicsContext fg = this.getGraphicsContext2D();

        // tiles and their content framed by black borders
        fg.setLineWidth(1);
        fg.setStroke(Color.BLACK);

        Position actorPosition = territory.getActorPosition();
        for (int y = 0; y < territory.getHeight(); y++) {
            for (int x = 0; x < territory.getWidth(); x++) {
                double tilePosX = x * TILE_DIMENSION;
                double tilePosY = y * TILE_DIMENSION;

                if (y == actorPosition.getY() && x == actorPosition.getX()) {
                    drawActor(fg, territory.getActorDirection(), territory.getTile(x, y), tilePosX, tilePosY);
                } else {
                    drawTile(fg, territory.getTile(x, y), tilePosX, tilePosY);
                }
            }
        }
    }

    /** Draw a single tile at the given position. The image is drawn with a small offset to center it in the tile. */
    private void drawTile(GraphicsContext gc, Tile tile, double x, double y) {
        x += OFFSET;
        y += OFFSET;
        switch (tile.getState()) {
            case EMPTY:
                return;
            case SHELF:
                gc.drawImage(shelfImg, x, y);
                return;
            case PRESENT:
                gc.drawImage(presentImg, x, y);
                return;
            case CART:
                gc.drawImage(cartImg, x, y);
                return;
            case PRESENT_AND_CART:
                gc.drawImage(presentCartImg, x, y);
        }
    }

    /** Draw the actor at a specific location based on the direction he is facing in. */
    private void drawActor(GraphicsContext gc, Direction dir, Tile tile, double x, double y) {
        x += OFFSET;
        y += OFFSET;
        Image image;
        switch (dir) {
            case EAST:
                image = tile.containsPresent() ? rightPresentImg : rightCustomerImg;
                break;
            case NORTH:
                image = tile.containsPresent() ? upPresentImg : upCustomerImg;
                break;
            case WEST:
                image = tile.containsPresent() ? leftPresentImg : leftCustomerImg;
                break;
            default:
                image = tile.containsPresent() ? downPresentImg : downCustomerImg;
        }
        gc.drawImage(image, x, y);
    }
}
