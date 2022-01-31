package controller.program;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.I18nUtil;
import util.Validation;

/**
 * Dialog with one input field. When a valid value is given, the 'OK' button will be set active.
 * An input is valid if it is a correct java identifier and there is no file with the same name already.
 */
public class NewFileNameDialogController {
    @FXML
    public DialogPane dialogPane;
    @FXML
    public Label filenameLabel;
    @FXML
    public TextField filenameInput;
    @FXML
    public ButtonType submitButton;


    public void initialize() {
        filenameLabel.textProperty()
                .bind(Bindings.createStringBinding(() -> I18nUtil.i18n("dialog.program.filenameLabel"), I18nUtil.localeProperty()));


        dialogPane.lookupButton(submitButton).setDisable(true);

        filenameInput.textProperty().addListener(((observable, oldValue, newValue) ->
                dialogPane.lookupButton(submitButton).setDisable(!validValue(newValue))
        ));

        // request focus in the input field after the components have been initialized
        Platform.runLater(() -> filenameInput.requestFocus());
    }

    public String getFilenameInputText() {
        return filenameInput.getText();
    }

    public void setFilenameInput(String text) {
        this.filenameInput.setText(text);
    }

    private boolean validValue(String str) {
        return Validation.isJavaIdentifier(str) && !ProgramController.fileExists(str);
    }
}
