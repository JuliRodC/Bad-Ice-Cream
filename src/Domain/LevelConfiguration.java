package Domain;

/**
 * Configuracion de un nivel del juego
 * Contiene todos los parametros personalizables de un nivel
 */
public class LevelConfiguration {
    private final int bananaCount;
    private final int grapeCount;
    private final int cherryCount;
    private final int pineappleCount;
    private final int cactusCount;

    private final int trollCount;
    private final int squidCount;
    private final int potCount;
    private final int narvalCount;

    private final int iceBlockCount;
    private final int fireBlockCount;
    private final int specialBlockCount;
    private final int hotTileCount;

    private final int mapWidth;
    private final int mapHeight;
    private final int cellSize;

    private LevelConfiguration(Builder builder) {
        this.bananaCount = builder.bananaCount;
        this.grapeCount = builder.grapeCount;
        this.cherryCount = builder.cherryCount;
        this.pineappleCount = builder.pineappleCount;
        this.cactusCount = builder.cactusCount;

        this.trollCount = builder.trollCount;
        this.squidCount = builder.squidCount;
        this.potCount = builder.potCount;
        this.narvalCount = builder.narvalCount;

        this.iceBlockCount = builder.iceBlockCount;
        this.fireBlockCount = builder.fireBlockCount;
        this.specialBlockCount = builder.specialBlockCount;
        this.hotTileCount = builder.hotTileCount;

        this.mapWidth = builder.mapWidth;
        this.mapHeight = builder.mapHeight;
        this.cellSize = builder.cellSize;
    }

    // Getters
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
    public int getSpecialBlockCount() { return specialBlockCount; }
    public int getHotTileCount() { return hotTileCount; }

    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getCellSize() { return cellSize; }

    /**
     * Builder para crear configuraciones de nivel
     */
    public static class Builder {
        private int bananaCount = 20;
        private int grapeCount = 16;
        private int cherryCount = 0;
        private int pineappleCount = 0;
        private int cactusCount = 0;

        private int trollCount = 2;
        private int squidCount = 0;
        private int potCount = 0;
        private int narvalCount = 0;

        private int iceBlockCount = 80;
        private int fireBlockCount = 0;
        private int specialBlockCount = 0;
        private int hotTileCount = 0;

        private int mapWidth = 756;
        private int mapHeight = 756;
        private int cellSize = 42;

        public Builder setBananaCount(int count) {
            this.bananaCount = count;
            return this;
        }

        public Builder setGrapeCount(int count) {
            this.grapeCount = count;
            return this;
        }

        public Builder setCherryCount(int count) {
            this.cherryCount = count;
            return this;
        }

        public Builder setPineappleCount(int count) {
            this.pineappleCount = count;
            return this;
        }

        public Builder setCactusCount(int count) {
            this.cactusCount = count;
            return this;
        }

        public Builder setTrollCount(int count) {
            this.trollCount = count;
            return this;
        }

        public Builder setSquidCount(int count) {
            this.squidCount = count;
            return this;
        }


        public Builder setPotCount(int count) {
            this.potCount = count;
            return this;
        }


        public Builder setNarvalCount(int count) {
            this.narvalCount = count;
            return this;
        }

        public Builder setIceBlockCount(int count) {
            this.iceBlockCount = count;
            return this;
        }

        public Builder setFireBlockCount(int count) {
            this.fireBlockCount = count;
            return this;
        }

        public Builder setSpecialBlockCount(int count) {
            this.specialBlockCount = count;
            return this;
        }

        public Builder setHotTileCount(int count) {
            this.hotTileCount = count;
            return this;
        }

        public Builder setMapDimensions(int width, int height, int cellSize) {
            this.mapWidth = width;
            this.mapHeight = height;
            this.cellSize = cellSize;
            return this;
        }

        public LevelConfiguration build() {
            return new LevelConfiguration(this);
        }
    }

    /**
     * Crea una configuracion por defecto para el nivel 1
     */
    public static LevelConfiguration createDefaultLevel1() {
        return new Builder()
                .setBananaCount(20)
                .setGrapeCount(16)
                .setTrollCount(2)
                .setSquidCount(1)
                .setNarvalCount(0)
                .setIceBlockCount(80)
                .setHotTileCount(0)
                .setFireBlockCount(0)
                .build();
    }

    /**
     * Crea una configuracion desde GameSessionData
     */
    public static LevelConfiguration fromSessionData(Presentation.GameSessionData sessionData) {
        return new Builder()
                .setBananaCount(sessionData.getBananaCount())
                .setGrapeCount(sessionData.getGrapeCount())
                .setCherryCount(sessionData.getCherryCount())
                .setPineappleCount(sessionData.getPineappleCount())
                .setCactusCount(sessionData.getCactusCount())
                .setTrollCount(sessionData.getTrollCount())
                .setSquidCount(sessionData.getSquidCount())
                .setPotCount(sessionData .getPotCount())
                .setNarvalCount(sessionData.getNarvalCount())
                .setIceBlockCount(sessionData.getIceBlockCount())
                .setFireBlockCount(sessionData.getFireBlockCount())
                .setSpecialBlockCount(sessionData.getSpecialBlockCount())
                .setHotTileCount(sessionData.getHotTileCount())
                .build();
    }



}