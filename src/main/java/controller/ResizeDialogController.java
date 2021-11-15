package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;

/**
 * Dialog with two input fields. Checks
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
    }

    public void setRowInputText(String text) {
        rowInput.setText(text);
    }

    public void setColumnInputText(String text) {
        columnInput.setText(text);
    }

    public Pair<String, String> getInputTexts() {
        return new Pair<>(columnInput.getText(), rowInput.getText());
    }

    private boolean validValue(String str) {
        return !str.equals("") && isNumeric(str)
                && Integer.parseInt(str) > 0 && Integer.parseInt(str) <= 100;
    }

    private boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
