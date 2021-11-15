package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Territory;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SimulatorView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 640);
        stage.setTitle("Super! Market - MPW");
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(this.getClass().getResource("/img/24x24/Present24.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}