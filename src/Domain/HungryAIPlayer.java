package Domain;

import java.awt.Point;

/**
 * Jugador IA tipo Hungry - Prioriza recolectar frutas agresivamente
 * Ignora enemigos y solo se enfoca en conseguir puntos
 * Ahora puede romper hielo si queda encerrado
 */
public class HungryAIPlayer extends AIPlayer {
    private static final int PATH_UPDATE_FREQUENCY = 15;

    private int stuckCounter;
    private static final int STUCK_THRESHOLD = 30;
    private Point lastPosition;

    public HungryAIPlayer(int startX, int startY, String spriteBasePath,
                          GridSystem grid, PathFinder pathFinder) {
        super(startX, startY, spriteBasePath, grid, pathFinder);
        this.stuckCounter = 0;
        this.lastPosition = null;
    }

    @Override
    public void updateAI(FruitManager fruitManager, EnemyManager enemyManager) {
        if (isPerformingAction()) {
            return;
        }

        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());

        checkIfStuck(currentPos);

        if (isEnclosed(currentPos) && canPerformAction()) {
            Player.Direction breakDirection = findBestIceToBreak(currentPos);
            if (breakDirection != null) {
                facingDirection = breakDirection;
                startBreakIceAnimation();
                performAction();
                stuckCounter = 0;
                return;
            }
        }

        pathUpdateCounter++;

        boolean needsNewPath = (currentPath == null || currentPath.isEmpty() ||
                pathUpdateCounter >= PATH_UPDATE_FREQUENCY ||
                targetFruitCollected(fruitManager));

        if (needsNewPath) {
            calculateNewPath(fruitManager);
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

    private void checkIfStuck(Point currentPos) {
        if (lastPosition != null && lastPosition.equals(currentPos)) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
        }
        lastPosition = new Point(currentPos.x, currentPos.y);
    }

    private boolean isEnclosed(Point position) {
        if (stuckCounter < STUCK_THRESHOLD) {
            return false;
        }

        int blockedSides = 0;
        Player.Direction[] directions = {
                Player.Direction.UP, Player.Direction.DOWN,
                Player.Direction.LEFT, Player.Direction.RIGHT
        };

        for (Player.Direction dir : directions) {
            Point nextPos = pathFinder.grid.getNextGridPosition(
                    position.x, position.y, dir);

            if (pathFinder.collisionDetector.isBlocked(nextPos.x, nextPos.y)) {
                if (hasIceBlock(nextPos.x, nextPos.y)) {
                    blockedSides++;
                }
            }
        }

        return blockedSides >= 3;
    }

    private Player.Direction findBestIceToBreak(Point position) {
        Player.Direction[] directions = {
                Player.Direction.UP, Player.Direction.DOWN,
                Player.Direction.LEFT, Player.Direction.RIGHT
        };

        Player.Direction bestDirection = null;
        int maxPathLength = -1;

        for (Player.Direction dir : directions) {
            Point nextPos = pathFinder.grid.getNextGridPosition(
                    position.x, position.y, dir);

            if (hasIceBlock(nextPos.x, nextPos.y)) {
                Point beyondIce = pathFinder.grid.getNextGridPosition(
                        nextPos.x, nextPos.y, dir);

                if (pathFinder.grid.isInBounds(beyondIce.x, beyondIce.y) &&
                        !pathFinder.collisionDetector.isBlocked(beyondIce.x, beyondIce.y)) {

                    int pathLength = countClearPath(beyondIce, dir);
                    if (pathLength > maxPathLength) {
                        maxPathLength = pathLength;
                        bestDirection = dir;
                    }
                }
            }
        }

        return bestDirection;
    }

    private int countClearPath(Point startPos, Player.Direction direction) {
        int count = 0;
        Point current = new Point(startPos.x, startPos.y);

        for (int i = 0; i < 5; i++) {
            if (!pathFinder.grid.isInBounds(current.x, current.y)) {
                break;
            }

            if (pathFinder.collisionDetector.isBlocked(current.x, current.y)) {
                break;
            }

            count++;
            current = pathFinder.grid.getNextGridPosition(
                    current.x, current.y, direction);
        }

        return count;
    }

    private boolean hasIceBlock(int gridX, int gridY) {
        if (!pathFinder.grid.isInBounds(gridX, gridY)) {
            return false;
        }

        return pathFinder.collisionDetector.isBlocked(gridX, gridY) &&
                !isWall(gridX, gridY);
    }

    private boolean isWall(int gridX, int gridY) {
        int gridWidth = pathFinder.grid.getGridWidth();
        int gridHeight = pathFinder.grid.getGridHeight();

        return gridX <= 1 || gridX >= gridWidth - 2 ||
                gridY <= 1 || gridY >= gridHeight - 2;
    }

    private void calculateNewPath(FruitManager fruitManager) {
        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());

        targetPosition = pathFinder.findNearestFruit(
                currentPos.x, currentPos.y, fruitManager);

        if (targetPosition != null) {
            currentPath = pathFinder.findPath(
                    currentPos.x, currentPos.y,
                    targetPosition.x, targetPosition.y);
        } else {
            currentPath = null;
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
        return AIType.HUNGRY;
    }

    @Override
    public void resetAIState() {
        super.resetAIState();
        stuckCounter = 0;
        lastPosition = null;
    }
}