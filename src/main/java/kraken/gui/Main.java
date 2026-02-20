package kraken.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kraken.Kraken;

/**
 * A JavaFX GUI for Kraken using FXML.
 */
public class Main extends Application {
    private final Kraken kraken = new Kraken();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Kraken");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setKraken(kraken);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

