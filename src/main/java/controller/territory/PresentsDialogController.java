package controller.territory;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.Validation;

public class PresentsDialogController {

    @FXML
    public DialogPane dialogPane;
    @FXML
    public Label presentsLabel;
    @FXML
    public TextField presentsInput;
    @FXML
    public ButtonType submitButton;
    @FXML
    public ButtonType cancelButton;

    /**
     * Add listener to the input field. Enables the 'OK' button when a numerical values is entered in the input
     * field. Disables the button otherwise.
     */
    public void initialize() {
        presentsInput.textProperty().addListener(((observable, oldValue, newValue) ->
                dialogPane.lookupButton(submitButton).setDisable(!Validation.isNumeric(newValue))));
    }

    public String getPresentsInputText() {
        return presentsInput.getText();
    }

    public void setPresentsInputText(String str) {
        presentsInput.setText(str);
    }
}
