package controller.simulation;

import controller.FXMLController;
import javafx.scene.control.Tooltip;
import model.simulation.Simulation;
import model.territory.Territory;


public class SimulationController {

    private Simulation simulation;
    private final Territory territory;
    private final FXMLController fxmlController;

    public SimulationController(Territory territory, FXMLController controller) {
        this.territory = territory;
        this.fxmlController = controller;

        setUpEventHandlers();
        setPauseButtonsEnabled(false);
        setStopButtonsEnabled(false);
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
    }

    /** Enable or disable the play buttons in the application. */
    private void setPlayButtonsEnabled(boolean enabled) {
        fxmlController.playButton.setDisable(!enabled);
        fxmlController.playMenuItem.setDisable(!enabled);
    }

    /** Enable or disable the pause buttons in the application. */
    private void setPauseButtonsEnabled(boolean enabled) {
        fxmlController.pauseButton.setDisable(!enabled);
        fxmlController.pauseMenuItem.setDisable(!enabled);
    }

    /** Enable or disable the stop buttons in the application. */
    private void setStopButtonsEnabled(boolean enabled) {
        fxmlController.stopButton.setDisable(!enabled);
        fxmlController.stopMenuItem.setDisable(!enabled);
    }

    /** Start the simulation. Disable unneeded buttons and change the 'play' buttons functionality from 'start' to 'continue'. */
    public void startSimulation() {
        setPlayButtonsEnabled(false);
        setPauseButtonsEnabled(true);
        setStopButtonsEnabled(true);

        fxmlController.playButton.setOnAction(event -> continueSimulation());
        fxmlController.playMenuItem.setOnAction(event -> continueSimulation());

        simulation = new Simulation(territory, this, fxmlController.speedSlider.getValue());
        simulation.setDaemon(true);
        simulation.start();
    }

    /** Unpause the simulation. Pausing and stopping is enabled. */
    public void continueSimulation() {
        setPlayButtonsEnabled(false);
        setPauseButtonsEnabled(true);
        setStopButtonsEnabled(true);

        fxmlController.playButton.setTooltip(new Tooltip("Setze die Simulation fort"));

        simulation.setPauseSimulation(false);
        simulation.notifySyncObject();
    }

    /** Pause the simulation. Continuing and stopping is enabled. */
    public void pauseSimulation() {
        setPlayButtonsEnabled(true);
        setPauseButtonsEnabled(false);
        setStopButtonsEnabled(true);

        simulation.setPauseSimulation(true);
    }

    /** Stop the simulation. Starting is enabled. */
    public void stopSimulation() {
        setPlayButtonsEnabled(true);
        setPauseButtonsEnabled(false);
        setStopButtonsEnabled(false);

        simulation.setStopSimulation(true);
        simulation.setPauseSimulation(false);
        simulation.notifySyncObject();
    }

    /** Enable buttons, so the simulation can be started again. */
    public void simulationEnded() {
        setPlayButtonsEnabled(true);
        setPauseButtonsEnabled(false);
        setStopButtonsEnabled(false);

        fxmlController.playButton.setTooltip(new Tooltip("Starte die Simulation"));

        fxmlController.playButton.setOnAction(event -> startSimulation());
        fxmlController.playMenuItem.setOnAction(event -> startSimulation());
    }
}
