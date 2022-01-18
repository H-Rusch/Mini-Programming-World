package controller.example;

import controller.FXMLController;
import controller.program.ProgramController;
import controller.save.SaveController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import model.example.Example;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Optional;

public class ExampleController {

    private final Stage stage;
    private final SaveController saveController;
    private final FXMLController fxmlController;

    public ExampleController(SaveController saveController, Stage stage, FXMLController fxmlController) {
        this.stage = stage;
        this.saveController = saveController;
        this.fxmlController = fxmlController;

        setUpEventHandlers();
    }

    /** Add EventHandlers so the user can save and load the territory. */
    private void setUpEventHandlers() {
        fxmlController.saveExampleMenuItem.setOnAction(a -> storeExample());
        fxmlController.loadExampleMenuItem.setOnAction(a -> loadExample());
    }

    /**
     * Store an example in the database. An example consists of its name, the territories XML,
     * the programs code and its tags.
     * */
    private void storeExample() {
        // build a dialog to ask for tag input
        Optional<String[]> tags = askTagInput();
        if (tags.isPresent()) {
            try {
                DBConnection db = DBConnection.getInstance();

                String title = stage.getTitle();
                String name = title.substring(title.indexOf(":")).trim();
                String territoryXML = saveController.getTerritoryXMLString();
                String code = fxmlController.codeTextArea.getText();
                Example example = new Example(name, territoryXML, code, tags.get());

                db.saveExample(example);
            } catch (XMLStreamException e) {
                e.printStackTrace();
                // Alert
            }
        }
    }

    private void loadExample() {
        // build a dialog to ask for tag input
        Optional<String[]> tags = askTagInput();
        if (tags.isPresent()) {
            DBConnection db = DBConnection.getInstance();

            db.loadExample(tags.get());
        }


    }

    private Optional<String[]> askTagInput() {
        Optional<String[]> result = Optional.empty();
        try {
            FXMLLoader loader = new FXMLLoader(ProgramController.class.getResource("/fxml/TagInputDialog.fxml"));
            DialogPane dialogPane = loader.load();

            TagInputDialogController dialogController = loader.getController();

            Dialog<String[]> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Tags eingeben");
            dialog.setHeaderText("Durch Komma getrennte Tags eingeben");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return dialogController.getTags();
                }
                return null;
            });

            result = dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Beim Speichern ist ein Fehler aufgetreten.", ButtonType.OK)
                    .showAndWait();
        }
        return result;
    }
}
