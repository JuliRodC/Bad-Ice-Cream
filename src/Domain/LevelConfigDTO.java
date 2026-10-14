package Domain;

public class LevelConfigDTO {
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

    public LevelConfigDTO() {}

    public LevelConfigDTO(int banana, int grape, int cherry, int pineapple, int cactus,
                          int troll, int squid, int pot, int narval,
                          int ice, int fire, int hot) {
        this.bananaCount = banana;
        this.grapeCount = grape;
        this.cherryCount = cherry;
        this.pineappleCount = pineapple;
        this.cactusCount = cactus;
        this.trollCount = troll;
        this.squidCount = squid;
        this.potCount = pot;
        this.narvalCount = narval;
        this.iceBlockCount = ice;
        this.fireBlockCount = fire;
        this.hotTileCount = hot;
    }

    // Getters y Setters
    public int getBananaCount() { return bananaCount; }
    public void setBananaCount(int count) { this.bananaCount = count; }

    public int getGrapeCount() { return grapeCount; }
    public void setGrapeCount(int count) { this.grapeCount = count; }

    public int getCherryCount() { return cherryCount; }
    public void setCherryCount(int count) { this.cherryCount = count; }

    public int getPineappleCount() { return pineappleCount; }
    public void setPineappleCount(int count) { this.pineappleCount = count; }

    public int getCactusCount() { return cactusCount; }
    public void setCactusCount(int count) { this.cactusCount = count; }

    public int getTrollCount() { return trollCount; }
    public void setTrollCount(int count) { this.trollCount = count; }

    public int getSquidCount() { return squidCount; }
    public void setSquidCount(int count) { this.squidCount = count; }

    public int getPotCount() { return potCount; }
    public void setPotCount(int count) { this.potCount = count; }

    public int getNarvalCount() { return narvalCount; }
    public void setNarvalCount(int count) { this.narvalCount = count; }

    public int getIceBlockCount() { return iceBlockCount; }
    public void setIceBlockCount(int count) { this.iceBlockCount = count; }

    public int getFireBlockCount() { return fireBlockCount; }
    public void setFireBlockCount(int count) { this.fireBlockCount = count; }

    public int getHotTileCount() { return hotTileCount; }
    public void setHotTileCount(int count) { this.hotTileCount = count; }
}