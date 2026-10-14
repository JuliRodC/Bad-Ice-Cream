package Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de jugadores del juego
 * Maneja multiples jugadores y sus interacciones con el nivel
 */
public class PlayerManager {
    private List<Player> players;
    private List<PlayerInfo> playerInfos;
    private boolean isTwoPlayerMode;
    private FruitManager fruitManager;
    private List<java.awt.Point> lastPlayerPositions;

    public PlayerManager() {
        this.players = new ArrayList<>();
        this.playerInfos = new ArrayList<>();
        this.isTwoPlayerMode = false;
        this.lastPlayerPositions = new ArrayList<>();
    }

    /**
     * Establece el FruitManager para notificar movimientos
     */
    public void setFruitManager(FruitManager fruitManager) {
        this.fruitManager = fruitManager;
    }

    /**
     * Registra un jugador en el gestor
     */
    public void registerPlayer(Player player, String name, String flavor, boolean isAI) {
        players.add(player);
        playerInfos.add(new PlayerInfo(name, flavor, isAI));
        lastPlayerPositions.add(new java.awt.Point(player.getX(), player.getY()));

        if (players.size() > 1) {
            isTwoPlayerMode = true;
        }

        System.out.println("Jugador registrado: " + name + " (" + flavor + ")" +
                (isAI ? " [IA]" : ""));
    }

    /**
     * Actualiza todos los jugadores activos
     */
    public void updateAllPlayers(FruitManager fruitManager, EnemyManager enemyManager) {
        boolean anyPlayerMoved = false;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerInfo info = playerInfos.get(i);

            if (!info.isDead()) {
                java.awt.Point lastPos = lastPlayerPositions.get(i);
                int lastX = lastPos.x;
                int lastY = lastPos.y;

                // PRIMERO actualizar IA si corresponde
                if (info.isAI()) {
                    AIPlayer aiPlayer = (AIPlayer) player;
                    System.out.println("DEBUG: Actualizando IA " + (i+1) + " - " + info.getName());
                    aiPlayer.updateAI(fruitManager, enemyManager);
                }

                // LUEGO actualizar el jugador (IA o humano)
                player.update();

                int currentX = player.getX();
                int currentY = player.getY();

                if (currentX != lastX || currentY != lastY) {
                    anyPlayerMoved = true;
                    lastPlayerPositions.set(i, new java.awt.Point(currentX, currentY));
                }
            }
        }

        if (anyPlayerMoved && this.fruitManager != null) {
            this.fruitManager.notifyPlayerMoved();
        }
    }


    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        }
        return null;
    }

    public PlayerInfo getPlayerInfo(int index) {
        if (index >= 0 && index < playerInfos.size()) {
            return playerInfos.get(index);
        }
        return null;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isTwoPlayerMode() {
        return isTwoPlayerMode;
    }

    public void markPlayerDead(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerInfos.size()) {
            playerInfos.get(playerIndex).setDead(true);
            System.out.println("Jugador " + (playerIndex + 1) + " ha muerto");
        }
    }

    public boolean areAllPlayersDead() {
        for (PlayerInfo info : playerInfos) {
            if (!info.isDead()) {
                return false;
            }
        }
        return true;
    }

    public boolean isPlayerDead(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerInfos.size()) {
            return playerInfos.get(playerIndex).isDead();
        }
        return false;
    }

    public boolean hasAIPlayer() {
        for (PlayerInfo info : playerInfos) {
            if (info.isAI()) {
                return true;
            }
        }
        return false;
    }

    public void resetAllPlayers() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerInfo info = playerInfos.get(i);

            info.setDead(false);

            if (player instanceof AIPlayer) {
                AIPlayer aiPlayer = (AIPlayer) player;
                aiPlayer.resetAIState();
            }
        }

        System.out.println("Todos los jugadores reiniciados");
    }

    public void clear() {
        players.clear();
        playerInfos.clear();
        lastPlayerPositions.clear();
        isTwoPlayerMode = false;
    }

    public static class PlayerInfo {
        private final String name;
        private final String flavor;
        private final boolean isAI;
        private boolean isDead;

        public PlayerInfo(String name, String flavor, boolean isAI) {
            this.name = name;
            this.flavor = flavor;
            this.isAI = isAI;
            this.isDead = false;
        }

        public String getName() { return name; }
        public String getFlavor() { return flavor; }
        public boolean isAI() { return isAI; }
        public boolean isDead() { return isDead; }
        public void setDead(boolean dead) { this.isDead = dead; }
    }
}