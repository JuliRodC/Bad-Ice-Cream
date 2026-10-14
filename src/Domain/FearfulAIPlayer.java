package Domain;

import java.awt.Point;

/**
 * Jugador IA tipo Fearful - Prioriza evitar enemigos
 * Calcula la posicion mas alejada de todos los enemigos y se mueve hacia ella
 */
public class FearfulAIPlayer extends AIPlayer {
    private static final int PATH_UPDATE_FREQUENCY = 10;
    private static final int DANGER_RADIUS = 5;

    public FearfulAIPlayer(int startX, int startY, String spriteBasePath,
                           GridSystem grid, PathFinder pathFinder) {
        super(startX, startY, spriteBasePath, grid, pathFinder);
    }

    @Override
    public void updateAI(FruitManager fruitManager, EnemyManager enemyManager) {
        if (isPerformingAction()) {
            return;
        }

        pathUpdateCounter++;

        boolean needsNewPath = (currentPath == null || currentPath.isEmpty() ||
                pathUpdateCounter >= PATH_UPDATE_FREQUENCY ||
                isInDanger(enemyManager));

        if (needsNewPath) {
            calculateSafePath(enemyManager);
            pathUpdateCounter = 0;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            Player.Direction nextDirection = currentPath.get(0);
            startMoveInDirection(nextDirection, pathFinder.grid);

            Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());
            Point nextPos = pathFinder.grid.getNextGridPosition(
                    currentPos.x, currentPos.y, nextDirection);

            if (hasReachedPosition(nextPos)) {
                currentPath.remove(0);
            }
        } else {
            stopMoving();
        }
    }

    private void calculateSafePath(EnemyManager enemyManager) {
        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());

        targetPosition = findSafestPosition(currentPos, enemyManager);

        if (targetPosition != null) {
            currentPath = pathFinder.findPath(
                    currentPos.x, currentPos.y,
                    targetPosition.x, targetPosition.y);
        } else {
            currentPath = null;
        }
    }

    private Point findSafestPosition(Point currentPos, EnemyManager enemyManager) {
        int gridWidth = pathFinder.grid.getGridWidth();
        int gridHeight = pathFinder.grid.getGridHeight();

        Point safestPosition = null;
        int maxTotalDistance = Integer.MIN_VALUE;

        Point[] candidates = {
                new Point(2, 2),
                new Point(gridWidth - 3, 2),
                new Point(2, gridHeight - 3),
                new Point(gridWidth - 3, gridHeight - 3),
                new Point(gridWidth / 2, 2),
                new Point(gridWidth / 2, gridHeight - 3),
                new Point(2, gridHeight / 2),
                new Point(gridWidth - 3, gridHeight / 2),
                new Point(gridWidth / 2, gridHeight / 2),
                new Point(currentPos.x + 3, currentPos.y),
                new Point(currentPos.x - 3, currentPos.y),
                new Point(currentPos.x, currentPos.y + 3),
                new Point(currentPos.x, currentPos.y - 3)
        };

        for (Point candidate : candidates) {
            if (candidate.x < 2 || candidate.x >= gridWidth - 2 ||
                    candidate.y < 2 || candidate.y >= gridHeight - 2) {
                continue;
            }

            if (pathFinder.collisionDetector.isBlocked(candidate.x, candidate.y)) {
                continue;
            }

            int totalDistance = 0;

            for (Enemy enemy : enemyManager.getEnemies()) {
                if (!enemy.isActive()) {
                    continue;
                }

                Point enemyPos = enemy.getGridPosition();
                int distance = manhattanDistance(candidate.x, candidate.y, enemyPos.x, enemyPos.y);
                totalDistance += distance;
            }

            if (totalDistance > maxTotalDistance) {
                java.util.List<Player.Direction> testPath = pathFinder.findPath(
                        currentPos.x, currentPos.y, candidate.x, candidate.y);

                if (testPath != null) {
                    maxTotalDistance = totalDistance;
                    safestPosition = candidate;
                }
            }
        }

        return safestPosition;
    }

    private boolean isInDanger(EnemyManager enemyManager) {
        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (!enemy.isActive()) {
                continue;
            }

            Point enemyPos = enemy.getGridPosition();
            int distance = manhattanDistance(
                    currentPos.x, currentPos.y,
                    enemyPos.x, enemyPos.y);

            if (distance <= DANGER_RADIUS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public AIType getAIType() {
        return AIType.FEARFUL;
    }
}