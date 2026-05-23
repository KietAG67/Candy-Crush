package main.ui.renderer;

import java.awt.*;

public class GameResultOverlayRenderer {
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();

    public void drawComplete(Graphics2D g, int w, int h, String imagePath) {
        backgroundRenderer.draw(g, imagePath, w, h);
    }

    public void drawLose(Graphics2D g, int w, int h, String imagePath) {
        backgroundRenderer.draw(g, imagePath, w, h);
    }
}
