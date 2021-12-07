package model.simulation;

import controller.simulation.SimulationController;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import model.territory.Territory;
import util.Observer;

public class Simulation extends Thread implements Observer {

    private Territory territory;
    private SimulationController controller;
    private final Object syncObject = new Object();

    private volatile double simulationSpeed;
    private volatile boolean pauseSimulation = false;
    private volatile boolean stopSimulation = false;

    public Simulation(Territory territory, SimulationController controller, double simulationSpeed) {
        this.territory = territory;
        this.controller = controller;
        this.simulationSpeed = simulationSpeed;
    }

    public void setSimulationSpeed(double speed) {
        this.simulationSpeed = speed;
    }

    public void setPauseSimulation(boolean pause) {
        this.pauseSimulation = pause;
    }

    public void setStopSimulation(boolean stop) {
        this.stopSimulation = stop;
    }

    /**
     * Run the simulation by executing the main-method the actor implements. After the simulation the UI is updated, so
     * the simulation can run again.
     */
    @Override
    public void run() {
        territory.addObserver(this);
        try {
            territory.getActor().main();
        } catch (StoppedSimulationException ignored) {
        } catch (RuntimeException e) {
            new AudioClip(String.valueOf(getClass().getResource("/sound/error_sound.mp3"))).play(0.2);
        } finally {
            territory.removeObserver(this);
            Platform.runLater(controller::simulationEnded);
        }
    }

    /**
     * Execute a step in the simulation and then wait a specific amount of time. If the user gives the signal to stop
     * the simulation, it will be stopped.
     */
    @Override
    public void update() {
        // don't call sleep on FXApplication Thread
        if (Platform.isFxApplicationThread()) {
            return;
        }

        // show the simulation step by step
        try {
            sleep((long) (500 - 4.5 * simulationSpeed));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        if (stopSimulation) throw new StoppedSimulationException();

        // pause the simulation by waiting
        synchronized (syncObject) {
            while (pauseSimulation) {
                try {
                    syncObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (stopSimulation) throw new StoppedSimulationException();
    }

    public void notifySyncObject() {
        synchronized (syncObject) {
            syncObject.notifyAll();
        }
    }
}
