package controller.program;

import controller.FXMLController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.program.Program;
import model.territory.Territory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProgramController {

    private static final String PREFIX1 = "import util.annotations.Invisible;\npublic class ";
    private static final String PREFIX2 = " extends model.territory.Actor { @Invisible public";
    private static final String SUFFIX = "}";

    private static final String FILE_EXTENSION = ".java";
    public static final String DIRECTORY = "programs";
    private static final String DEFAULT = "void main() {\n  \n}";

    private static final Map<String, Stage> stageMap = new HashMap<>();
    private static final Map<String, Program> programMap = new HashMap<>();

    private final FXMLController controller;
    private final Program program;
    private final Territory territory;
    private final Stage stage;

    public ProgramController(Program program, Stage stage, Territory territory, FXMLController controller) {
        this.program = program;
        this.stage = stage;
        this.territory = territory;
        this.controller = controller;

        controller.codeTextArea.setProgram(program);

        setUpEventHandlers();
    }

    /** Add EventHandlers for the interaction between buttons and the program. */
    private void setUpEventHandlers() {
        controller.newButton.setOnAction(a -> ProgramController.newProgram());
        controller.newMenuItem.setOnAction(a -> ProgramController.newProgram());

        controller.openButton.setOnAction(a -> ProgramController.openProgram(stage));
        controller.openMenuItem.setOnAction(a -> ProgramController.openProgram(stage));

        controller.saveButton.setOnAction(a -> saveProgram(program, controller.codeTextArea.getText()));
        controller.saveMenuItem.setOnAction(a -> saveProgram(program, controller.codeTextArea.getText()));

        controller.compileButton.setOnAction(a -> CompileController.compileProgram(program, controller.codeTextArea.getText(), territory));
        controller.compileMenuItem.setOnAction(a -> CompileController.compileProgram(program, controller.codeTextArea.getText(), territory));

        controller.exitMenuItem.setOnAction(a -> {
            ProgramController.saveProgramToFile(program, controller.codeTextArea.getText());
            ProgramController.removeProgram(program);
            stage.close();
        });

        stage.setOnCloseRequest(event -> {
            ProgramController.saveProgramToFile(program, controller.codeTextArea.getText());
            ProgramController.removeProgram(program);
        });
    }

    /** Non-static method which updates the notification label and calls the static method to do the actual saving. */
    public void saveProgram(Program program, String code) {
        controller.updateNotificationText("Programm abgespeichert");
        ProgramController.saveProgramToFile(program, code);
    }

    /* Static methods. */

    public static void addProgram(Program program, Stage stage) {
        programMap.put(program.getName(), program);
        stageMap.put(program.getName(), stage);
    }

    public static void removeProgram(Program program) {
        programMap.remove(program.getName());
        stageMap.remove(program.getName());
    }


    /** Checks if the program which belongs to a file is already open. */
    public static boolean isOpen(String filename) {
        for (Program program : programMap.values()) {
            if (program.getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public static String getFullFileName(String programName) {
        return ProgramController.DIRECTORY + File.separator + programName + ProgramController.FILE_EXTENSION;
    }

    /**
     * Start a simulator window for the program specified with its program name.
     *
     * @param programName name of the program to be launched
     */
    public static void startSimulatorStage(String programName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ProgramController.class.getResource("/fxml/SimulatorView.fxml"));
            Stage stage = fxmlLoader.load();
            stage.setTitle("Market MPW: " + programName);
            stage.getIcons().add(new Image(String.valueOf(ProgramController.class.getResource("/img/24x24/Present24.png"))));
            stage.getScene().getStylesheets().add("css/style.css");

            Program program = new Program(programName);
            ProgramController.addProgram(program, stage);
            Territory territory = new Territory(12, 15);
            // put some initial elements into the market
            territory.forcePlaceActor(0, 7);
            territory.placeShelf(5, 5);
            territory.placeShelf(4, 4);
            territory.placeShelf(4, 5);
            territory.placeShelf(4, 3);
            territory.placePresent(1, 4);
            territory.placePresent(8, 9);
            territory.placePresent(14, 3);
            territory.placeCart(1, 4);
            territory.placeCart(7, 9);

            CompileController.compileProgramSilently(program, territory);

            FXMLController controller = fxmlLoader.getController();
            controller.setTerritory(territory);
            controller.setStage(stage);
            controller.setProgram(program);

            controller.setUpControllers();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Simulator konnte nicht gestartet werden", ButtonType.OK).showAndWait();
        }
    }

    /** Checks whether a file specified by its filename exists in the 'programs' folder. */
    public static boolean fileExists(String filename) {
        Path path = Paths.get(getFullFileName(filename));
        return Files.exists(path);
    }

    /** Create the 'programs' directory if it does not exist already. */
    public static void createDirectoryIfNotExists() {
        Path path = Paths.get(DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the program code for a program identified by its name. If a file does not exist, it is created with
     * sample code.
     *
     * @param programName the name of a program
     * @return the program's code. Each line seperated with a line break. The first and last line containing
     * class definition and closing braces are not included.
     */
    public static String getProgramCode(String programName) {
        Path file = Paths.get(getFullFileName(programName));
        try {
            // create a file if it does not already exist
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
            List<String> lines = Files.readAllLines(file);

            // write initial content into the file if it is empty
            if (lines.isEmpty()) {
                lines.add(PREFIX1 + programName + PREFIX2);
                lines.add(DEFAULT);
                lines.add(SUFFIX);

                Files.write(file, lines);
            }

            // remove class definition and closing bracket
            lines.remove(0);
            lines.remove(0);
            lines.remove(lines.size() - 1);
            StringBuilder builder = new StringBuilder();
            lines.forEach(line -> builder.append(line).append("\n"));

            return builder.toString();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Konnte Programmdatei nicht laden.", ButtonType.OK).showAndWait();
        }
        return null;
    }

    /**
     * Save the program to its file.
     *
     * @param program the program to be saved
     * @param code    the code of the program to be saved in the file
     */
    public static void saveProgramToFile(Program program, String code) {
        Path file = Paths.get(program.getFullFileName());
        try {
            if (Files.notExists(file)) {
                return;
            }

            List<String> lines = new ArrayList<>();
            lines.add(PREFIX1 + program.getName() + PREFIX2);
            lines.addAll(Arrays.asList(code.split("\n")));
            lines.add(SUFFIX);

            Files.write(file, lines);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Konnte Programm nicht speichern.", ButtonType.OK).showAndWait();
        }
    }

    /**
     * Show a dialog where the user can input the name for a new program. If the chosen name is not already taken,
     * a new program will be created and a new window with that program active will be displayed.
     */
    public static void newProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(ProgramController.class.getResource("/fxml/NewFileNameDialogView.fxml"));
            DialogPane dialogPane = loader.load();

            NewFileNameDialogController controller = loader.getController();

            Dialog<String> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Dateinamen eingeben");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return controller.getFilenameInputText();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(ProgramController::startSimulatorStage);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Neues Programm konnte nicht erstellt werden.", ButtonType.OK)
                    .showAndWait();
        }
    }

    /**
     * Let the user choose a program to open with a FileChooser. The user can only open files from the 'programs'
     * directory and can only open the same file one time.
     *
     * @param stage the stage the FileChooser appears in
     */
    public static void openProgram(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files (*.java)",
                "*.java"));
        fileChooser.setInitialDirectory(new File(DIRECTORY));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // can only open files in the 'programs' folder
            if (!selectedFile.getParent().equals(Paths.get(DIRECTORY).toAbsolutePath().toString())) {
                new Alert(Alert.AlertType.ERROR, "Die angegebene Datei stammt nicht aus dem 'programs'-Verzeichnis",
                        ButtonType.OK).showAndWait();
                return;
            }
            // filename without the '.java'
            String filename = selectedFile.getName().substring(0, selectedFile.getName().indexOf("."));
            if (ProgramController.isOpen(filename)) {
                // move the simulator which has the program opened into the foreground
                stageMap.get(filename).requestFocus();
            } else {
                ProgramController.startSimulatorStage(filename);
            }
        }
    }
}
