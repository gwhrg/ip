package kraken.gui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility for generating simple placeholder avatars without bundling image resources.
 */
public final class Avatar {
    private static final int SIZE_PX = 100;

    private Avatar() {
        // Utility class.
    }

    /**
     * Creates a solid-color square avatar image.
     *
     * @param color fill color
     * @return an {@link Image} usable by JavaFX controls
     */
    public static Image solid(Color color) {
        WritableImage img = new WritableImage(SIZE_PX, SIZE_PX);
        PixelWriter writer = img.getPixelWriter();
        for (int y = 0; y < SIZE_PX; y++) {
            for (int x = 0; x < SIZE_PX; x++) {
                writer.setColor(x, y, color);
            }
        }
        return img;
    }
}

