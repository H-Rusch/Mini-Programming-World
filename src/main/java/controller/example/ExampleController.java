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
import java.sql.SQLException;
import java.util.List;
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
     */
    private void storeExample() {
        // build a dialog to ask for tag input
        Optional<String[]> tags = askTagInput();
        if (tags.isPresent()) {
            try {
                DBConnection db = DBConnection.getInstance();

                String title = stage.getTitle();
                String name = title.substring(title.indexOf(":") + 2).trim();
                String territoryXML = saveController.getTerritoryXMLString();
                String code = fxmlController.codeTextArea.getText();
                Example example = new Example(name, territoryXML, code, tags.get());

                db.saveExample(example);
            } catch (XMLStreamException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Beim Speichern des Beispiels ist ein Fehler aufgetreten.",
                        ButtonType.OK).show();
            }
            fxmlController.updateNotificationText("Das Beispiel wurde in der Datenbank gespeichert.");
        }
    }

    /**
     * Load an example from the database. The user can search by giving multiple tags as an input and the entries will
     * be listed based on how many tags are fitting.
     */
    private void loadExample() {
        // build a dialog to ask for tag input
        Optional<String[]> tags = askTagInput();
        if (tags.isPresent()) {
            DBConnection db = DBConnection.getInstance();

            // load the short form "id - name" from the database
            List<String> shortExamples = db.loadExample(tags.get());

            if (shortExamples.isEmpty()) {
                System.out.println("list empty");
            } else {
                // build another dialog to ask the user to select one of the listed examples
                // TODO
                shortExamples.forEach(System.out::println);
            }


        }
    }

    /** Show an input dialog asking the user to input multiple tags separated by a comma. */
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
        }
        return result;
    }
}
