package Domain;

import java.awt.Point;

/**
 * Gestor de colisiones del juego
 * Maneja todas las colisiones entre jugadores, frutas, enemigos, fogatas, cactus y acciones de hielo
 */
public class CollisionManager {
    private FruitManager fruitManager;
    private EnemyManager enemyManager;
    private GameMap gameMap;

    public CollisionManager(FruitManager fruitManager, EnemyManager enemyManager, GameMap gameMap) {
        this.fruitManager = fruitManager;
        this.enemyManager = enemyManager;
        this.gameMap = gameMap;
    }

    /**
     * Verifica todas las colisiones para un jugador
     * @param player jugador a verificar
     * @return true si el jugador colisiono con un enemigo, fogata encendida o cactus peligroso
     */
    public boolean checkPlayerCollisions(Player player) {
        fruitManager.checkCollisions(player);

        boolean hitEnemy = enemyManager.checkCollisionWithPlayer(player);

        boolean hitFire = checkCampfireCollision(player);

        boolean hitDangerousCactus = fruitManager.checkDangerousCactusCollision(player);

        return hitEnemy || hitFire || hitDangerousCactus;
    }

    /**
     * Verifica si el jugador colisiona con una fogata encendida
     * @param player jugador a verificar
     * @return true si colisiona con fogata encendida
     */
    private boolean checkCampfireCollision(Player player) {
        Point playerGrid = gameMap.getGrid().pixelToGrid(player.getX(), player.getY());

        if (gameMap.hasLitCampfireAt(playerGrid.x, playerGrid.y)) {
            System.out.println("Jugador murio por fogata encendida en: (" + playerGrid.x + ", " + playerGrid.y + ")");
            return true;
        }

        return false;
    }

    /**
     * Procesa las acciones de hielo de un jugador
     * @param player jugador que realiza la accion
     */
    public void processIceActions(Player player) {
        if (player.shouldCreateIce()) {
            Player.Direction dir = player.getDirection();
            gameMap.createIceInDirection(player.getX(), player.getY(), dir);
            player.resetIceCreation();
        }

        if (player.shouldDestroyIce()) {
            Player.Direction dir = player.getDirection();
            gameMap.destroyIceInDirection(player.getX(), player.getY(), dir);
            player.resetIceDestruction();
        }
    }

    /**
     * Verifica colisiones para todos los jugadores de un PlayerManager
     * @param playerManager gestor de jugadores
     * @return true si algun jugador colisiono con enemigos, fogatas o cactus peligrosos
     */
    public boolean checkAllPlayerCollisions(PlayerManager playerManager) {
        boolean anyCollision = false;

        for (int i = 0; i < playerManager.getPlayerCount(); i++) {
            Player player = playerManager.getPlayer(i);

            if (player != null && !playerManager.isPlayerDead(i)) {
                if (checkPlayerCollisions(player)) {
                    anyCollision = true;
                }
            }
        }

        return anyCollision;
    }

    /**
     * Procesa acciones de hielo para todos los jugadores
     * @param playerManager gestor de jugadores
     */
    public void processAllIceActions(PlayerManager playerManager) {
        for (int i = 0; i < playerManager.getPlayerCount(); i++) {
            Player player = playerManager.getPlayer(i);

            if (player != null && !playerManager.isPlayerDead(i)) {
                processIceActions(player);
            }
        }
    }

    /**
     * Actualiza las referencias de los managers
     * Util cuando se reinicia el nivel
     */
    public void updateManagers(FruitManager fruitManager, EnemyManager enemyManager, GameMap gameMap) {
        this.fruitManager = fruitManager;
        this.enemyManager = enemyManager;
        this.gameMap = gameMap;
    }
}