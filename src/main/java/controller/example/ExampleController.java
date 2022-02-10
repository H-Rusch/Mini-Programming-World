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
import util.I18nUtil;

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
                // save the example in the database
                DBConnection db = DBConnection.getInstance();

                String title = stage.getTitle();
                String name = title.substring(title.indexOf(":") + 2).trim();
                String code = fxmlController.codeTextArea.getText();
                String territoryXML = saveController.getTerritoryXMLString();
                Example example = new Example(name, code, territoryXML, tags.get());

                db.saveExample(example);

                fxmlController.updateNotificationText(I18nUtil.i18n("notification.example.saved"));
            } catch (XMLStreamException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.example.savingError"),
                        ButtonType.OK).show();
            }
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
            List<String> shortExamples = db.loadExamplesForTags(tags.get());

            if (shortExamples == null) {
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.example.communicationError"), ButtonType.OK).show();
            } else if (shortExamples.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, I18nUtil.i18n("alert.example.noExamplesForTags"),
                        ButtonType.OK).show();
            } else {
                // build another dialog to ask the user to select one of the listed examples
                Optional<Integer> exampleID = askExampleChoice(shortExamples);
                if (exampleID.isPresent()) {
                    Example example = db.loadExampleForId(exampleID.get());
                    if (example == null) {
                        new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.example.loadingError"),
                                ButtonType.OK).show();
                    } else {
                        fxmlController.codeTextArea.setText(example.getCode());
                        saveController.loadTerritoryFromXMLString(example.getTerritoryString());

                        fxmlController.updateNotificationText(I18nUtil.i18n("notification.example.loaded"));
                    }
                }
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
            dialog.setTitle(I18nUtil.i18n("dialog.tag.title"));
            dialog.setHeaderText(I18nUtil.i18n("dialog.tag.header"));

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

    /** Show an input dialog asking the user to select one of multiple examples. */
    private Optional<Integer> askExampleChoice(List<String> shortExamples) {
        Optional<Integer> result = Optional.empty();
        try {
            FXMLLoader loader = new FXMLLoader(ProgramController.class.getResource("/fxml/ExampleChoiceDialog.fxml"));
            DialogPane dialogPane = loader.load();

            ExampleChoiceDialogController dialogController = loader.getController();
            dialogController.setChoices(shortExamples);

            Dialog<Integer> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(I18nUtil.i18n("dialog.example.title"));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return dialogController.getIdOfChoice();
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
