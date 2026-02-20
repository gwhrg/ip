package kraken.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kraken.Kraken;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Kraken kraken;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user_avatar.png"));
    private final Image krakenImage = new Image(this.getClass().getResourceAsStream("/images/kraken_avatar.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Kraken} instance.
     */
    public void setKraken(Kraken k) {
        kraken = k;
        dialogContainer.getChildren().add(DialogBox.getKrakenDialog(kraken.getWelcomeMessage(), krakenImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Kraken's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = kraken.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getKrakenDialog(response, krakenImage)
        );
        userInput.clear();

        if (kraken.isExit()) {
            Platform.exit();
        }
    }
}

