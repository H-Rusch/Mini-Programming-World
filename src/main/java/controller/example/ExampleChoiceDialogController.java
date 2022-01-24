package controller.example;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;

import java.util.List;

public class ExampleChoiceDialogController {
    @FXML
    public ChoiceBox<String> exampleChoice;
    @FXML
    public DialogPane dialogPane;
    @FXML
    public ButtonType submitButton;

    public void initialize() {
        dialogPane.lookupButton(submitButton).setDisable(true);
    }

    public void setChoices(List<String> shortExamples) {
        exampleChoice.getItems().addAll(shortExamples);

        exampleChoice.getSelectionModel().select(0);

        dialogPane.lookupButton(submitButton).setDisable(false);
    }

    public Integer getIdOfChoice() {
        String choice = exampleChoice.getValue();
        return Integer.parseInt(choice.substring(0, choice.indexOf(" - ")));
    }
}
