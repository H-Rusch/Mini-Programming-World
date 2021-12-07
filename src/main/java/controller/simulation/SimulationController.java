package controller.simulation;

import controller.MainController;
import javafx.scene.control.Tooltip;
import model.simulation.Simulation;
import model.territory.Territory;


public class SimulationController {

    private Simulation simulation;
    private final Territory territory;
    private MainController controller;

    public SimulationController(Territory territory) {
        this.territory = territory;
    }

    public void setMainController(MainController controller) {
        this.controller = controller;

        setUpEventHandlers();
        setPauseButtonsEnabled(false);
        setStopButtonsEnabled(false);
    }

    /** Connect buttons and menu items with corresponding event handlers. */
    private void setUpEventHandlers() {
        controller.speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (simulation != null) {
                simulation.setSimulationSpeed((Double) newValue);
            }
        });

        controller.playButton.setOnAction(event -> startSimulation());
        controller.playMenuItem.setOnAction(event -> startSimulation());

        controller.pauseButton.setOnAction(event -> pauseSimulation());
        controller.pauseMenuItem.setOnAction(event -> pauseSimulation());

        controller.stopButton.setOnAction(event -> stopSimulation());
        controller.stopMenuItem.setOnAction(event -> stopSimulation());
    }

    /** Enable or disable the play buttons in the application. */
    private void setPlayButtonsEnabled(boolean enabled) {
        controller.playButton.setDisable(!enabled);
        controller.playMenuItem.setDisable(!enabled);
    }

    /** Enable or disable the pause buttons in the application. */
    private void setPauseButtonsEnabled(boolean enabled) {
        controller.pauseButton.setDisable(!enabled);
        controller.pauseMenuItem.setDisable(!enabled);
    }

    /** Enable or disable the stop buttons in the application. */
    private void setStopButtonsEnabled(boolean enabled) {
        controller.stopButton.setDisable(!enabled);
        controller.stopMenuItem.setDisable(!enabled);
    }

    /** Start the simulation. Disable unneeded buttons and change the 'play' buttons functionality from 'start' to 'continue'. */
    public void startSimulation() {
        setPlayButtonsEnabled(false);
        setPauseButtonsEnabled(true);
        setStopButtonsEnabled(true);

        controller.playButton.setOnAction(event -> continueSimulation());
        controller.playMenuItem.setOnAction(event -> continueSimulation());

        simulation = new Simulation(territory, this, controller.speedSlider.getValue());
        simulation.start();
    }

    /** Unpause the simulation. Pausing and stopping is enabled. */
    public void continueSimulation() {
        setPlayButtonsEnabled(false);
        setPauseButtonsEnabled(true);
        setStopButtonsEnabled(true);

        controller.playButton.setTooltip(new Tooltip("Setze die Simulation fort"));

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

        controller.playButton.setTooltip(new Tooltip("Starte die Simulation"));

        controller.playButton.setOnAction(event -> startSimulation());
        controller.playMenuItem.setOnAction(event -> startSimulation());
    }
}
