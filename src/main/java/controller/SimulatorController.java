package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import model.Territory;

import java.io.IOException;
import java.util.Optional;


public class SimulatorController {


    private Territory territory;

    // menu items
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    private ToggleGroup placeItem;
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


    public void initialize() {
        this.territory = new Territory(12, 16);

        // restyle radio buttons so they fit into the ui
        restyleRadioButtons();

        bindCustomerActions();
        bindMarketActions();


        // put some initial elements into the market
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

        territoryPanel.setTerritory(territory);
        territoryPanel.init();
    }

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
        turnLeftButton.setOnAction(a -> territory.turnLeft());
        turnRightButton.setOnAction(a -> territory.turnRight());
        pickUpButton.setOnAction(a -> territory.pickUp());
        putDownButton.setOnAction(a -> territory.putDown());

        forwardMenuItem.setOnAction(a -> territory.forward());
        turnLeftMenuItem.setOnAction(a -> territory.turnLeft());
        turnRightMenuItem.setOnAction(a -> territory.turnRight());
        pickUpMenuItem.setOnAction(a -> territory.pickUp());
        putDownMenuItem.setOnAction(a -> territory.putDown());
    }

    /** Add EventHandlers to change the territory with the buttons. */
    private void bindMarketActions() {
        // create a dialog window which takes the territory's dimensions as input and resizes the market based on those values
        EventHandler<ActionEvent> resizerHandler = event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResizeDialogView.fxml"));
                DialogPane dialogPane = loader.load();

                ResizeDialogController controller = loader.getController();
                controller.setColumnInputText(String.valueOf(territory.getHeight()));
                controller.setRowInputText(String.valueOf(territory.getWidth()));

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Territorium-Größe");

                Optional<ButtonType> result = dialog.showAndWait();

                result.ifPresent(buttonType -> {
                    if (result.get() == ButtonType.OK) {
                        territory.resizeTerritory(Integer.parseInt(controller.getInputTexts().getKey()),
                                Integer.parseInt(controller.getInputTexts().getValue()));
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        resizeMarketButton.addEventHandler(ActionEvent.ACTION, resizerHandler);
        resizeMarketMenuItem.addEventHandler(ActionEvent.ACTION, resizerHandler);
    }

    /** Change the styling from a radio button to look like a toggle button. */
    private void styleRadioToToggleButton(RadioButton radioButton) {
        radioButton.getStyleClass().remove("radio-button");
        radioButton.getStyleClass().add("toggle-button");
    }
}