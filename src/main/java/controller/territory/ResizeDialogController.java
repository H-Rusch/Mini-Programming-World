package controller.territory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.Validation;

/**
 * Dialog with two input fields. Only positive numbers are valid values for the input fields.
 */
public class ResizeDialogController {
    @FXML
    public Label rowLabel;
    @FXML
    public TextField rowInput;
    @FXML
    public Label columnLabel;
    @FXML
    public TextField columnInput;
    @FXML
    public ButtonType submitButton;
    @FXML
    public ButtonType cancelButton;
    @FXML
    public DialogPane dialogPane;


    /**
     * Add listeners to the input fields. When a valid value is given in both fields, the 'OK' Button is enabled, but if
     * a value is not valid, it is disabled. The input is valid if it is a numerical value in the interval [1, 100].
     */
    public void initialize() {
        columnInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (validValue(newValue) && validValue(rowInput.getText())) {
                dialogPane.lookupButton(submitButton).setDisable(false);
            } else {
                dialogPane.lookupButton(submitButton).setDisable(true);
            }
        }));

        rowInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (validValue(newValue) && validValue(columnInput.getText())) {
                dialogPane.lookupButton(submitButton).setDisable(false);
            } else {
                dialogPane.lookupButton(submitButton).setDisable(true);
            }
        }));

        // request focus in the input field after the components have been initialized
        Platform.runLater(() -> rowInput.requestFocus());
    }

    public void setRowInputText(String text) {
        rowInput.setText(text);
    }

    public void setColumnInputText(String text) {
        columnInput.setText(text);
    }

    public String getRowInputText() {
        return rowInput.getText();
    }

    public String getColumnInputText() {
        return columnInput.getText();
    }

    private boolean validValue(String str) {
        return Validation.isNumeric(str) && Integer.parseInt(str) > 0 && Integer.parseInt(str) <= 100;
    }
}
