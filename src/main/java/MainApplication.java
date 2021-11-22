import controller.program.ProgramController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ProgramController.createDirectoryIfNotExists();
        ProgramController.startSimulatorStage("DefaultCustomer");
    }

    public static void main(String[] args) {
        launch();
    }
}
