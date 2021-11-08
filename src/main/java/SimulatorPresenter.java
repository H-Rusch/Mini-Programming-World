import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.Territory;


public class SimulatorPresenter {

    private Territory territory;

    // menu items
    @FXML
    private ToggleGroup placeItem;

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
    public Pane simulatorPanel;

    @FXML
    public Label notificationLabel;


    public void initialize() {
        // restyle radio buttons so they fit into the ui
        styleRadioToToggleButton(placeCustomerButton);
        styleRadioToToggleButton(placeShelfButton);
        styleRadioToToggleButton(placeCartButton);
        styleRadioToToggleButton(placePresentButton);
        styleRadioToToggleButton(clearTileButton);

        this.territory = new Territory(22, 16);
        /* put some initial elements into the market */
        territory.placeActor(8, 7);

        territory.placeShelf(5, 5);
        territory.placeShelf(4, 4);
        territory.placeShelf(4, 5);
        territory.placeShelf(4, 3);

        territory.placePresent(1, 4);
        territory.placePresent(8, 9);
        territory.placePresent(15, 3);

        territory.placeCart(1, 4);
        territory.placeCart(7, 9);

        simulatorPanel.getChildren().add(new TerritoryPanel(territory));

        territory.resizeTerritory(6, 16);
        // ((TerritoryPanel) simulatorPanel.getChildren().get(0)).draw(); // experimentell, um zu sehen, dass beim resize sich sich das territorium vernünftig anpasst
    }

    /** Change the styling from a radio button to look like a toggle button. */
    private void styleRadioToToggleButton(RadioButton radioButton) {
        radioButton.getStyleClass().remove("radio-button");
        radioButton.getStyleClass().add("toggle-button");
    }
}