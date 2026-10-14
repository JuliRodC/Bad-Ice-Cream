package Domain;

import java.awt.Point;
import java.util.List;

/**
 * Jugador IA tipo Expert - Combina busqueda de frutas con evasion de enemigos
 * Busca frutas activamente pero cambia a modo evasivo cuando hay enemigos cercanos
 */
public class ExpertAIPlayer extends AIPlayer {
    private static final int PATH_UPDATE_FREQUENCY = 10;
    private static final int DANGER_RADIUS = 4;
    private static final int CRITICAL_DANGER_RADIUS = 2;

    private AIMode currentMode;

    private enum AIMode {
        HUNTING,
        EVADING
    }

    public ExpertAIPlayer(int startX, int startY, String spriteBasePath,
                          GridSystem grid, PathFinder pathFinder) {
        super(startX, startY, spriteBasePath, grid, pathFinder);
        this.currentMode = AIMode.HUNTING;
    }

    @Override
    public void updateAI(FruitManager fruitManager, EnemyManager enemyManager) {
        if (isPerformingAction()) {
            return;
        }

        pathUpdateCounter++;

        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());
        DangerLevel danger = evaluateDanger(currentPos, enemyManager);

        AIMode previousMode = currentMode;
        currentMode = (danger == DangerLevel.NONE || danger == DangerLevel.LOW)
                ? AIMode.HUNTING
                : AIMode.EVADING;

        boolean needsNewPath = (currentPath == null || currentPath.isEmpty() ||
                pathUpdateCounter >= PATH_UPDATE_FREQUENCY ||
                previousMode != currentMode ||
                (currentMode == AIMode.HUNTING && targetFruitCollected(fruitManager)) ||
                (currentMode == AIMode.EVADING && danger == DangerLevel.CRITICAL));

        if (needsNewPath) {
            if (currentMode == AIMode.HUNTING) {
                calculateHuntingPath(fruitManager, enemyManager);
            } else {
                calculateEvadingPath(enemyManager);
            }
            pathUpdateCounter = 0;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            Player.Direction nextDirection = currentPath.get(0);
            startMoveInDirection(nextDirection, pathFinder.grid);

            Point nextPos = pathFinder.grid.getNextGridPosition(
                    currentPos.x, currentPos.y, nextDirection);

            if (hasReachedPosition(nextPos)) {
                currentPath.remove(0);
            }
        } else {
            stopMoving();
        }
    }

    private void calculateHuntingPath(FruitManager fruitManager, EnemyManager enemyManager) {
        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());
        Point bestFruit = findSafestFruit(currentPos, fruitManager, enemyManager);

        if (bestFruit != null) {
            targetPosition = bestFruit;
            currentPath = pathFinder.findPath(
                    currentPos.x, currentPos.y,
                    bestFruit.x, bestFruit.y);
        } else {
            currentMode = AIMode.EVADING;
            calculateEvadingPath(enemyManager);
        }
    }

    private void calculateEvadingPath(EnemyManager enemyManager) {
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

    private Point findSafestFruit(Point currentPos, FruitManager fruitManager,
                                  EnemyManager enemyManager) {
        Point bestFruit = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Fruit fruit : fruitManager.getFruits()) {
            if (fruit.isCollected()) {
                continue;
            }

            Point fruitPos = fruit.getGridPosition();

            List<Player.Direction> testPath = pathFinder.findPath(
                    currentPos.x, currentPos.y, fruitPos.x, fruitPos.y);

            if (testPath == null) {
                continue;
            }

            int distanceToFruit = testPath.size();

            int minEnemyDistance = Integer.MAX_VALUE;
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (!enemy.isActive()) {
                    continue;
                }

                Point enemyPos = enemy.getGridPosition();
                int distance = manhattanDistance(
                        fruitPos.x, fruitPos.y,
                        enemyPos.x, enemyPos.y);

                minEnemyDistance = Math.min(minEnemyDistance, distance);
            }

            double score = (minEnemyDistance * 2.0) - (distanceToFruit * 0.5);

            if (minEnemyDistance < DANGER_RADIUS) {
                score -= 50;
            }

            if (score > bestScore) {
                bestScore = score;
                bestFruit = fruitPos;
            }
        }

        return bestFruit;
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
                new Point(gridWidth / 4, gridHeight / 4),
                new Point(3 * gridWidth / 4, gridHeight / 4),
                new Point(gridWidth / 4, 3 * gridHeight / 4),
                new Point(3 * gridWidth / 4, 3 * gridHeight / 4)
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
                int distance = manhattanDistance(
                        candidate.x, candidate.y,
                        enemyPos.x, enemyPos.y);
                totalDistance += distance;
            }

            if (totalDistance > maxTotalDistance) {
                List<Player.Direction> testPath = pathFinder.findPath(
                        currentPos.x, currentPos.y, candidate.x, candidate.y);

                if (testPath != null) {
                    maxTotalDistance = totalDistance;
                    safestPosition = candidate;
                }
            }
        }

        return safestPosition;
    }

    private DangerLevel evaluateDanger(Point position, EnemyManager enemyManager) {
        int closestEnemyDistance = Integer.MAX_VALUE;

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (!enemy.isActive()) {
                continue;
            }

            Point enemyPos = enemy.getGridPosition();
            int distance = manhattanDistance(
                    position.x, position.y,
                    enemyPos.x, enemyPos.y);

            closestEnemyDistance = Math.min(closestEnemyDistance, distance);
        }

        if (closestEnemyDistance <= CRITICAL_DANGER_RADIUS) {
            return DangerLevel.CRITICAL;
        } else if (closestEnemyDistance <= DANGER_RADIUS) {
            return DangerLevel.HIGH;
        } else if (closestEnemyDistance <= DANGER_RADIUS + 2) {
            return DangerLevel.LOW;
        } else {
            return DangerLevel.NONE;
        }
    }

    private boolean targetFruitCollected(FruitManager fruitManager) {
        if (targetPosition == null) {
            return true;
        }

        for (Fruit fruit : fruitManager.getFruits()) {
            Point fruitPos = fruit.getGridPosition();
            if (fruitPos.equals(targetPosition) && !fruit.isCollected()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public AIType getAIType() {
        return AIType.EXPERT;
    }

    @Override
    public void resetAIState() {
        super.resetAIState();
        currentMode = AIMode.HUNTING;
    }

    private enum DangerLevel {
        NONE,
        LOW,
        HIGH,
        CRITICAL
    }
}