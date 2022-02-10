package controller.simulation;

import controller.FXMLController;
import javafx.beans.binding.Bindings;
import model.simulation.Simulation;
import model.territory.Territory;
import util.I18nUtil;


public class SimulationController {

    private Simulation simulation;
    private final Territory territory;
    private final FXMLController fxmlController;

    public SimulationController(Territory territory, FXMLController controller) {
        this.territory = territory;
        this.fxmlController = controller;

        setUpEventHandlers();
        fxmlController.pauseButton.setDisable(true);
        fxmlController.stopButton.setDisable(true);
    }

    /** Connect buttons and menu items with corresponding event handlers. */
    private void setUpEventHandlers() {
        fxmlController.speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (simulation != null) {
                simulation.setSimulationSpeed((Double) newValue);
            }
        });

        fxmlController.playButton.setOnAction(event -> startSimulation());
        fxmlController.playMenuItem.setOnAction(event -> startSimulation());

        fxmlController.pauseButton.setOnAction(event -> pauseSimulation());
        fxmlController.pauseMenuItem.setOnAction(event -> pauseSimulation());

        fxmlController.stopButton.setOnAction(event -> stopSimulation());
        fxmlController.stopMenuItem.setOnAction(event -> stopSimulation());

        fxmlController.playButton.disableProperty().bindBidirectional(fxmlController.playMenuItem.disableProperty());
        fxmlController.pauseButton.disableProperty().bindBidirectional(fxmlController.pauseMenuItem.disableProperty());
        fxmlController.stopButton.disableProperty().bindBidirectional(fxmlController.stopMenuItem.disableProperty());
        fxmlController.saveTerritoryMenu.disableProperty().bindBidirectional(fxmlController.loadTerritoryMenu.disableProperty());
        fxmlController.loadTerritoryMenu.disableProperty().bindBidirectional(fxmlController.resetTerritoryButton.disableProperty());
    }

    /** Start the simulation. Disable unneeded buttons and change the 'play' buttons functionality from 'start' to 'continue'. */
    public void startSimulation() {
        fxmlController.playButton.setDisable(true);
        fxmlController.pauseButton.setDisable(false);
        fxmlController.stopButton.setDisable(false);
        fxmlController.saveTerritoryMenu.setDisable(true);

        fxmlController.playButton.setOnAction(event -> continueSimulation());
        fxmlController.playMenuItem.setOnAction(event -> continueSimulation());

        simulation = new Simulation(territory, this, fxmlController.speedSlider.getValue());
        simulation.setDaemon(true);
        simulation.start();
    }

    /** Unpause the simulation. Pausing and stopping is enabled. */
    public void continueSimulation() {
        fxmlController.playButton.setDisable(true);
        fxmlController.pauseButton.setDisable(false);
        fxmlController.stopButton.setDisable(false);

        changeToStart();

        simulation.setPauseSimulation(false);
        simulation.notifySyncObject();
    }

    /** Pause the simulation. Continuing and stopping is enabled. */
    public void pauseSimulation() {
        fxmlController.playButton.setDisable(false);
        fxmlController.pauseButton.setDisable(true);
        fxmlController.stopButton.setDisable(false);

        changeStartToResume();

        simulation.setPauseSimulation(true);
    }

    /** Stop the simulation. Starting is enabled. */
    public void stopSimulation() {
        fxmlController.playButton.setDisable(false);
        fxmlController.pauseButton.setDisable(true);
        fxmlController.stopButton.setDisable(true);

        simulation.setStopSimulation(true);
        simulation.setPauseSimulation(false);
        simulation.notifySyncObject();
    }

    /** Enable buttons, so the simulation can be started again. */
    public void simulationEnded() {
        fxmlController.playButton.setDisable(false);
        fxmlController.pauseButton.setDisable(true);
        fxmlController.stopButton.setDisable(true);
        fxmlController.saveTerritoryMenu.setDisable(false);

        changeStartToResume();

        fxmlController.playButton.setOnAction(event -> startSimulation());
        fxmlController.playMenuItem.setOnAction(event -> startSimulation());
    }

    private void changeStartToResume() {
        fxmlController.tooltipPlayButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.playButton.resume"), I18nUtil.localeProperty()));

        fxmlController.playMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("playMenuItem.resume"), I18nUtil.localeProperty()));
    }

    private void changeToStart() {
        fxmlController.tooltipPlayButton.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("tooltip.playButton.start"), I18nUtil.localeProperty()));

        fxmlController.playMenuItem.textProperty().bind(Bindings.createStringBinding(
                () -> I18nUtil.i18n("playMenuItem"), I18nUtil.localeProperty()));
    }
}
