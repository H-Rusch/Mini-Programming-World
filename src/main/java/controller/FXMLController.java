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
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.program.Program;
import model.territory.Territory;
import util.I18nUtil;
import util.PropertyController;
import view.CodeEditor;
import view.TerritoryPanel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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

    @FXML
    public Menu editorMenu;
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
    public Menu marketMenu;
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
    public Menu customerMenu;
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
    public Menu simulationMenu;
    @FXML
    public MenuItem playMenuItem;
    @FXML
    public MenuItem pauseMenuItem;
    @FXML
    public MenuItem stopMenuItem;
    @FXML
    public Menu examplesMenu;
    @FXML
    public MenuItem loadExampleMenuItem;
    @FXML
    public MenuItem saveExampleMenuItem;
    @FXML
    public Menu tutorMenu;
    @FXML
    public MenuItem sendTutorRequestMenuItem;
    @FXML
    public MenuItem receiveTutorAnswerMenuItem;
    @FXML
    public MenuItem receiveStudentRequestMenuItem;
    @FXML
    public MenuItem sendStudentAnswerMenuItem;
    @FXML
    public Menu languageMenu;
    @FXML
    public MenuItem germanLanguageMenuItem;
    @FXML
    public MenuItem englishLanguageMenuItem;

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
    public Tooltip tooltipPauseButton;
    @FXML
    public Tooltip tooltipPlayButton;
    @FXML
    public Tooltip tooltipStopButton;
    @FXML
    public Tooltip tooltipForwardButton;
    @FXML
    public Tooltip tooltipTurnLeftButton;
    @FXML
    public Tooltip tooltipTurnRightButton;
    @FXML
    public Tooltip tooltipPickUpButton;
    @FXML
    public Tooltip tooltipPutDownButton;
    @FXML
    public Tooltip tooltipPresentsButton;
    @FXML
    public Tooltip tooltipNewButton;
    @FXML
    public Tooltip tooltipOpenButton;
    @FXML
    public Tooltip tooltipSaveButton;
    @FXML
    public Tooltip tooltipCompileButton;
    @FXML
    public Tooltip tooltipResizeMarketButton;
    @FXML
    public Tooltip tooltipPlaceCustomerButton;
    @FXML
    public Tooltip tooltipPlaceShelfButton;
    @FXML
    public Tooltip tooltipPlaceCartButton;
    @FXML
    public Tooltip tooltipPlacePresentButton;
    @FXML
    public Tooltip tooltipClearTileButton;
    @FXML
    public Tooltip tooltipResetTerritoryButton;

    @FXML
    public CodeEditor codeTextArea;
    @FXML
    public TerritoryPanel territoryPanel;

    @FXML
    public Label notificationLabel;

    /** Bind UI-elements text to the current language selection. */
    public void initialize() {
        bindTexts();

    }

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

        germanLanguageMenuItem.setOnAction(a -> I18nUtil.setLocale(Locale.GERMANY));
        englishLanguageMenuItem.setOnAction(a -> I18nUtil.setLocale(Locale.US));
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

    private void bindTexts() {
        editorMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("editorMenu"), I18nUtil.localeProperty()));
        newMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("newMenuItem"), I18nUtil.localeProperty()));
        openMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("openMenuItem"), I18nUtil.localeProperty()));
        saveMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("saveMenuItem"), I18nUtil.localeProperty()));
        compileMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("compileMenuItem"), I18nUtil.localeProperty()));
        exitMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("exitMenuItem"), I18nUtil.localeProperty()));
        marketMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("marketMenu"), I18nUtil.localeProperty()));
        saveTerritoryMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("saveTerritoryMenu"), I18nUtil.localeProperty()));
        saveXmlMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("saveXmlMenuItem"), I18nUtil.localeProperty()));
        serializeMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("serializeMenuItem"), I18nUtil.localeProperty()));
        loadTerritoryMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("loadTerritoryMenu"), I18nUtil.localeProperty()));
        loadXmlMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("loadXmlMenuItem"), I18nUtil.localeProperty()));
        deserializeMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("deserializeMenuItem"), I18nUtil.localeProperty()));
        saveImageMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("saveImageMenuItem"), I18nUtil.localeProperty()));
        resizeMarketMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("resizeMarketMenuItem"), I18nUtil.localeProperty()));
        placeCustomerMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("placeCustomerMenuItem"), I18nUtil.localeProperty()));
        placeShelfMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("placeShelfMenuItem"), I18nUtil.localeProperty()));
        placeCartMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("placeCartMenuItem"), I18nUtil.localeProperty()));
        placePresentMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("placePresentMenuItem"), I18nUtil.localeProperty()));
        clearTileMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("clearTileMenuItem"), I18nUtil.localeProperty()));
        customerMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("customerMenu"), I18nUtil.localeProperty()));
        forwardMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("forwardMenuItem"), I18nUtil.localeProperty()));
        turnLeftMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("turnLeftMenuItem"), I18nUtil.localeProperty()));
        turnRightMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("turnRightMenuItem"), I18nUtil.localeProperty()));
        pickUpMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("pickUpMenuItem"), I18nUtil.localeProperty()));
        putDownMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("putDownMenuItem"), I18nUtil.localeProperty()));
        presentsMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("presentsMenuItem"), I18nUtil.localeProperty()));
        simulationMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("simulationMenu"), I18nUtil.localeProperty()));
        playMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("playMenuItem"), I18nUtil.localeProperty()));
        pauseMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("pauseMenuItem"), I18nUtil.localeProperty()));
        stopMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("stopMenuItem"), I18nUtil.localeProperty()));
        examplesMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("examplesMenu"), I18nUtil.localeProperty()));
        saveExampleMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("saveExampleMenuItem"), I18nUtil.localeProperty()));
        loadExampleMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("loadExampleMenuItem"), I18nUtil.localeProperty()));
        tutorMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tutorMenu"), I18nUtil.localeProperty()));
        sendTutorRequestMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("sendTutorRequestMenuItem"), I18nUtil.localeProperty()));
        receiveTutorAnswerMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("receiveTutorAnswerMenuItem"), I18nUtil.localeProperty()));
        receiveStudentRequestMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("receiveStudentRequestMenuItem"), I18nUtil.localeProperty()));
        sendStudentAnswerMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("sendStudentAnswerMenuItem"), I18nUtil.localeProperty()));
        languageMenu.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("languageMenu"), I18nUtil.localeProperty()));
        tooltipPauseButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.pauseButton"), I18nUtil.localeProperty()));
        tooltipPlayButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.playButton.start"), I18nUtil.localeProperty()));
        tooltipStopButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.stopButton"), I18nUtil.localeProperty()));
        tooltipForwardButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.forwardButton"), I18nUtil.localeProperty()));
        tooltipTurnLeftButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.turnLeftButton"), I18nUtil.localeProperty()));
        tooltipTurnRightButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.turnRightButton"), I18nUtil.localeProperty()));
        tooltipPickUpButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.pickUpButton"), I18nUtil.localeProperty()));
        tooltipPutDownButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.putDownButton"), I18nUtil.localeProperty()));
        tooltipPresentsButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.presentsButton"), I18nUtil.localeProperty()));
        tooltipNewButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.newButton"), I18nUtil.localeProperty()));
        tooltipOpenButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.openButton"), I18nUtil.localeProperty()));
        tooltipSaveButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.saveButton"), I18nUtil.localeProperty()));
        tooltipCompileButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.compileButton"), I18nUtil.localeProperty()));
        tooltipResizeMarketButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.resizeMarketButton"), I18nUtil.localeProperty()));
        tooltipPlaceCustomerButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.placeCustomerButton"), I18nUtil.localeProperty()));
        tooltipPlaceShelfButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.placeShelfButton"), I18nUtil.localeProperty()));
        tooltipPlaceCartButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.placeCartButton"), I18nUtil.localeProperty()));
        tooltipPlacePresentButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.placePresentButton"), I18nUtil.localeProperty()));
        tooltipClearTileButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.clearTileButton"), I18nUtil.localeProperty()));
        tooltipResetTerritoryButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.resetTerritoryButton"), I18nUtil.localeProperty()));
    }
}
