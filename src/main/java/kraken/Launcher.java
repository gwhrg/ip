package kraken;

import javafx.application.Application;
import kraken.gui.Main;

/**
 * A launcher class to workaround classpath issues when launching JavaFX applications.
 */
public class Launcher {
    /**
     * Launches the JavaFX GUI.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

