package controller;

import controller.actor.ActorController;
import controller.example.ExampleController;
import controller.program.ProgramController;
import controller.save.SaveController;
import controller.simulation.SimulationController;
import controller.territory.TerritoryController;
import controller.tutor.StudentController;
import controller.tutor.TutorController;
import controller.tutor.TutorStudentController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.program.Program;
import model.territory.Territory;
import util.PropertyController;
import view.CodeEditor;
import view.TerritoryPanel;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FXMLController {

    private Stage stage;
    private Territory territory;
    private Program program;

    private ActorController actorController;
    private SimulationController simulationController;
    private SaveController saveController;
    private ProgramController programController;
    private TerritoryController territoryController;
    private ExampleController exampleController;
    private TutorStudentController tutoringController;

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
    public Menu saveTerritoryMenu;
    @FXML
    public Menu loadTerritoryMenu;
    @FXML
    public MenuItem serializeMenuItem;
    @FXML
    public MenuItem deserializeMenuItem;
    @FXML
    public MenuItem saveXmlMenuItem;
    @FXML
    public MenuItem loadXmlMenuItem;
    @FXML
    public MenuItem saveImageMenuItem;
    @FXML
    public ToggleGroup placeItemToggleMenu;
    @FXML
    public RadioMenuItem placeCustomerMenuItem;
    @FXML
    public RadioMenuItem placeShelfMenuItem;
    @FXML
    public RadioMenuItem placeCartMenuItem;
    @FXML
    public RadioMenuItem placePresentMenuItem;
    @FXML
    public RadioMenuItem clearTileMenuItem;
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
    @FXML
    public MenuItem loadExampleMenuItem;
    @FXML
    public MenuItem saveExampleMenuItem;
    @FXML
    public MenuItem sendTutorRequestMenuItem;
    @FXML
    public MenuItem receiveTutorAnswerMenuItem;
    @FXML
    public MenuItem receiveStudentRequestMenuItem;
    @FXML
    public MenuItem sendStudentAnswerMenuItem;

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
    public Button resetTerritoryButton;
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

    /** Create all the used controllers. Those controllers connect the UI elements with actions. */
    public void setUpControllers() {
        this.territoryController = new TerritoryController(territory, this);
        this.actorController = new ActorController(territory, territory.getActor(), this);
        this.simulationController = new SimulationController(territory, this);
        this.saveController = new SaveController(territory, stage, this);
        this.programController = new ProgramController(program, stage, territory, this);
        this.exampleController = new ExampleController(saveController, stage, this);
        if (PropertyController.isTutor()) {
            this.tutoringController = new TutorController(saveController, this);
        } else {
            this.tutoringController = new StudentController(saveController, this);
        }
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;

        setupUI();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    /** Set up the UI by restyling some elements. */
    private void setupUI() {
        restyleRadioButtons();
    }

    /** Convert radio buttons into toggle buttons visually. */
    private void restyleRadioButtons() {
        styleRadioToToggleButton(placeCustomerButton);
        styleRadioToToggleButton(placeShelfButton);
        styleRadioToToggleButton(placeCartButton);
        styleRadioToToggleButton(placePresentButton);
        styleRadioToToggleButton(clearTileButton);
    }

    /** Change the styling from a radio button to look like a toggle button. */
    private void styleRadioToToggleButton(RadioButton radioButton) {
        radioButton.getStyleClass().remove("radio-button");
        radioButton.getStyleClass().add("toggle-button");
    }

    public void createActionsMenu(double x, double y) {
        actorController.createActionContextMenu(stage, x, y);
    }

    public void updateNotificationText(String text) {
        notificationLabel.setText(text + " (" + new SimpleDateFormat("HH:mm").format(new Date()) + ")");
    }
}
