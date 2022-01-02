package controller.save;

import controller.FXMLController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.territory.Territory;

import javax.imageio.ImageIO;
import java.io.*;


public class SaveController {

    private final FXMLController fxmlController;
    private final Stage stage;
    private final Territory territory;

    public SaveController(Territory territory, Stage stage, FXMLController controller) {
        this.fxmlController = controller;
        this.stage = stage;
        this.territory = territory;

        setUpEventHandlers();
    }

    /** Add EventHandlers so the user can save and load the territory. */
    private void setUpEventHandlers() {
        fxmlController.serializeMenuItem.setOnAction(a -> serializeTerritory());
        fxmlController.deserializeMenuItem.setOnAction(a -> deserializeTerritory());

        fxmlController.saveImageMenuItem.setOnAction(a -> saveScreenshot());
    }

    /** Save the territory to a file by serializing it. */
    public void serializeTerritory() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory Files (*.ter)",
                "*.ter"));
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(selectedFile.getAbsolutePath())))) {
                out.writeObject(territory);
                // save the state, so the territory can be reset to this state
                territory.saveState();
                fxmlController.updateNotificationText("Territorium gespeichert");
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Beim Speichern des Territoriums ist ein Fehler aufgetreten",
                        ButtonType.OK).show();
            }
        }
    }

    /** Load a territory from a file by deserializing it. */
    public void deserializeTerritory() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory Files (*.ter)",
                "*.ter"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(selectedFile.getAbsolutePath())))) {
                Territory loaded = (Territory) in.readObject();

                this.territory.loadTerritory(loaded);
                // save the state, so the territory can be reset to this state
                this.territory.saveState();
                fxmlController.updateNotificationText("Territorium geladen");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Beim Laden des Territoriums ist ein Fehler aufgetreten",
                        ButtonType.OK).show();
            }
        }
    }

    /** Save a screenshot of the TerritoryPanel and save it to the disk */
    public void saveScreenshot() {
        WritableImage screenshot = fxmlController.territoryPanel.snapshot(null, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png, *.gif, *.jpg)",
                "*.png", "*.gif", "*.jpg"));
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Beim Speichern des Bildes ist ein Fehler aufgetreten",
                        ButtonType.OK).show();
            }
        }
    }
}
