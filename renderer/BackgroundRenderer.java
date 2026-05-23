package main.ui.renderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BackgroundRenderer {
    private final Map<String, BufferedImage> cache = new HashMap<>();

    public void draw(Graphics2D g, String path, int width, int height) {
        BufferedImage image = load(path);

        if (image != null) {

            double scale = Math.max(
                    (double) width / image.getWidth(),
                    (double) height / image.getHeight());

            int drawWidth = (int) (image.getWidth() * scale);
            int drawHeight = (int) (image.getHeight() * scale);

            int x = (width - drawWidth) / 2;
            int y = (height - drawHeight) / 2;

            g.drawImage(image, x, y, drawWidth, drawHeight, null);
            return;
        }

        drawFallback(g, width, height);
    }


    public void drawCropLeft(Graphics2D g, String path, int width, int height, int cropLeftPixels) {
        BufferedImage image = load(path);
        if (image != null) {
            int sx1 = Math.max(0, Math.min(cropLeftPixels, image.getWidth() - 1));
            g.drawImage(image, 0, 0, width, height,
                    sx1, 0, image.getWidth(), image.getHeight(), null);
            return;
        }
        drawFallback(g, width, height);
    }

    private BufferedImage load(String path) {
        if (path == null || path.isEmpty()) return null;
        if (cache.containsKey(path)) return cache.get(path);
        try {
            URL resource = getClass().getClassLoader().getResource(path);
            BufferedImage img = resource != null ? ImageIO.read(resource) : ImageIO.read(new File(path));
            cache.put(path, img);
            return img;
        } catch (IOException | IllegalArgumentException ex) {
            cache.put(path, null);
            return null;
        }
    }

    private void drawFallback(Graphics2D g, int width, int height) {
        GradientPaint gp = new GradientPaint(0, 0, new Color(120, 220, 245), 0, height, new Color(0, 185, 190));
        g.setPaint(gp);
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(255, 255, 255, 130));
        for (int i = 0; i < 12; i++) {
            g.fillOval(50 + i * 130, 80 + (i % 3) * 45, 80, 35);
        }
    }
}
