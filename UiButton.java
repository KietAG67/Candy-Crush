package main.ui;

import java.awt.*;

public class UiButton {
    private final Rectangle bounds;
    private final String text;

    public UiButton(int x, int y, int width, int height, String text) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
    }

    public boolean contains(int x, int y) { return bounds.contains(x, y); }
    public Rectangle getBounds() { return bounds; }

    public void draw(Graphics2D g) {
        g.setColor(new Color(214, 232, 239, 220));
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 18, 18);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 18, 18);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.PLAIN, Math.max(26, bounds.height / 2)));
        FontMetrics fm = g.getFontMetrics();
        int tx = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int ty = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, tx, ty);
    }
}
