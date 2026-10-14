package Domain;

import java.awt.Point;
import java.util.List;

/**
 * Selector de objetivo para enemigos
 * Encuentra el jugador mas cercano y activo para perseguir
 */
public class TargetSelector {
    private PlayerManager playerManager;
    private GridSystem grid;

    public TargetSelector(PlayerManager playerManager, GridSystem grid) {
        this.playerManager = playerManager;
        this.grid = grid;
    }

    /**
     * Encuentra el jugador mas cercano y activo desde una posicion
     * @param fromX posicion X en grid
     * @param fromY posicion Y en grid
     * @return jugador mas cercano o null si no hay jugadores activos
     */
    public Player findNearestActivePlayer(int fromX, int fromY) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        List<Player> players = playerManager.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (playerManager.isPlayerDead(i)) {
                continue;
            }

            if (player.isPerformingDeathAnimation()) {
                continue;
            }

            Point playerGrid = grid.pixelToGrid(player.getX(), player.getY());
            int distance = manhattanDistance(fromX, fromY, playerGrid.x, playerGrid.y);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    /**
     * Verifica si hay al menos un jugador activo
     * @return true si hay jugadores activos
     */
    public boolean hasActivePlayer() {
        List<Player> players = playerManager.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (!playerManager.isPlayerDead(i) && !players.get(i).isPerformingDeathAnimation()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene todos los jugadores activos
     * @return lista de jugadores activos
     */
    public List<Player> getActivePlayers() {
        return playerManager.getPlayers();
    }

    /**
     * Verifica si un jugador especifico esta muerto
     * @param player jugador a verificar
     * @return true si esta muerto
     */
    public boolean isPlayerDead(Player player) {
        List<Player> players = playerManager.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == player) {
                return playerManager.isPlayerDead(i);
            }
        }
        return false;
    }

    /**
     * Calcula distancia Manhattan entre dos puntos
     */
    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Actualiza las referencias
     */
    public void updateReferences(PlayerManager playerManager, GridSystem grid) {
        this.playerManager = playerManager;
        this.grid = grid;
    }
}