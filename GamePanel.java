package main.ui;

import main.GameManager;
import main.model.Board;
import main.model.Candy;
import main.model.ScoreManager;
import main.logic.SelectionController;
import main.ui.renderer.BackgroundRenderer;
import main.ui.renderer.GameResultOverlayRenderer;

import javax.swing.JPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel {
    private final GameManager gameManager;
    private final ScoreManager scoreManager = ScoreManager.getInstance();
    private final SelectionController selectionController;
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final GameResultOverlayRenderer resultOverlayRenderer = new GameResultOverlayRenderer();
    private final Map<Integer, LevelConfig> levels = new HashMap<>();

    public static final int CELL_SIZE = 60;

    private static final double DESIGN_W = 1600.0;
    private static final double DESIGN_H = 900.0;

    private static final double PLAY_X = 674.0 / DESIGN_W;
    private static final double PLAY_Y = 612.0 / DESIGN_H;
    private static final double PLAY_W = 250.0 / DESIGN_W;
    private static final double PLAY_H = 155.0 / DESIGN_H;

    private static final double[] LEVEL_CENTER_X = {
            440.0 / DESIGN_W,
            795.0 / DESIGN_W,
            1184.0 / DESIGN_W
    };
    private static final double LEVEL_CENTER_Y = 492.0 / DESIGN_H;
    private static final double LEVEL_RADIUS = 95.0 / DESIGN_W;

    private GameScreen screen = GameScreen.START;
    private LevelConfig currentLevel;
    private int movesLeft;

    public GamePanel(GameManager gameManager, SelectionController selectionController) {
        this.gameManager = gameManager;
        this.selectionController = selectionController;
        setFocusable(true);

        levels.put(1, new LevelConfig(1, 300, 18,
                "assets/background_level1.png",
                "assets/result_complete.png",
                "assets/result_lose.png"));
        levels.put(2, new LevelConfig(2, 450, 18,
                "assets/background_level2.png",
                "assets/result_complete_level2.png",
                "assets/result_lose_level2.png"));
        levels.put(3, new LevelConfig(3, 600, 18,
                "assets/background_level3.jpg",
                "assets/result_complete_level3.png",
                "assets/result_lose_level3.png"));
    }

    public boolean isPlaying() {
        return screen == GameScreen.PLAYING;
    }

    public void handleMouseClick(int x, int y) {
        if (screen == GameScreen.START) {
            if (getPlayHitBox().contains(x, y)) {
                screen = GameScreen.LEVEL_SELECT;
                repaint();
            }
            return;
        }

        if (screen == GameScreen.LEVEL_SELECT) {
            int level = getLevelAt(x, y);
            if (level > 0) {
                startLevel(level);
            }
            return;
        }

        if (screen == GameScreen.MISSION_COMPLETE || screen == GameScreen.YOU_LOSE) {
            screen = GameScreen.LEVEL_SELECT;
            repaint();
        }
    }

    public int getBoardX() {
        Board board = gameManager.getBoard();
        int cols = board == null ? 8 : board.getCols();
        int boardWidth = cols * CELL_SIZE;
        return (getWidth() - boardWidth) / 2;
    }

    public int getBoardY() {
        Board board = gameManager.getBoard();
        int rows = board == null ? 8 : board.getRows();
        int boardHeight = rows * CELL_SIZE;
        // Đẩy nhẹ xuống dưới để không đè HUD, nhưng vẫn nằm giữa màn hình.
        return Math.max(110, (getHeight() - boardHeight) / 2 + 45);
    }

    public boolean isInsideBoard(int x, int y) {
        Board board = gameManager.getBoard();
        if (board == null) return false;
        int bx = getBoardX();
        int by = getBoardY();
        int w = board.getCols() * CELL_SIZE;
        int h = board.getRows() * CELL_SIZE;
        return x >= bx && x < bx + w && y >= by && y < by + h;
    }

    public int getBoardColFromMouse(int mouseX) {
        return (mouseX - getBoardX()) / CELL_SIZE;
    }

    public int getBoardRowFromMouse(int mouseY) {
        return (mouseY - getBoardY()) / CELL_SIZE;
    }

    public void consumeMoveAfterSwap() {
        if (screen != GameScreen.PLAYING || currentLevel == null) return;
        movesLeft = Math.max(0, movesLeft - 1);
        checkResult();
        repaint();
    }

    private void startLevel(int levelNumber) {
        currentLevel = levels.get(levelNumber);
        movesLeft = currentLevel.getMaxMoves();
        resetScoreToZero();
        screen = GameScreen.PLAYING;

        try {
            gameManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        repaint();
    }

    private void checkResult() {
        if (currentLevel == null) return;
        if (screen != GameScreen.PLAYING) return;
        if (scoreManager.getScore() >= currentLevel.getTargetScore()) {
            screen = GameScreen.MISSION_COMPLETE;
            resetScoreToZero();
        } else if (movesLeft <= 0) {
            screen = GameScreen.YOU_LOSE;
            resetScoreToZero();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Kiểm tra thắng/thua liên tục sau khi game cập nhật điểm.
        // Vì điểm thường tăng sau animation match, không phải ngay lúc click.
        if (screen == GameScreen.PLAYING) {
            checkResult();
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (screen == GameScreen.START) {
            drawStart(g2);
        } else if (screen == GameScreen.LEVEL_SELECT) {
            drawLevelSelect(g2);
        } else {
            drawGame(g2);
        }

        if (screen == GameScreen.MISSION_COMPLETE) {
            resultOverlayRenderer.drawComplete(g2, getWidth(), getHeight(), getCompleteResultPath());
        }
        if (screen == GameScreen.YOU_LOSE) {
            resultOverlayRenderer.drawLose(g2, getWidth(), getHeight(), getLoseResultPath());
        }

        g2.dispose();
    }

    private String getCompleteResultPath() {
        return currentLevel == null ? "assets/result_complete.png" : currentLevel.getCompletePath();
    }

    private String getLoseResultPath() {
        return currentLevel == null ? "assets/result_lose.png" : currentLevel.getLosePath();
    }

    private void resetScoreToZero() {
        try {
            scoreManager.getClass().getMethod("resetScore").invoke(scoreManager);
            return;
        } catch (Exception ignored) { }

        try {
            scoreManager.getClass().getMethod("reset").invoke(scoreManager);
            return;
        } catch (Exception ignored) { }

        try {
            java.lang.reflect.Field scoreField = scoreManager.getClass().getDeclaredField("score");
            scoreField.setAccessible(true);
            scoreField.setInt(scoreManager, 0);
        } catch (Exception ignored) {
            // Nếu class ScoreManager của bạn có tên biến khác, thêm hàm resetScore() vào ScoreManager.
        }
    }

    private void drawStart(Graphics2D g) {
        backgroundRenderer.draw(g, "assets/background_start.png", getWidth(), getHeight());
        // Không vẽ thêm nút Play nữa, vì nút Play đã nằm sẵn trong background PowerPoint.
        // Như vậy khung Play sẽ khớp đúng với thiết kế của bạn.
    }

    private void drawLevelSelect(Graphics2D g) {
        backgroundRenderer.draw(g, "assets/background_level_select.png", getWidth(), getHeight());
        // Không vẽ thêm 3 ô level nữa, vì 3 vòng tròn đã có sẵn trong background.
        // Click được canh theo đúng vị trí 3 vòng tròn trong ảnh.
    }

    private Rectangle getPlayHitBox() {
        return new Rectangle(
                (int) (getWidth() * PLAY_X),
                (int) (getHeight() * PLAY_Y),
                (int) (getWidth() * PLAY_W),
                (int) (getHeight() * PLAY_H)
        );
    }

    private int getLevelAt(int mx, int my) {
        int radius = (int) (getWidth() * LEVEL_RADIUS);
        int cy = (int) (getHeight() * LEVEL_CENTER_Y);

        for (int i = 0; i < 3; i++) {
            int cx = (int) (getWidth() * LEVEL_CENTER_X[i]);
            int dx = mx - cx;
            int dy = my - cy;
            if (dx * dx + dy * dy <= radius * radius) {
                return i + 1;
            }
        }
        return 0;
    }

    private void drawGame(Graphics2D g) {
        String bg = currentLevel == null ? "assets/background_level1.png" : currentLevel.getBackgroundPath();
        backgroundRenderer.draw(g, bg, getWidth(), getHeight());
        drawHud(g);
        drawBoardFrame(g);
        drawBoard(g);
        drawCandies(g);
        drawSelector(g);
    }

    private void drawHud(Graphics2D g) {
        int hudW = 520;
        int hudX = (getWidth() - hudW) / 2;
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRoundRect(hudX, 24, hudW, 72, 24, 24);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));

        String levelText = currentLevel == null ? "LEVEL" : "LEVEL " + currentLevel.getLevel();
        String target = currentLevel == null ? "0" : String.valueOf(currentLevel.getTargetScore());
        String text = levelText + "   Score: " + scoreManager.getScore() + "/" + target + "   Moves: " + movesLeft;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, hudX + (hudW - fm.stringWidth(text)) / 2, 68);
    }

    private void drawBoardFrame(Graphics2D g) {
        Board board = gameManager.getBoard();
        if (board == null) return;

        int bx = getBoardX();
        int by = getBoardY();
        int bw = board.getCols() * CELL_SIZE;
        int bh = board.getRows() * CELL_SIZE;

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(bx - 18, by - 18, bw + 36, bh + 36, 34, 34);

        g.setColor(new Color(255, 255, 255, 210));
        g.setStroke(new BasicStroke(5));
        g.drawRoundRect(bx - 18, by - 18, bw + 36, bh + 36, 34, 34);
    }

    private void drawBoard(Graphics2D g) {
        Board board = gameManager.getBoard();
        if (board == null) return;

        int bx = getBoardX();
        int by = getBoardY();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                int x = bx + c * CELL_SIZE;
                int y = by + r * CELL_SIZE;
                g.setColor(new Color(255, 255, 255, 95));
                g.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 14, 14);
                g.setColor(new Color(255, 255, 255, 180));
                g.drawRoundRect(x, y, CELL_SIZE, CELL_SIZE, 14, 14);
            }
        }
    }

    private void drawCandies(Graphics2D g) {
        Board board = gameManager.getBoard();
        if (board == null) return;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Candy candy = board.getCandy(r, c);
                if (candy != null) drawCandy(g, candy);
            }
        }
    }

    private void drawCandy(Graphics2D g, Candy candy) {
        int x = getBoardX() + (int) (candy.getVisualX() * CELL_SIZE);
        int y = getBoardY() + (int) (candy.getVisualY() * CELL_SIZE);
        int padding = 8;

        g.setColor(candy.getType().getColor());
        g.fillOval(x + padding, y + padding, CELL_SIZE - 2 * padding, CELL_SIZE - 2 * padding);

        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(x + padding + 7, y + padding + 6, 16, 11);

        main.ui.renderer.SpecialCandyRendererRegistry.getInstance()
                .render(candy.getSpecialType(), g, x, y, CELL_SIZE, padding);
    }

    private void drawSelector(Graphics2D g) {
        int r = selectionController.getSelectedRow();
        int c = selectionController.getSelectedCol();
        if (r != -1 && c != -1) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4));
            g.drawRoundRect(getBoardX() + c * CELL_SIZE, getBoardY() + r * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE, 14, 14);
        }
    }
}
