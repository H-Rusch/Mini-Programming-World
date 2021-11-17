package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import model.PlaceOnTileSelection;
import model.Territory;
import util.Observer;
import util.Position;

import java.io.IOException;
import java.util.Optional;


public class SimulatorController implements Observer {

    private Territory territory;
    private PlaceOnTileSelection selection;

    // menu items
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    private ToggleGroup placeItemToggleMenu;
    @FXML
    public MenuItem resizeMarketMenuItem;
    @FXML
    public MenuItem forwardMenuItem;
    @FXML
    public MenuItem turnLeftMenuItem;
    @FXML
    public MenuItem turnRightMenuItem;
    @FXML
    public MenuItem pickUpMenuItem;
    @FXML
    public MenuItem putDownMenuItem;
    @FXML
    public MenuItem presentsMenuItem;

    // toolbar items
    @FXML
    public Button newButton;
    @FXML
    public Button openButton;
    @FXML
    public Button saveButton;
    @FXML
    public Button compileButton;
    @FXML
    public Button resizeMarketButton;
    @FXML
    public ToggleGroup placeItemToggleToolbar;
    @FXML
    public RadioButton placeCustomerButton;
    @FXML
    public RadioButton placeShelfButton;
    @FXML
    public RadioButton placeCartButton;
    @FXML
    public RadioButton placePresentButton;
    @FXML
    public RadioButton clearTileButton;
    @FXML
    public Button forwardButton;
    @FXML
    public Button turnLeftButton;
    @FXML
    public Button turnRightButton;
    @FXML
    public Button pickUpButton;
    @FXML
    public Button putDownButton;
    @FXML
    public Button presentsButton;
    @FXML
    public Button playButton;
    @FXML
    public Button pauseButton;
    @FXML
    public Button stopButton;
    @FXML
    public Slider speedSlider;

    @FXML
    public TextArea codeTextArea;
    @FXML
    public TerritoryPanel territoryPanel;

    @FXML
    public Label notificationLabel;

    /**
     * Initialize the controller by generating te model. Also set up the EventHandlers, so the user can
     * manipulate the territory by clicking in the UI.
     */
    public void initialize() {
        this.territory = new Territory(12, 16);
        this.selection = new PlaceOnTileSelection();
        selection.addObserver(this);

        restyleRadioButtons();

        bindMarketActions();
        bindCustomerActions();

        // put some initial elements into the market
        territory.forcePlaceActor(8, 7);

        territory.placeShelf(5, 5);
        territory.placeShelf(4, 4);
        territory.placeShelf(4, 5);
        territory.placeShelf(4, 3);

        territory.placePresent(1, 4);
        territory.placePresent(8, 9);
        territory.placePresent(15, 3);

        territory.placeCart(1, 4);
        territory.placeCart(7, 9);

        territoryPanel.setTerritory(territory);
        territoryPanel.init();
    }

    /** Convert radio buttons into toggle buttons visually. */
    private void restyleRadioButtons() {
        styleRadioToToggleButton(placeCustomerButton);
        styleRadioToToggleButton(placeShelfButton);
        styleRadioToToggleButton(placeCartButton);
        styleRadioToToggleButton(placePresentButton);
        styleRadioToToggleButton(clearTileButton);
    }

    /** Add EventHandlers for the interaction between buttons and the customer. */
    private void bindCustomerActions() {
        forwardButton.setOnAction(a -> territory.forward());
        forwardMenuItem.setOnAction(a -> territory.forward());

        turnLeftButton.setOnAction(a -> territory.turnLeft());
        turnLeftMenuItem.setOnAction(a -> territory.turnLeft());

        turnRightButton.setOnAction(a -> territory.turnRight());
        turnRightMenuItem.setOnAction(a -> territory.turnRight());

        pickUpButton.setOnAction(a -> territory.pickUp());
        pickUpMenuItem.setOnAction(a -> territory.pickUp());

        putDownButton.setOnAction(a -> territory.putDown());
        putDownMenuItem.setOnAction(a -> territory.putDown());

        // create a dialog window which asks the user to input how many presents should be in the basket
        EventHandler<ActionEvent> presentSettingHandler = event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresentsDialogView.fxml"));
                DialogPane dialogPane = loader.load();

                PresentsDialogController controller = loader.getController();
                controller.setPresentsInputText(String.valueOf(territory.getActorPresentCount()));

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Geschenke im Korb");

                Optional<ButtonType> result = dialog.showAndWait();

                result.ifPresent(buttonType -> {
                    if (result.get() == ButtonType.OK) {
                        territory.setActorPresentCount(Integer.parseInt(controller.getPresentsInputText()));
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        presentsButton.setOnAction(presentSettingHandler);
        presentsMenuItem.setOnAction(presentSettingHandler);
    }

    /** Add EventHandlers to change the territory with the buttons. */
    private void bindMarketActions() {
        // event handlers to synchronize the selection of the toggle groups
        placeItemToggleMenu.selectedToggleProperty().addListener((obs, old_toggle, new_toggle) ->
                selection.setSelected(placeItemToggleMenu.getToggles().indexOf(new_toggle)));
        placeItemToggleToolbar.selectedToggleProperty().addListener((obs, old_toggle, new_toggle) ->
                selection.setSelected(placeItemToggleToolbar.getToggles().indexOf(new_toggle)));

        // create a dialog window which takes the territory's dimensions as input and resizes the market based on those values
        EventHandler<ActionEvent> resizerHandler = event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResizeDialogView.fxml"));
                DialogPane dialogPane = loader.load();

                ResizeDialogController controller = loader.getController();
                controller.setColumnInputText(String.valueOf(territory.getWidth()));
                controller.setRowInputText(String.valueOf(territory.getHeight()));

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Territorium-Größe");

                Optional<ButtonType> result = dialog.showAndWait();

                result.ifPresent(buttonType -> {
                    if (result.get() == ButtonType.OK) {
                        territory.resizeTerritory(Integer.parseInt(controller.getRowInputText()),
                                Integer.parseInt(controller.getColumnInputText()));
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        resizeMarketButton.addEventHandler(ActionEvent.ACTION, resizerHandler);
        resizeMarketMenuItem.addEventHandler(ActionEvent.ACTION, resizerHandler);

        // event handlers to place objects
        territoryPanel.setOnMousePressed((me) -> {
            Position pos = territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
            placeItemAtPosition(pos);
        });
        territoryPanel.setOnMouseDragged((me) -> {
            Position pos = territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
            if (pos != null) {
                placeItemAtPosition(pos);
            }
        });
    }

    /** Change the styling from a radio button to look like a toggle button. */
    private void styleRadioToToggleButton(RadioButton radioButton) {
        radioButton.getStyleClass().remove("radio-button");
        radioButton.getStyleClass().add("toggle-button");
    }

    /**
     * Place an "item" at a specific location. Placing the actor will try to place the actor at that position, but if
     * the tile is blocked, the actor will not be moved.
     *
     * @param pos the position the "item" should bee placed at
     */
    public void placeItemAtPosition(Position pos) {
        switch (selection.getSelected()) {
            case 0:
                territory.tryPlaceActor(pos.getX(), pos.getY());
                break;
            case 1:
                territory.placeShelf(pos.getX(), pos.getY());
                break;
            case 2:
                territory.placeCart(pos.getX(), pos.getY());
                break;
            case 3:
                territory.placePresent(pos.getX(), pos.getY());
                break;
            case 4:
                territory.clearTile(pos.getX(), pos.getY());
        }
    }

    /** Update the selected item in the toggle group, so they have the same option selected at all times. */
    @Override
    public void update() {
        int index = selection.getSelected();
        placeItemToggleMenu.selectToggle(placeItemToggleMenu.getToggles().get(index));
        placeItemToggleToolbar.selectToggle(placeItemToggleToolbar.getToggles().get(index));
    }
}