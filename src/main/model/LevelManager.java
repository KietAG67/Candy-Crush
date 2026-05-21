package main.model;

public class LevelManager {
    private static LevelManager instance;
    private int currentLevel = 1;
    private int movesLeft = 25;
    private int targetScore = 500;
    private boolean gameOver = false;
    private boolean gameWon = false;

    private LevelManager() {}

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public void startLevel(int level) {
        this.currentLevel = level;
        this.movesLeft = 25;
        this.gameOver = false;
        this.gameWon = false;
        switch (level) {
            case 1: this.targetScore = 500; break;
            case 2: this.targetScore = 700; break;
            case 3: this.targetScore = 900; break;
            default: this.targetScore = 900; break;
        }
    }

    public void resetCurrentLevel() {
        startLevel(this.currentLevel);
    }

    public void nextLevel() {
        if (currentLevel < 3) {
            startLevel(currentLevel + 1);
        } else {
            // Beat the whole game
            gameWon = true;
            gameOver = true;
        }
    }

    public void decrementMove() {
        if (movesLeft > 0) movesLeft--;
    }
    
    public void setMovesLeft(int moves) {
        this.movesLeft = moves;
    }

    public int getCurrentLevel() { return currentLevel; }
    public int getMovesLeft() { return movesLeft; }
    public int getTargetScore() { return targetScore; }
    
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    
    public boolean isGameWon() { return gameWon; }
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }
}
