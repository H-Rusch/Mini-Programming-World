package controller.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class TagInputDialogController {

    @FXML
    public ButtonType submitButton;
    @FXML
    public TextField tagInput;
    @FXML
    public DialogPane dialogPane;

    public void initialize() {
        dialogPane.lookupButton(submitButton).setDisable(true);

        tagInput.textProperty().addListener(((observable, oldValue, newValue) ->
                dialogPane.lookupButton(submitButton).setDisable(!validValue(newValue))
        ));

        // request focus in the input field after the components have been initialized
        Platform.runLater(() -> tagInput.requestFocus());
    }

    public String[] getTags() {
        return Arrays.stream(tagInput.getText().split(","))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .toArray(String[]::new);
    }

    /** String can not be empty or only filled with whitespaces. */
    private boolean validValue(String str) {
        return str.trim().length() >= 1 && str.replace(" ", "").split(",").length > 0;
    }
}
