package Presentation;

public class GameSessionData {
    // Jugador 1
    private String player1Name;
    private String player1Flavor;

    // Jugador 2
    private String player2Name;
    private String player2Flavor;

    // Modo de juego
    private String gameMode;

    // Cantidades de recursos configurables
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
    private int specialBlockCount;
    private int hotTileCount;

    public GameSessionData() {
        resetToDefaults();
    }

    /**
     * Resetea todos los valores a sus valores por defecto
     * Este metodo se llama en el constructor y puede ser llamado externamente
     */
    public void resetToDefaults() {
        // IMPORTANTE: Limpiar COMPLETAMENTE los datos de jugadores
        this.player1Name = "";
        this.player1Flavor = "";
        this.player2Name = "";
        this.player2Flavor = "";

        // CRITICO: Resetear el modo de juego a SINGLE
        this.gameMode = "SINGLE";

        // Valores por defecto del nivel 1
        this.bananaCount = 20;
        this.grapeCount = 16;
        this.cherryCount = 0;
        this.pineappleCount = 0;
        this.cactusCount = 0;

        this.trollCount = 2;
        this.squidCount = 0;
        this.potCount = 0;
        this.narvalCount = 0;

        this.iceBlockCount = 80;
        this.fireBlockCount = 0;
        this.specialBlockCount = 0;
        this.hotTileCount = 0;
    }

    // Getters y setters de jugadores
    public String getPlayer1Name() { return player1Name; }
    public void setPlayer1Name(String name) { this.player1Name = name; }

    public String getPlayer1Flavor() { return player1Flavor; }
    public void setPlayer1Flavor(String flavor) { this.player1Flavor = flavor; }

    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String name) { this.player2Name = name; }

    public String getPlayer2Flavor() { return player2Flavor; }
    public void setPlayer2Flavor(String flavor) { this.player2Flavor = flavor; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String mode) { this.gameMode = mode; }

    public boolean isTwoPlayerMode() {
        return "J1VSJ2".equals(gameMode) || "J1VSIA".equals(gameMode) || "IAVSIA".equals(gameMode);
    }

    // Getters y setters para recursos de frutas
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

    // Getters y setters para enemigos
    public int getTrollCount() { return trollCount; }
    public void setTrollCount(int count) { this.trollCount = count; }

    public int getSquidCount() { return squidCount; }
    public void setSquidCount(int count) { this.squidCount = count; }

    public int getPotCount() { return potCount; }
    public void setPotCount(int count) { this.potCount = count; }

    public int getNarvalCount() { return narvalCount; }
    public void setNarvalCount(int count) { this.narvalCount = count; }


    // Getters y setters para bloques
    public int getIceBlockCount() { return iceBlockCount; }
    public void setIceBlockCount(int count) { this.iceBlockCount = count; }

    public int getFireBlockCount() { return fireBlockCount; }
    public void setFireBlockCount(int count) { this.fireBlockCount = count; }

    public int getSpecialBlockCount() { return specialBlockCount; }
    public void setSpecialBlockCount(int count) { this.specialBlockCount = count; }

    // Getter y setter para baldosas calientes
    public int getHotTileCount() { return hotTileCount; }
    public void setHotTileCount(int count) { this.hotTileCount = count; }
}