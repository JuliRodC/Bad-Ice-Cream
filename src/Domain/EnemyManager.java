package Domain;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Gestiona todos los enemigos del juego
 * Maneja actualizacion, colisiones y creacion de enemigos
 */
public class EnemyManager {
    private List<Enemy> enemies;
    private GridSystem grid;
    private CollisionDetector collisionDetector;
    private PathFinder pathFinder;
    private GameMap gameMap;
    private TargetSelector targetSelector;

    public EnemyManager(GridSystem grid, CollisionDetector collisionDetector) {
        this.grid = grid;
        this.collisionDetector = collisionDetector;
        this.enemies = new ArrayList<>();
    }

    public void setPathFinder(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void setTargetSelector(TargetSelector selector) {
        this.targetSelector = selector;
    }

    /**
     * Crea un enemigo troll en una posicion de grid
     */
    public void createTroll(int gridX, int gridY) {
        Enemy troll = new TrollEnemy(gridX, gridY, grid, collisionDetector);
        enemies.add(troll);
        System.out.println("Troll creado en posicion: (" + gridX + ", " + gridY + ")");
    }

    /**
     * Crea un enemigo squid en una posicion de grid
     */
    public void createSquid(int gridX, int gridY) {
        if (pathFinder == null) {
            System.out.println("Error: PathFinder no esta configurado para Squid");
            return;
        }

        SquidEnemy squid = new SquidEnemy(gridX, gridY, grid, collisionDetector, pathFinder);
        squid.setTargetSelector(targetSelector);
        enemies.add(squid);
        System.out.println("Squid creado en posicion: (" + gridX + ", " + gridY + ")");
    }

    /**
     * Crea un enemigo pot en una posicion de grid
     */
    public void createPot(int gridX, int gridY) {
        if (pathFinder == null) {
            System.out.println("Error: PathFinder no esta configurado para Pot");
            return;
        }

        Pot pot = new Pot(gridX, gridY, grid, collisionDetector, pathFinder);
        pot.setTargetSelector(targetSelector);
        pot.setEnemyManager(this);
        enemies.add(pot);
        System.out.println("Pot creado en posicion: (" + gridX + ", " + gridY + ")");
    }

    /**
     * Crea pots en posiciones estrategicas del mapa
     */
    public void createPotsInMap(int potCount) {
        if (potCount <= 0) {
            System.out.println("No se crearan pots (cantidad: 0)");
            return;
        }

        int[][] potPositions = {
                {3, 3},
                {14, 3},
                {3, 14},
                {14, 14},
                {8, 8},
                {9, 9},
                {7, 9},
                {10, 8}
        };

        int createdPots = 0;
        int posIndex = 0;
        int maxAttempts = potCount * 10;

        while (createdPots < potCount && posIndex < maxAttempts) {
            int arrayIndex = posIndex % potPositions.length;
            int[] position = potPositions[arrayIndex];

            int offsetX = (posIndex / potPositions.length) % 3 - 1;
            int offsetY = (posIndex / (potPositions.length * 3)) % 3 - 1;

            int finalX = position[0] + offsetX;
            int finalY = position[1] + offsetY;

            if (finalX >= 2 && finalX <= 15 && finalY >= 2 && finalY <= 15 &&
                    !collisionDetector.isBlocked(finalX, finalY) &&
                    !isGridPositionOccupiedByEnemy(finalX, finalY)) {
                createPot(finalX, finalY);
                createdPots++;
            }

            posIndex++;
        }

        System.out.println("Pots creados: " + createdPots);
    }

    /**
     * Crea un enemigo narval en una posicion de grid
     */
    public void createNarval(int gridX, int gridY) {
        if (gameMap == null) {
            System.out.println("Error: GameMap no esta configurado para Narval");
            return;
        }

        Narval narval = new Narval(gridX, gridY, grid, collisionDetector, gameMap);
        narval.setTargetSelector(targetSelector);
        enemies.add(narval);
        System.out.println("Narval creado en posicion: (" + gridX + ", " + gridY + ")");
    }

    /**
     * Crea narvales en posiciones estrategicas del mapa
     */
    public void createNarvalsInMap(int narvalCount) {
        if (narvalCount <= 0) {
            System.out.println("No se crearan narvales (cantidad: 0)");
            return;
        }

        int[][] narvalPositions = {
                {5, 8},
                {12, 8},
                {8, 5},
                {8, 12},
                {6, 6},
                {11, 11},
                {6, 11},
                {11, 6}
        };

        int createdNarvals = 0;
        int posIndex = 0;
        int maxAttempts = narvalCount * 10;

        while (createdNarvals < narvalCount && posIndex < maxAttempts) {
            int arrayIndex = posIndex % narvalPositions.length;
            int[] position = narvalPositions[arrayIndex];

            int offsetX = (posIndex / narvalPositions.length) % 3 - 1;
            int offsetY = (posIndex / (narvalPositions.length * 3)) % 3 - 1;

            int finalX = position[0] + offsetX;
            int finalY = position[1] + offsetY;

            if (finalX >= 2 && finalX <= 15 && finalY >= 2 && finalY <= 15 &&
                    !collisionDetector.isBlocked(finalX, finalY) &&
                    !isGridPositionOccupiedByEnemy(finalX, finalY)) {
                createNarval(finalX, finalY);
                createdNarvals++;
            }

            posIndex++;
        }

        System.out.println("Narvales creados: " + createdNarvals);
    }

    public int getActiveNarvalCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.getType() == Enemy.EnemyType.NARVAL) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifica si una posicion de grid esta ocupada por otro enemigo
     */
    private boolean isGridPositionOccupiedByEnemy(int gridX, int gridY) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                Point enemyPos = enemy.getGridPosition();
                if (enemyPos.x == gridX && enemyPos.y == gridY) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getActivePotCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.getType() == Enemy.EnemyType.POT) {
                count++;
            }
        }
        return count;
    }

    /**
     * Crea trolls en las esquinas del mapa segun la cantidad especificada
     */
    public void createTrollsInCorners(int trollCount) {
        if (trollCount <= 0) {
            System.out.println("No se crearan trolls (cantidad: 0)");
            return;
        }

        int[][] cornerPositions = {
                {2, 2},
                {15, 2},
                {2, 15},
                {15, 15}
        };

        int createdTrolls = 0;
        int cornerIndex = 0;

        while (createdTrolls < trollCount && cornerIndex < cornerPositions.length * 10) {
            int posIndex = cornerIndex % cornerPositions.length;
            int[] position = cornerPositions[posIndex];

            int offsetX = (cornerIndex / cornerPositions.length) % 3;
            int offsetY = (cornerIndex / (cornerPositions.length * 3)) % 3;

            int finalX = position[0] + offsetX;
            int finalY = position[1] + offsetY;

            if (finalX >= 2 && finalX <= 15 && finalY >= 2 && finalY <= 15) {
                createTroll(finalX, finalY);
                createdTrolls++;
            }

            cornerIndex++;
        }

        System.out.println("Trolls creados en esquinas: " + createdTrolls);
    }

    /**
     * Crea squids en posiciones estrategicas del mapa
     */
    public void createSquidsInMap(int squidCount) {
        if (squidCount <= 0) {
            System.out.println("No se crearan squids (cantidad: 0)");
            return;
        }

        int[][] squidPositions = {
                {8, 3},
                {9, 3},
                {8, 14},
                {9, 14}
        };

        int createdSquids = 0;
        int posIndex = 0;

        while (createdSquids < squidCount && posIndex < squidPositions.length * 3) {
            int arrayIndex = posIndex % squidPositions.length;
            int[] position = squidPositions[arrayIndex];

            int offsetX = (posIndex / squidPositions.length);

            int finalX = position[0] + offsetX;
            int finalY = position[1];

            if (finalX >= 2 && finalX <= 15 && finalY >= 2 && finalY <= 15) {
                createSquid(finalX, finalY);
                createdSquids++;
            }

            posIndex++;
        }

        System.out.println("Squids creados: " + createdSquids);
    }

    /**
     * Actualiza todos los enemigos
     */
    public void update() {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update();

                if (enemy.getType() == Enemy.EnemyType.SQUID) {
                    processSquidIceDestruction((SquidEnemy) enemy);
                }

                if (enemy.getType() == Enemy.EnemyType.NARVAL) {
                    processNarvalIceDestruction((Narval) enemy);
                }
            }
        }
    }

    private void processSquidIceDestruction(SquidEnemy squid) {
        if (squid.shouldDestroyIceNow() && gameMap != null) {
            java.awt.Point squidGrid = squid.getGridPosition();
            Player.Direction direction = squid.getCurrentDirection();
            gameMap.destroyIceInDirection(
                    squidGrid.x * grid.getCellSize(),
                    squidGrid.y * grid.getCellSize(),
                    direction
            );
            squid.resetIceDestruction();
        }
    }

    /**
     * Verifica colisiones con el jugador
     */
    public boolean checkCollisionWithPlayer(Player player) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.collidesWith(
                    player.getX(), player.getY(),
                    player.getWidth(), player.getHeight())) {
                System.out.println("Colision detectada con enemigo tipo: " + enemy.getType());
                return true;
            }
        }
        return false;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void clear() {
        enemies.clear();
    }

    private void processNarvalIceDestruction(Narval narval) {
        if (narval.shouldDestroyIceNow() && gameMap != null) {
            Point narvalGrid = narval.getGridPosition();
            Player.Direction direction = narval.getCurrentDirection();

            Point nextGrid = grid.getNextGridPosition(
                    narvalGrid.x, narvalGrid.y, direction);

            Point pixelPos = grid.gridToPixel(nextGrid.x, nextGrid.y);

            boolean destroyed = gameMap.destroyIceInDirection(
                    pixelPos.x, pixelPos.y, direction);

            if (destroyed) {
                System.out.println("Narval destruyo hielo en direccion " + direction +
                        " desde (" + narvalGrid.x + "," + narvalGrid.y +
                        ") hacia (" + nextGrid.x + "," + nextGrid.y + ")");
            }

            narval.resetIceDestruction();
        }
    }

    public int getActiveEnemyCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                count++;
            }
        }
        return count;
    }

    public Map<Enemy.EnemyType, Integer> getEnemyCountsByType() {
        Map<Enemy.EnemyType, Integer> counts = new HashMap<>();

        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                Enemy.EnemyType type = enemy.getType();
                counts.put(type, counts.getOrDefault(type, 0) + 1);
            }
        }

        return counts;
    }

    public int getActiveTrollCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.getType() == Enemy.EnemyType.TROLL) {
                count++;
            }
        }
        return count;
    }

    public int getActiveSquidCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.getType() == Enemy.EnemyType.SQUID) {
                count++;
            }
        }
        return count;
    }
}