package Domain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

/**
 * Maneja el guardado y carga del estado del juego
 * Incluye soporte para fogatas, baldosas calientes, configuraciones del nivel
 * y estado de oleadas de frutas
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 5L; // CAMBIAR VERSION

    private String playerName;
    private String playerFlavor;
    private int playerX;
    private int playerY;
    private int score;


    private boolean isTwoPlayerMode;
    private String player2Name;
    private String player2Flavor;
    private int player2GridX;
    private int player2GridY;
    private int player2Score;
    private int player2CollectedFruits;

    private boolean player2IsAI;
    private String player2AIType;



    private int collectedFruits;
    private int totalFruits;
    private long remainingTime;

    // NUEVO: Estado de oleadas
    private boolean bananasCompleted;
    private boolean grapesSpawned;
    private boolean grapesCompleted;
    private boolean cherriesSpawned;
    private boolean cherriesCompleted;
    private boolean pineapplesSpawned;
    private boolean pineapplesCompleted;
    private boolean cactusesSpawned;
    private boolean cactusesCompleted;

    private List<FruitData> fruitsData;
    private List<EnemyData> enemiesData;
    private List<IceBlockData> iceBlocksData;
    private List<CampfireData> campfiresData;
    private List<HotTileData> hotTilesData;

    private LevelConfigData levelConfig;

    public GameSaveData() {
        fruitsData = new ArrayList<>();
        enemiesData = new ArrayList<>();
        iceBlocksData = new ArrayList<>();
        campfiresData = new ArrayList<>();
        hotTilesData = new ArrayList<>();

        // Inicializar estado de oleadas
        bananasCompleted = false;
        grapesSpawned = false;
        grapesCompleted = false;
        cherriesSpawned = false;
        cherriesCompleted = false;
        pineapplesSpawned = false;
        pineapplesCompleted = false;
        cactusesSpawned = false;
        cactusesCompleted = false;
    }

    public boolean saveToFile(String filename) {
        try {
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }

            String filepath = "saves/" + filename + ".sav";
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();

            System.out.println("Juego guardado en: " + filepath);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar el juego: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static GameSaveData loadFromFile(String filename) {
        try {
            String filepath = "saves/" + filename + ".sav";
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            GameSaveData data = (GameSaveData) in.readObject();
            in.close();
            fileIn.close();

            System.out.println("Juego cargado desde: " + filepath);
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar el juego: " + e.getMessage());
            return null;
        }
    }

    // Getters y setters existentes
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPlayerFlavor() { return playerFlavor; }
    public void setPlayerFlavor(String playerFlavor) { this.playerFlavor = playerFlavor; }

    public int getPlayerX() { return playerX; }
    public void setPlayerX(int playerX) { this.playerX = playerX; }

    public int getPlayerY() { return playerY; }
    public void setPlayerY(int playerY) { this.playerY = playerY; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }


    public boolean isTwoPlayerMode() { return isTwoPlayerMode; }
    public void setTwoPlayerMode(boolean mode) { this.isTwoPlayerMode = mode; }

    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String name) { this.player2Name = name; }

    public String getPlayer2Flavor() { return player2Flavor; }
    public void setPlayer2Flavor(String flavor) { this.player2Flavor = flavor; }

    public int getPlayer2GridX() { return player2GridX; }
    public void setPlayer2GridX(int x) { this.player2GridX = x; }

    public int getPlayer2GridY() { return player2GridY; }
    public void setPlayer2GridY(int y) { this.player2GridY = y; }

    public int getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(int score) { this.player2Score = score; }

    public int getPlayer2CollectedFruits() { return player2CollectedFruits; }
    public void setPlayer2CollectedFruits(int count) { this.player2CollectedFruits = count; }


    public boolean isPlayer2AI() { return player2IsAI; }
    public void setPlayer2IsAI(boolean isAI) { this.player2IsAI = isAI; }

    public String getPlayer2AIType() { return player2AIType; }
    public void setPlayer2AIType(String aiType) { this.player2AIType = aiType; }



    public int getCollectedFruits() { return collectedFruits; }
    public void setCollectedFruits(int collectedFruits) { this.collectedFruits = collectedFruits; }

    public int getTotalFruits() { return totalFruits; }
    public void setTotalFruits(int totalFruits) { this.totalFruits = totalFruits; }

    public long getRemainingTime() { return remainingTime; }
    public void setRemainingTime(long remainingTime) { this.remainingTime = remainingTime; }

    // NUEVOS: Getters y setters para estado de oleadas
    public boolean isBananasCompleted() { return bananasCompleted; }
    public void setBananasCompleted(boolean bananasCompleted) { this.bananasCompleted = bananasCompleted; }

    public boolean isGrapesSpawned() { return grapesSpawned; }
    public void setGrapesSpawned(boolean grapesSpawned) { this.grapesSpawned = grapesSpawned; }

    public boolean isGrapesCompleted() { return grapesCompleted; }
    public void setGrapesCompleted(boolean grapesCompleted) { this.grapesCompleted = grapesCompleted; }

    public boolean isCherriesSpawned() { return cherriesSpawned; }
    public void setCherriesSpawned(boolean cherriesSpawned) { this.cherriesSpawned = cherriesSpawned; }

    public boolean isCherriesCompleted() { return cherriesCompleted; }
    public void setCherriesCompleted(boolean cherriesCompleted) { this.cherriesCompleted = cherriesCompleted; }

    public boolean isPineapplesSpawned() { return pineapplesSpawned; }
    public void setPineapplesSpawned(boolean pineapplesSpawned) { this.pineapplesSpawned = pineapplesSpawned; }

    public boolean isPineapplesCompleted() { return pineapplesCompleted; }
    public void setPineapplesCompleted(boolean pineapplesCompleted) { this.pineapplesCompleted = pineapplesCompleted; }

    public boolean isCactusesSpawned() { return cactusesSpawned; }
    public void setCactusesSpawned(boolean cactusesSpawned) { this.cactusesSpawned = cactusesSpawned; }

    public boolean isCactusesCompleted() { return cactusesCompleted; }
    public void setCactusesCompleted(boolean cactusesCompleted) { this.cactusesCompleted = cactusesCompleted; }

    public List<FruitData> getFruitsData() { return fruitsData; }
    public void setFruitsData(List<FruitData> fruitsData) { this.fruitsData = fruitsData; }

    public List<EnemyData> getEnemiesData() { return enemiesData; }
    public void setEnemiesData(List<EnemyData> enemiesData) { this.enemiesData = enemiesData; }

    public List<IceBlockData> getIceBlocksData() { return iceBlocksData; }
    public void setIceBlocksData(List<IceBlockData> iceBlocksData) { this.iceBlocksData = iceBlocksData; }

    public List<CampfireData> getCampfiresData() { return campfiresData; }
    public void setCampfiresData(List<CampfireData> campfiresData) { this.campfiresData = campfiresData; }

    public List<HotTileData> getHotTilesData() { return hotTilesData; }
    public void setHotTilesData(List<HotTileData> hotTilesData) { this.hotTilesData = hotTilesData; }

    public LevelConfigData getLevelConfig() { return levelConfig; }
    public void setLevelConfig(LevelConfigData levelConfig) { this.levelConfig = levelConfig; }

    // Clases internas existentes (FruitData, EnemyData, etc.)
    public static class FruitData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int gridX;
        private int gridY;
        private boolean collected;
        private String fruitType;

        public FruitData(int gridX, int gridY, boolean collected, String fruitType) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.collected = collected;
            this.fruitType = fruitType;
        }

        public int getGridX() { return gridX; }
        public int getGridY() { return gridY; }
        public boolean isCollected() { return collected; }
        public String getFruitType() { return fruitType; }
    }

    public static class EnemyData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int gridX;
        private int gridY;
        private boolean active;
        private String enemyType;
        private String direction;

        public EnemyData(int gridX, int gridY, boolean active, String enemyType, String direction) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.active = active;
            this.enemyType = enemyType;
            this.direction = direction;
        }

        public int getGridX() { return gridX; }
        public int getGridY() { return gridY; }
        public boolean isActive() { return active; }
        public String getEnemyType() { return enemyType; }
        public String getDirection() { return direction; }
    }

    public static class IceBlockData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int gridX;
        private int gridY;
        private String blockType;

        public IceBlockData(int gridX, int gridY, String blockType) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.blockType = blockType;
        }

        public int getGridX() { return gridX; }
        public int getGridY() { return gridY; }
        public String getBlockType() { return blockType; }
    }

    public static class CampfireData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int gridX;
        private int gridY;
        private boolean isLit;
        private boolean hasIceAbove;

        public CampfireData(int gridX, int gridY, boolean isLit, boolean hasIceAbove) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.isLit = isLit;
            this.hasIceAbove = hasIceAbove;
        }

        public int getGridX() { return gridX; }
        public int getGridY() { return gridY; }
        public boolean isLit() { return isLit; }
        public boolean hasIceAbove() { return hasIceAbove; }
    }

    public static class HotTileData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int gridX;
        private int gridY;

        public HotTileData(int gridX, int gridY) {
            this.gridX = gridX;
            this.gridY = gridY;
        }

        public int getGridX() { return gridX; }
        public int getGridY() { return gridY; }
    }

    public static class LevelConfigData implements Serializable {
        private static final long serialVersionUID = 5L;

        private int bananaCount;
        private int grapeCount;
        private int cherryCount;
        private int pineappleCount;
        private int cactusCount;

        private int trollCount;
        private int squidCount;
        private int potCount;
        private int narvalCount;

        private int iceBlockCount;
        private int fireBlockCount;
        private int hotTileCount;

        public LevelConfigData(int bananaCount, int grapeCount, int cherryCount,
                               int pineappleCount, int cactusCount, int trollCount,
                               int squidCount, int potCount, int narvalCount,
                               int iceBlockCount, int fireBlockCount, int hotTileCount) {
            this.bananaCount = bananaCount;
            this.grapeCount = grapeCount;
            this.cherryCount = cherryCount;
            this.pineappleCount = pineappleCount;
            this.cactusCount = cactusCount;
            this.trollCount = trollCount;
            this.squidCount = squidCount;
            this.potCount = potCount;
            this.narvalCount = narvalCount;
            this.iceBlockCount = iceBlockCount;
            this.fireBlockCount = fireBlockCount;
            this.hotTileCount = hotTileCount;
        }

        public int getBananaCount() { return bananaCount; }
        public int getGrapeCount() { return grapeCount; }
        public int getCherryCount() { return cherryCount; }
        public int getPineappleCount() { return pineappleCount; }
        public int getCactusCount() { return cactusCount; }
        public int getTrollCount() { return trollCount; }
        public int getSquidCount() { return squidCount; }
        public int getPotCount() { return potCount; }
        public int getNarvalCount() { return narvalCount; }
        public int getIceBlockCount() { return iceBlockCount; }
        public int getFireBlockCount() { return fireBlockCount; }
        public int getHotTileCount() { return hotTileCount; }
    }
}
