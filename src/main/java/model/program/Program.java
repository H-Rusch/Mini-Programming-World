package model.program;

import controller.program.ProgramController;
import javafx.stage.Stage;

import java.io.File;

public class Program {

    private final String name;
    private Stage stage;

    public Program(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }

    public String getFullFileName() {
        return ProgramController.getFullFileName(name);
    }

    /** Load code for this program from file. */
    public String getCode() {
        return ProgramController.getProgramCode(getName());
    }
}
