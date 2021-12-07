package controller;

import controller.actor.ActorController;
import controller.program.CompileController;
import controller.program.ProgramController;
import controller.simulation.SimulationController;
import controller.territory.ResizeDialogController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.program.Program;
import model.territory.PlaceOnTileSelection;
import model.territory.Territory;
import util.Position;
import view.CodeEditor;
import view.TerritoryPanel;

import java.io.IOException;


public class MainController {

    private Stage stage;
    private Territory territory;
    private Program program;
    private PlaceOnTileSelection selection;
    private ActorController actorController;

    private SimulationController simulationController;

    // menu items
    @FXML
    public MenuItem newMenuItem;
    @FXML
    public MenuItem openMenuItem;
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem compileMenuItem;
    @FXML
    public MenuItem exitMenuItem;
    @FXML
    private ToggleGroup placeItemToggleMenu;
    @FXML
    private RadioMenuItem placeCustomerMenuItem;
    @FXML
    private RadioMenuItem placeShelfMenuItem;
    @FXML
    private RadioMenuItem placeCartMenuItem;
    @FXML
    private RadioMenuItem placePresentMenuItem;
    @FXML
    private RadioMenuItem clearTileMenuItem;
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
    @FXML
    public MenuItem playMenuItem;
    @FXML
    public MenuItem pauseMenuItem;
    @FXML
    public MenuItem stopMenuItem;

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
    public CodeEditor codeTextArea;
    @FXML
    public TerritoryPanel territoryPanel;

    @FXML
    public Label notificationLabel;

    public void initialize() {
        this.selection = new PlaceOnTileSelection();
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;

        setupUI();
    }

    public void setProgram(Program program) {
        this.program = program;
        codeTextArea.setProgram(program);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            ProgramController.saveProgram(program, codeTextArea.getText());
            ProgramController.removeProgram(program);
        });
    }

    public void setActorController(ActorController actorController) {
        this.actorController = actorController;
    }

    public void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
        this.simulationController.setMainController(this);
    }

    /**
     * Set up the UI by restyling some elements. Also set up the EventHandlers, so the user can
     * manipulate the territory by clicking in the UI.
     */
    private void setupUI() {
        restyleRadioButtons();

        bindProgramActions();
        bindMarketActions();
        bindCustomerActions();

        territoryPanel.setTerritory(territory);
    }

    /** Convert radio buttons into toggle buttons visually. */
    private void restyleRadioButtons() {
        styleRadioToToggleButton(placeCustomerButton);
        styleRadioToToggleButton(placeShelfButton);
        styleRadioToToggleButton(placeCartButton);
        styleRadioToToggleButton(placePresentButton);
        styleRadioToToggleButton(clearTileButton);
    }

    /** Add EventHandlers for the interaction between buttons and the program. */
    private void bindProgramActions() {
        newButton.setOnAction(a -> ProgramController.newProgram());
        newMenuItem.setOnAction(a -> ProgramController.newProgram());

        openButton.setOnAction(a -> ProgramController.openProgram(stage));
        openMenuItem.setOnAction(a -> ProgramController.openProgram(stage));

        saveButton.setOnAction(a -> ProgramController.saveProgram(program, codeTextArea.getText()));
        saveMenuItem.setOnAction(a -> ProgramController.saveProgram(program, codeTextArea.getText()));

        compileButton.setOnAction(a -> CompileController.compileProgram(program, codeTextArea.getText(), territory));
        compileMenuItem.setOnAction(a -> CompileController.compileProgram(program, codeTextArea.getText(), territory));

        exitMenuItem.setOnAction(a -> {
            ProgramController.saveProgram(program, codeTextArea.getText());
            ProgramController.removeProgram(program);
            stage.close();
        });
    }

    /** Add EventHandlers for the interaction between buttons and the customer. */
    private void bindCustomerActions() {
        forwardButton.setOnAction(a -> actorController.forward());
        forwardMenuItem.setOnAction(a -> actorController.forward());

        turnLeftButton.setOnAction(a -> actorController.turnLeft());
        turnLeftMenuItem.setOnAction(a -> actorController.turnLeft());

        turnRightButton.setOnAction(a -> actorController.turnRight());
        turnRightMenuItem.setOnAction(a -> actorController.turnRight());

        pickUpButton.setOnAction(a -> actorController.pickUp());
        pickUpMenuItem.setOnAction(a -> actorController.pickUp());

        putDownButton.setOnAction(a -> actorController.putDown());
        putDownMenuItem.setOnAction(a -> actorController.putDown());

        presentsButton.setOnAction(a -> actorController.setPresentCount());
        presentsMenuItem.setOnAction(a -> actorController.setPresentCount());
    }

    /** Add EventHandlers to change the territory with the buttons. */
    private void bindMarketActions() {
        placeCustomerButton.selectedProperty().bindBidirectional(placeCustomerMenuItem.selectedProperty());
        placeShelfButton.selectedProperty().bindBidirectional(placeShelfMenuItem.selectedProperty());
        placeCartButton.selectedProperty().bindBidirectional(placeCartMenuItem.selectedProperty());
        placePresentButton.selectedProperty().bindBidirectional(placePresentMenuItem.selectedProperty());
        clearTileButton.selectedProperty().bindBidirectional(clearTileMenuItem.selectedProperty());

        // event handlers to synchronize the selection of the toggle groups
        placeItemToggleToolbar.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == placeCustomerButton) {
                selection.setSelected(PlaceOnTileSelection.ACTOR);
            } else if (newToggle == placeShelfButton) {
                selection.setSelected(PlaceOnTileSelection.SHELF);
            } else if (newToggle == placeCartButton) {
                selection.setSelected(PlaceOnTileSelection.CART);
            } else if (newToggle == placePresentButton) {
                selection.setSelected(PlaceOnTileSelection.PRESENT);
            } else if (newToggle == clearTileButton) {
                selection.setSelected(PlaceOnTileSelection.REMOVE);
            }
        });

        // create a dialog window which takes the territory's dimensions as input and resizes the market based on those values
        EventHandler<ActionEvent> resizerHandler = event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResizeDialogView.fxml"));
                DialogPane dialogPane = loader.load();

                ResizeDialogController controller = loader.getController();
                controller.setColumnInputText(String.valueOf(territory.getWidth()));
                controller.setRowInputText(String.valueOf(territory.getHeight()));

                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Territorium-Größe");

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return new Pair<>(controller.getRowInputText(), controller.getColumnInputText());
                    }
                    return null;
                });

                dialog.showAndWait().ifPresent(pair ->
                        territory.resizeTerritory(Integer.parseInt(pair.getKey()), Integer.parseInt(pair.getValue()))
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        resizeMarketButton.addEventHandler(ActionEvent.ACTION, resizerHandler);
        resizeMarketMenuItem.addEventHandler(ActionEvent.ACTION, resizerHandler);

        // event handlers to place objects
        territoryPanel.setOnMousePressed(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position pos = territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
                placeItemAtPosition(pos);
            }
        });
        territoryPanel.setOnMouseDragged(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position pos = territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
                if (pos != null) {
                    placeItemAtPosition(pos);
                }
            }
        });

        // context menu from where the user can call every method the actor implements
        territoryPanel.setOnContextMenuRequested(me -> {
            Position pos = territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
            if (pos.getX() == territory.getActorPosition().getX() && pos.getY() == territory.getActorPosition().getY()) {
                actorController.createActionContextMenu(stage, me.getScreenX() + 10, me.getScreenY() - 10);
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
            case PlaceOnTileSelection.ACTOR:
                territory.tryPlaceActor(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.SHELF:
                territory.placeShelf(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.CART:
                territory.placeCart(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.PRESENT:
                territory.placePresent(pos.getX(), pos.getY());
                break;
            default:
                territory.clearTile(pos.getX(), pos.getY());
        }
    }
}