package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;
import java.util.List;
import java.util.Random;

/**
 * Implementacion del enemigo Pot (Maceta)
 * Alterna entre caminar tranquilamente y perseguir al jugador agresivamente
 */
public class Pot extends Enemy {
    private PathFinder pathFinder;
    private EnemyManager enemyManager;
    private TargetSelector targetSelector;
    private static final int WALK_SPEED = 2;
    private static final int RUN_SPEED = 4;
    private int currentSpeed;

    private int cellsMoved;
    private static final int CELLS_BEFORE_DIRECTION_CHANGE = 4;

    private PotState currentState;
    private long stateStartTime;
    private static final long WALK_DURATION = 8000;
    private static final long RUN_DURATION = 8000;

    private int pathUpdateCounter;
    private static final int PATH_UPDATE_FREQUENCY = 15;
    private static final int PATH_UPDATE_FREQUENCY_CROWDED = 5;
    private List<Player.Direction> currentPath;

    private int randomMoveCounter;
    private static final int RANDOM_MOVE_FREQUENCY = 60;
    private boolean needsNewDirection;

    private Image[][] walkSprites;
    private int walkFrame;
    private int walkCounter;
    private static final int WALK_ANIMATION_SPEED = 8;
    private static final int WALK_FRAMES = 12;

    private Image[][] realizeSprites;
    private int realizeFrame;
    private int realizeCounter;
    private static final int REALIZE_ANIMATION_SPEED = 5;
    private static final int REALIZE_FRAMES = 24;

    private Image[][] runSprites;
    private int runFrame;
    private int runCounter;
    private static final int RUN_ANIMATION_SPEED = 6;
    private static final int RUN_FRAMES = 12;

    private Image[][] calmSprites;
    private int calmFrame;
    private int calmCounter;
    private static final int CALM_ANIMATION_SPEED = 7;
    private static final int CALM_FRAMES = 10;

    private static final int WALK_SPRITE_WIDTH = 60;
    private static final int WALK_SPRITE_HEIGHT = 70;
    private static final int REALIZE_SPRITE_WIDTH = 98;
    private static final int REALIZE_SPRITE_HEIGHT = 108;
    private static final int RUN_SPRITE_WIDTH = 62;
    private static final int RUN_SPRITE_HEIGHT = 75;
    private static final int CALM_SPRITE_WIDTH = 98;
    private static final int CALM_SPRITE_HEIGHT = 108;

    private Random random;

    private enum PotState {
        WALKING,
        REALIZING,
        RUNNING,
        CALMING
    }

    public Pot(int gridX, int gridY, GridSystem grid,
               CollisionDetector collisionDetector, PathFinder pathFinder) {
        super(gridX, gridY, grid, collisionDetector);
        this.pathFinder = pathFinder;
        this.enemyManager = null;
        this.currentDirection = Player.Direction.DOWN;
        this.currentSpeed = WALK_SPEED;

        this.currentState = PotState.WALKING;
        this.stateStartTime = System.currentTimeMillis();

        this.walkFrame = 0;
        this.walkCounter = 0;
        this.realizeFrame = 0;
        this.realizeCounter = 0;
        this.runFrame = 0;
        this.runCounter = 0;
        this.calmFrame = 0;
        this.calmCounter = 0;

        this.pathUpdateCounter = 0;
        this.currentPath = null;
        this.randomMoveCounter = 0;
        this.random = new Random();
        this.cellsMoved = 0;
        this.needsNewDirection = false;

        loadSprites();

        this.currentDirection = getRandomDirection();
        startNextMove();
    }

    private void loadSprites() {
        walkSprites = new Image[4][WALK_FRAMES];
        for (int frame = 0; frame < WALK_FRAMES; frame++) {
            String path = "Images/Enemies/Pot/Walk/Down/" + (frame + 1) + ".png";
            Image sprite = loadImage(path);
            walkSprites[0][frame] = sprite;
            walkSprites[1][frame] = sprite;
            walkSprites[2][frame] = sprite;
            walkSprites[3][frame] = sprite;
        }

        realizeSprites = new Image[4][REALIZE_FRAMES];
        for (int frame = 0; frame < REALIZE_FRAMES; frame++) {
            String path = "Images/Enemies/Pot/Realize/Vanilla/Down/" + (frame + 1) + ".png";
            Image sprite = loadImage(path);
            realizeSprites[0][frame] = sprite;
            realizeSprites[1][frame] = sprite;
            realizeSprites[2][frame] = sprite;
            realizeSprites[3][frame] = sprite;
        }

        runSprites = new Image[4][RUN_FRAMES];
        for (int frame = 0; frame < RUN_FRAMES; frame++) {
            String path = "Images/Enemies/Pot/Run/Vanilla/Down/" + (frame + 1) + ".png";
            Image sprite = loadImage(path);
            runSprites[0][frame] = sprite;
            runSprites[1][frame] = sprite;
            runSprites[2][frame] = sprite;
            runSprites[3][frame] = sprite;
        }

        calmSprites = new Image[4][CALM_FRAMES];
        for (int frame = 0; frame < CALM_FRAMES; frame++) {
            String path = "Images/Enemies/Pot/Calm/Vanilla/Down/" + (frame + 1) + ".png";
            Image sprite = loadImage(path);
            calmSprites[0][frame] = sprite;
            calmSprites[1][frame] = sprite;
            calmSprites[2][frame] = sprite;
            calmSprites[3][frame] = sprite;
        }

        System.out.println("Sprites de Pot cargados correctamente");
    }

    private Image loadImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                return icon.getImage();
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprite de Pot: " + path);
        }
        return null;
    }

    @Override
    public void setTargetSelector(TargetSelector selector) {
        this.targetSelector = selector;
    }

    public void setEnemyManager(EnemyManager manager) {
        this.enemyManager = manager;
    }

    @Override
    public void update() {
        if (!active) {
            return;
        }

        updateState();
        updateAnimation();

        if (isMoving) {
            moveTowardsTarget(currentSpeed);
        } else if (currentState == PotState.WALKING || currentState == PotState.RUNNING) {
            startNextMove();
        }
    }

    private void updateState() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - stateStartTime;

        switch (currentState) {
            case WALKING:
                if (elapsedTime >= WALK_DURATION) {
                    transitionToState(PotState.REALIZING);
                }
                break;

            case REALIZING:
                if (realizeFrame >= REALIZE_FRAMES - 1) {
                    transitionToState(PotState.RUNNING);
                }
                break;

            case RUNNING:
                if (elapsedTime >= RUN_DURATION) {
                    transitionToState(PotState.CALMING);
                }
                break;

            case CALMING:
                if (calmFrame >= CALM_FRAMES - 1) {
                    transitionToState(PotState.WALKING);
                }
                break;
        }
    }

    private void transitionToState(PotState newState) {
        currentState = newState;
        stateStartTime = System.currentTimeMillis();

        switch (newState) {
            case WALKING:
                currentSpeed = WALK_SPEED;
                walkFrame = 0;
                walkCounter = 0;
                randomMoveCounter = 0;
                cellsMoved = 0;
                needsNewDirection = false;
                currentDirection = getRandomDirection();
                break;

            case REALIZING:
                realizeFrame = 0;
                realizeCounter = 0;
                isMoving = false;
                break;

            case RUNNING:
                currentSpeed = RUN_SPEED;
                runFrame = 0;
                runCounter = 0;
                pathUpdateCounter = 0;
                currentPath = null;
                cellsMoved = 0;
                needsNewDirection = false;
                break;

            case CALMING:
                calmFrame = 0;
                calmCounter = 0;
                isMoving = false;
                break;
        }
    }

    private void updateAnimation() {
        switch (currentState) {
            case WALKING:
                if (isMoving) {
                    walkCounter++;
                    if (walkCounter >= WALK_ANIMATION_SPEED) {
                        walkCounter = 0;
                        walkFrame++;
                        if (walkFrame >= WALK_FRAMES) {
                            walkFrame = 0;
                        }
                    }
                }
                break;

            case REALIZING:
                realizeCounter++;
                if (realizeCounter >= REALIZE_ANIMATION_SPEED) {
                    realizeCounter = 0;
                    if (realizeFrame < REALIZE_FRAMES) {
                        realizeFrame++;
                    }
                }
                break;

            case RUNNING:
                if (isMoving) {
                    runCounter++;
                    if (runCounter >= RUN_ANIMATION_SPEED) {
                        runCounter = 0;
                        runFrame++;
                        if (runFrame >= RUN_FRAMES) {
                            runFrame = 0;
                        }
                    }
                }
                break;

            case CALMING:
                calmCounter++;
                if (calmCounter >= CALM_ANIMATION_SPEED) {
                    calmCounter = 0;
                    if (calmFrame < CALM_FRAMES) {
                        calmFrame++;
                    }
                }
                break;
        }
    }

    private void startNextMove() {
        if (currentState == PotState.RUNNING) {
            handleRunningMovement();
        } else if (currentState == PotState.WALKING) {
            handleWalkingMovement();
        }
    }

    private void handleRunningMovement() {
        if (targetSelector == null || !targetSelector.hasActivePlayer()) {
            return;
        }

        pathUpdateCounter++;

        int updateFrequency = hasEnemyNearby() ?
                PATH_UPDATE_FREQUENCY_CROWDED : PATH_UPDATE_FREQUENCY;

        Point nextPosInPath = null;
        if (currentPath != null && !currentPath.isEmpty()) {
            Player.Direction nextDir = currentPath.get(0);
            nextPosInPath = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, nextDir);

            if (isPositionOccupiedByEnemy(nextPosInPath)) {
                currentPath = null;
                pathUpdateCounter = updateFrequency;
            }
        }

        if (currentPath == null || currentPath.isEmpty() ||
                pathUpdateCounter >= updateFrequency) {
            calculatePathToPlayer();
            pathUpdateCounter = 0;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            Player.Direction nextDirection = currentPath.get(0);
            Point nextPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, nextDirection);

            if (!collisionDetector.isBlocked(nextPos.x, nextPos.y) &&
                    !isPositionOccupiedByEnemy(nextPos)) {
                currentDirection = nextDirection;
                startMovement(nextDirection);
                currentPath.remove(0);
            } else {
                Player.Direction alternativeDir = findAlternativeDirection();
                if (alternativeDir != null) {
                    Point altPos = grid.getNextGridPosition(
                            gridPosition.x, gridPosition.y, alternativeDir);
                    if (!collisionDetector.isBlocked(altPos.x, altPos.y) &&
                            !isPositionOccupiedByEnemy(altPos)) {
                        currentDirection = alternativeDir;
                        startMovement(alternativeDir);
                        currentPath = null;
                        pathUpdateCounter = updateFrequency;
                    }
                }
            }
        }
    }

    private void handleWalkingMovement() {
        if (needsNewDirection) {
            Player.Direction newDirection = getRandomDirection();
            if (newDirection == getOppositeDirection(currentDirection)) {
                newDirection = getRandomDirection();
            }
            currentDirection = newDirection;
            needsNewDirection = false;
        }

        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, currentDirection);

        if (!collisionDetector.isBlocked(nextPos.x, nextPos.y) &&
                !isPositionOccupiedByEnemy(nextPos)) {
            startMovement(currentDirection);
        } else {
            currentDirection = getRandomDirection();
            nextPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, currentDirection);

            if (!collisionDetector.isBlocked(nextPos.x, nextPos.y) &&
                    !isPositionOccupiedByEnemy(nextPos)) {
                startMovement(currentDirection);
            }
        }
    }

    @Override
    protected void moveTowardsTarget(int speed) {
        super.moveTowardsTarget(speed);

        if (!isMoving) {
            cellsMoved++;

            if (currentState == PotState.WALKING && cellsMoved >= CELLS_BEFORE_DIRECTION_CHANGE) {
                cellsMoved = 0;
                needsNewDirection = true;
            }
        }
    }

    private boolean isPositionOccupiedByEnemy(Point position) {
        if (enemyManager == null) {
            return false;
        }

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy != this && enemy.isActive()) {
                Point enemyPos = enemy.getGridPosition();

                if (enemyPos.x == position.x && enemyPos.y == position.y) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasEnemyNearby() {
        if (enemyManager == null) {
            return false;
        }

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy != this && enemy.isActive()) {
                Point enemyPos = enemy.getGridPosition();
                int distance = Math.abs(enemyPos.x - gridPosition.x) +
                        Math.abs(enemyPos.y - gridPosition.y);

                if (distance <= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void calculatePathToPlayer() {
        if (targetSelector == null) {
            return;
        }

        Player targetPlayer = targetSelector.findNearestActivePlayer(
                gridPosition.x, gridPosition.y);

        if (targetPlayer == null) {
            return;
        }

        Point playerGrid = grid.pixelToGrid(
                targetPlayer.getX(), targetPlayer.getY());

        currentPath = pathFinder.findPath(
                gridPosition.x, gridPosition.y,
                playerGrid.x, playerGrid.y);
    }

    private void startMovement(Player.Direction direction) {
        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, direction);
        Point targetPixel = grid.gridToPixel(nextPos.x, nextPos.y);

        targetX = targetPixel.x;
        targetY = targetPixel.y;
        currentDirection = direction;
        isMoving = true;
    }
    private Player.Direction getRandomDirection() {
        Player.Direction[] directions = {
                Player.Direction.UP, Player.Direction.DOWN,
                Player.Direction.LEFT, Player.Direction.RIGHT
        };
        return directions[random.nextInt(directions.length)];
    }

    private Player.Direction getOppositeDirection(Player.Direction direction) {
        switch (direction) {
            case UP:
                return Player.Direction.DOWN;
            case DOWN:
                return Player.Direction.UP;
            case LEFT:
                return Player.Direction.RIGHT;
            case RIGHT:
                return Player.Direction.LEFT;
            default:
                return Player.Direction.DOWN;
        }
    }

    private Player.Direction findAlternativeDirection() {
        Player.Direction[] directions = {
                Player.Direction.UP, Player.Direction.DOWN,
                Player.Direction.LEFT, Player.Direction.RIGHT
        };

        Player.Direction[] perpendicular = getPerpendicularDirections(currentDirection);
        for (Player.Direction dir : perpendicular) {
            Point testPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, dir);
            if (!collisionDetector.isBlocked(testPos.x, testPos.y) &&
                    !isPositionOccupiedByEnemy(testPos)) {
                return dir;
            }
        }

        for (Player.Direction dir : directions) {
            if (dir == getOppositeDirection(currentDirection)) {
                continue;
            }
            Point testPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, dir);
            if (!collisionDetector.isBlocked(testPos.x, testPos.y) &&
                    !isPositionOccupiedByEnemy(testPos)) {
                return dir;
            }
        }

        return null;
    }

    private Player.Direction[] getPerpendicularDirections(Player.Direction direction) {
        switch (direction) {
            case UP:
            case DOWN:
                return new Player.Direction[]{Player.Direction.LEFT, Player.Direction.RIGHT};
            case LEFT:
            case RIGHT:
                return new Player.Direction[]{Player.Direction.UP, Player.Direction.DOWN};
            default:
                return new Player.Direction[]{Player.Direction.UP, Player.Direction.DOWN};
        }
    }

    @Override
    public int getWidth() {
        switch (currentState) {
            case WALKING:
                return WALK_SPRITE_WIDTH;
            case REALIZING:
                return REALIZE_SPRITE_WIDTH;
            case RUNNING:
                return RUN_SPRITE_WIDTH;
            case CALMING:
                return CALM_SPRITE_WIDTH;
            default:
                return WALK_SPRITE_WIDTH;
        }
    }

    @Override
    public int getHeight() {
        switch (currentState) {
            case WALKING:
                return WALK_SPRITE_HEIGHT;
            case REALIZING:
                return REALIZE_SPRITE_HEIGHT;
            case RUNNING:
                return RUN_SPRITE_HEIGHT;
            case CALMING:
                return CALM_SPRITE_HEIGHT;
            default:
                return WALK_SPRITE_HEIGHT;
        }
    }

    @Override
    public Image getCurrentSprite() {
        int dirIndex = getDirectionIndex();

        switch (currentState) {
            case WALKING:
                if (walkSprites != null && walkSprites[dirIndex] != null &&
                        walkFrame < WALK_FRAMES && walkSprites[dirIndex][walkFrame] != null) {
                    return walkSprites[dirIndex][walkFrame];
                }
                break;

            case REALIZING:
                if (realizeSprites != null && realizeSprites[dirIndex] != null &&
                        realizeFrame < REALIZE_FRAMES && realizeSprites[dirIndex][realizeFrame] != null) {
                    return realizeSprites[dirIndex][realizeFrame];
                }
                break;

            case RUNNING:
                if (runSprites != null && runSprites[dirIndex] != null &&
                        runFrame < RUN_FRAMES && runSprites[dirIndex][runFrame] != null) {
                    return runSprites[dirIndex][runFrame];
                }
                break;

            case CALMING:
                if (calmSprites != null && calmSprites[dirIndex] != null &&
                        calmFrame < CALM_FRAMES && calmSprites[dirIndex][calmFrame] != null) {
                    return calmSprites[dirIndex][calmFrame];
                }
                break;
        }

        if (walkSprites != null && walkSprites[0] != null && walkSprites[0][0] != null) {
            return walkSprites[0][0];
        }

        return null;
    }

    private int getDirectionIndex() {
        switch (currentDirection) {
            case DOWN:
                return 0;
            case UP:
                return 1;
            case LEFT:
                return 2;
            case RIGHT:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public EnemyType getType() {
        return EnemyType.POT;
    }

    public PotState getCurrentState() {
        return currentState;
    }
}