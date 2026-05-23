package main.ui;

public class LevelConfig {
    private final int level;
    private final int targetScore;
    private final int maxMoves;
    private final String backgroundPath;
    private final String completePath;
    private final String losePath;

    public LevelConfig(int level, int targetScore, int maxMoves,
                       String backgroundPath, String completePath, String losePath) {
        this.level = level;
        this.targetScore = targetScore;
        this.maxMoves = maxMoves;
        this.backgroundPath = backgroundPath;
        this.completePath = completePath;
        this.losePath = losePath;
    }

    public int getLevel() { return level; }
    public int getTargetScore() { return targetScore; }
    public int getMaxMoves() { return maxMoves; }
    public String getBackgroundPath() { return backgroundPath; }
    public String getCompletePath() { return completePath; }
    public String getLosePath() { return losePath; }
}
