package Domain;

public class SaveGameDTO {
    private String playerName;
    private String playerFlavor;
    private int playerGridX;
    private int playerGridY;
    private int score;
    private int collectedFruits;
    private int totalFruits;
    private long remainingTimeMs;

    private boolean bananasCompleted;
    private boolean grapesSpawned;
    private boolean grapesCompleted;
    private boolean cherriesSpawned;
    private boolean cherriesCompleted;
    private boolean pineapplesSpawned;
    private boolean pineapplesCompleted;
    private boolean cactusesSpawned;
    private boolean cactusesCompleted;

    private LevelConfigDTO levelConfig;

    public SaveGameDTO() {}

    // Getters y Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }

    public String getPlayerFlavor() { return playerFlavor; }
    public void setPlayerFlavor(String flavor) { this.playerFlavor = flavor; }

    public int getPlayerGridX() { return playerGridX; }
    public void setPlayerGridX(int x) { this.playerGridX = x; }

    public int getPlayerGridY() { return playerGridY; }
    public void setPlayerGridY(int y) { this.playerGridY = y; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCollectedFruits() { return collectedFruits; }
    public void setCollectedFruits(int count) { this.collectedFruits = count; }

    public int getTotalFruits() { return totalFruits; }
    public void setTotalFruits(int count) { this.totalFruits = count; }

    public long getRemainingTimeMs() { return remainingTimeMs; }
    public void setRemainingTimeMs(long time) { this.remainingTimeMs = time; }

    public boolean isBananasCompleted() { return bananasCompleted; }
    public void setBananasCompleted(boolean completed) { this.bananasCompleted = completed; }

    public boolean isGrapesSpawned() { return grapesSpawned; }
    public void setGrapesSpawned(boolean spawned) { this.grapesSpawned = spawned; }

    public boolean isGrapesCompleted() { return grapesCompleted; }
    public void setGrapesCompleted(boolean completed) { this.grapesCompleted = completed; }

    public boolean isCherriesSpawned() { return cherriesSpawned; }
    public void setCherriesSpawned(boolean spawned) { this.cherriesSpawned = spawned; }

    public boolean isCherriesCompleted() { return cherriesCompleted; }
    public void setCherriesCompleted(boolean completed) { this.cherriesCompleted = completed; }

    public boolean isPineapplesSpawned() { return pineapplesSpawned; }
    public void setPineapplesSpawned(boolean spawned) { this.pineapplesSpawned = spawned; }

    public boolean isPineapplesCompleted() { return pineapplesCompleted; }
    public void setPineapplesCompleted(boolean completed) { this.pineapplesCompleted = completed; }

    public boolean isCactusesSpawned() { return cactusesSpawned; }
    public void setCactusesSpawned(boolean spawned) { this.cactusesSpawned = spawned; }

    public boolean isCactusesCompleted() { return cactusesCompleted; }
    public void setCactusesCompleted(boolean completed) { this.cactusesCompleted = completed; }

    public LevelConfigDTO getLevelConfig() { return levelConfig; }
    public void setLevelConfig(LevelConfigDTO config) { this.levelConfig = config; }
}