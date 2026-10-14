package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Implementacion del enemigo Narval
 * Se mueve lentamente sin perseguir directamente
 * Cuando detecta alineacion con el jugador:
 * 1. PREPARING: Preparacion (frames 1-8)
 * 2. CHARGING: Embestida en loop (frames 9-12) hasta chocar con pared
 * 3. STUCK: Atascado en pared (frames 13-20) por 3 segundos
 * Destruye TODOS los bloques de hielo durante la embestida
 */
public class Narval extends Enemy {
    private GameMap gameMap;
    private TargetSelector targetSelector;
    private static final int DETECTION_RANGE = 14;
    private static final int WALK_SPEED = 1;
    private static final int CHARGE_SPEED = 6;
    private int currentSpeed;

    private NarvalState currentState;
    private long stateStartTime;
    private static final long STUCK_IMPACT_DURATION = 3000;

    private Image[][] walkSprites;
    private int walkFrame;
    private int walkCounter;
    private static final int WALK_ANIMATION_SPEED = 10;
    private static final int WALK_FRAMES = 7;

    private Image[][] preparingSprites;
    private int preparingFrame;
    private int preparingCounter;
    private static final int PREPARING_ANIMATION_SPEED = 6;
    private static final int PREPARING_FRAMES = 8;

    private Image[][] chargingSprites;
    private int chargingFrame;
    private int chargingCounter;
    private static final int CHARGING_ANIMATION_SPEED = 3;
    private static final int CHARGING_FRAMES = 4;

    private Image[][] impactSprites;
    private int impactFrame;
    private int impactCounter;
    private static final int IMPACT_ANIMATION_SPEED = 4;
    private static final int IMPACT_FRAMES = 7;

    private Image[][] stuckImpactSprites;

    private Image[][] stuckExitSprites;
    private int stuckExitFrame;
    private int stuckExitCounter;
    private static final int STUCK_EXIT_ANIMATION_SPEED = 5;
    private static final int STUCK_EXIT_FRAMES = 4;

    private Image[][] breakIceSprites;
    private int breakIceFrame;
    private int breakIceCounter;
    private static final int BREAK_ICE_ANIMATION_SPEED = 5;
    private static final int BREAK_ICE_FRAMES = 8;
    private boolean shouldDestroyIce;

    private static final int WALK_SPRITE_WIDTH = 55;
    private static final int WALK_SPRITE_HEIGHT = 65;
    private static final int CHARGE_DOWN_SPRITE_WIDTH = 55;
    private static final int CHARGE_DOWN_SPRITE_HEIGHT = 65;
    private static final int CHARGE_SIDE_SPRITE_WIDTH = 85;
    private static final int CHARGE_SIDE_SPRITE_HEIGHT = 75;
    private static final int CHARGE_UP_SPRITE_WIDTH = 55;
    private static final int CHARGE_UP_SPRITE_HEIGHT = 65;
    private static final int BREAK_ICE_SPRITE_WIDTH = 70;
    private static final int BREAK_ICE_SPRITE_HEIGHT = 80;

    private int randomMoveCounter;
    private static final int RANDOM_MOVE_FREQUENCY = 80;
    private Player.Direction chargeDirection;

    private enum NarvalState {
        WALKING,
        PREPARING,
        CHARGING,
        IMPACTING,
        STUCK_IMPACT,
        STUCK_EXIT,
        BREAKING_ICE
    }

    public Narval(int gridX, int gridY, GridSystem grid,
                  CollisionDetector collisionDetector, GameMap gameMap) {
        super(gridX, gridY, grid, collisionDetector);
        this.gameMap = gameMap;
        this.currentDirection = Player.Direction.DOWN;
        this.currentSpeed = WALK_SPEED;
        this.currentState = NarvalState.WALKING;
        this.stateStartTime = System.currentTimeMillis();

        this.walkFrame = 0;
        this.walkCounter = 0;
        this.preparingFrame = 0;
        this.preparingCounter = 0;
        this.chargingFrame = 0;
        this.chargingCounter = 0;
        this.impactFrame = 0;
        this.impactCounter = 0;
        this.stuckExitFrame = 0;
        this.stuckExitCounter = 0;
        this.breakIceFrame = 0;
        this.breakIceCounter = 0;
        this.shouldDestroyIce = false;
        this.randomMoveCounter = 0;

        loadSprites();
    }

    private void loadSprites() {
        String[] directions = {"Down", "Up", "Left", "Right"};

        walkSprites = new Image[4][WALK_FRAMES];
        for (int frame = 0; frame < WALK_FRAMES; frame++) {
            String path = "Images/Enemies/Narval/Walk/Down/" + (frame + 1) + ".png";
            Image sprite = loadImage(path);
            for (int dir = 0; dir < 4; dir++) {
                walkSprites[dir][frame] = sprite;
            }
        }

        preparingSprites = new Image[4][PREPARING_FRAMES];
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < PREPARING_FRAMES; frame++) {
                String path = "Images/Enemies/Narval/Atrack/" +
                        directions[dir] + "/" + (frame + 1) + ".png";
                preparingSprites[dir][frame] = loadImage(path);
            }
        }

        chargingSprites = new Image[4][CHARGING_FRAMES];
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < CHARGING_FRAMES; frame++) {
                String path = "Images/Enemies/Narval/Atrack/" +
                        directions[dir] + "/" + (frame + 9) + ".png";
                chargingSprites[dir][frame] = loadImage(path);
            }
        }

        impactSprites = new Image[4][IMPACT_FRAMES];
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < IMPACT_FRAMES; frame++) {
                String path = "Images/Enemies/Narval/Atrack/" +
                        directions[dir] + "/" + (frame + 10) + ".png";
                impactSprites[dir][frame] = loadImage(path);
            }
        }

        stuckImpactSprites = new Image[4][1];
        for (int dir = 0; dir < 4; dir++) {
            String path = "Images/Enemies/Narval/Atrack/" +
                    directions[dir] + "/16.png";
            stuckImpactSprites[dir][0] = loadImage(path);
        }

        stuckExitSprites = new Image[4][STUCK_EXIT_FRAMES];
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < STUCK_EXIT_FRAMES; frame++) {
                String path = "Images/Enemies/Narval/Atrack/" +
                        directions[dir] + "/" + (frame + 17) + ".png";
                stuckExitSprites[dir][frame] = loadImage(path);
            }
        }

        breakIceSprites = new Image[4][BREAK_ICE_FRAMES];
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < BREAK_ICE_FRAMES; frame++) {
                String path = "Images/Enemies/Narval/Atrack/" +
                        directions[dir] + "/" + (frame + 1) + ".png";
                breakIceSprites[dir][frame] = loadImage(path);
            }
        }

        System.out.println("Sprites de Narval cargados correctamente");
    }

    private Image loadImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                return icon.getImage();
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprite de Narval: " + path);
        }
        return null;
    }

    @Override
    public void setTargetSelector(TargetSelector selector) {
        this.targetSelector = selector;
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
        } else if (currentState == NarvalState.WALKING || currentState == NarvalState.CHARGING) {
            startNextMove();
        }
    }

    private void updateState() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - stateStartTime;

        switch (currentState) {
            case WALKING:
                if (shouldBreakIceInFront()) {
                    transitionToState(NarvalState.BREAKING_ICE);
                } else if (canChargeAtPlayer()) {
                    transitionToState(NarvalState.PREPARING);
                }
                break;

            case PREPARING:
                if (preparingFrame >= PREPARING_FRAMES - 1) {
                    transitionToState(NarvalState.CHARGING);
                }
                break;

            case CHARGING:
                break;

            case IMPACTING:
                if (impactFrame >= IMPACT_FRAMES - 1) {
                    transitionToState(NarvalState.STUCK_IMPACT);
                }
                break;

            case STUCK_IMPACT:
                if (elapsedTime >= STUCK_IMPACT_DURATION) {
                    transitionToState(NarvalState.STUCK_EXIT);
                }
                break;

            case STUCK_EXIT:
                if (stuckExitFrame >= STUCK_EXIT_FRAMES - 1) {
                    transitionToState(NarvalState.WALKING);
                }
                break;

            case BREAKING_ICE:
                if (breakIceFrame >= BREAK_ICE_FRAMES - 1) {
                    transitionToState(NarvalState.WALKING);
                }
                break;
        }
    }

    private void transitionToState(NarvalState newState) {
        currentState = newState;
        stateStartTime = System.currentTimeMillis();

        switch (newState) {
            case WALKING:
                currentSpeed = WALK_SPEED;
                walkFrame = 0;
                walkCounter = 0;
                randomMoveCounter = 0;
                isMoving = false;
                chargeDirection = null;
                break;

            case PREPARING:
                preparingFrame = 0;
                preparingCounter = 0;
                isMoving = false;
                break;

            case CHARGING:
                currentSpeed = CHARGE_SPEED;
                chargingFrame = 0;
                chargingCounter = 0;
                currentDirection = chargeDirection;
                isMoving = false;
                break;

            case IMPACTING:
                isMoving = false;
                impactFrame = 0;
                impactCounter = 0;
                break;

            case STUCK_IMPACT:
                isMoving = false;
                break;

            case STUCK_EXIT:
                isMoving = false;
                stuckExitFrame = 0;
                stuckExitCounter = 0;
                break;

            case BREAKING_ICE:
                breakIceFrame = 0;
                breakIceCounter = 0;
                shouldDestroyIce = false;
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

            case PREPARING:
                preparingCounter++;
                if (preparingCounter >= PREPARING_ANIMATION_SPEED) {
                    preparingCounter = 0;
                    preparingFrame++;
                }
                break;

            case CHARGING:
                chargingCounter++;
                if (chargingCounter >= CHARGING_ANIMATION_SPEED) {
                    chargingCounter = 0;
                    chargingFrame++;
                    if (chargingFrame >= CHARGING_FRAMES) {
                        chargingFrame = 0;
                    }
                }
                break;

            case IMPACTING:
                impactCounter++;
                if (impactCounter >= IMPACT_ANIMATION_SPEED) {
                    impactCounter = 0;
                    if (impactFrame < IMPACT_FRAMES - 1) {
                        impactFrame++;
                    }
                }
                break;

            case STUCK_IMPACT:
                break;

            case STUCK_EXIT:
                stuckExitCounter++;
                if (stuckExitCounter >= STUCK_EXIT_ANIMATION_SPEED) {
                    stuckExitCounter = 0;
                    if (stuckExitFrame < STUCK_EXIT_FRAMES - 1) {
                        stuckExitFrame++;
                    }
                }
                break;

            case BREAKING_ICE:
                breakIceCounter++;
                if (breakIceCounter >= BREAK_ICE_ANIMATION_SPEED) {
                    breakIceCounter = 0;
                    if (breakIceFrame < BREAK_ICE_FRAMES) {
                        breakIceFrame++;
                        if (breakIceFrame == 4) {
                            shouldDestroyIce = true;
                            Point nextPos = grid.getNextGridPosition(
                                    gridPosition.x, gridPosition.y, currentDirection);
                            destroyIceInstantly(nextPos.x, nextPos.y);
                        }
                    }
                }
                break;
        }
    }

    private void startNextMove() {
        if (currentState == NarvalState.CHARGING) {
            continueCharge();
        } else if (currentState == NarvalState.WALKING) {
            handleWalkingMovement();
        }
    }

    private void handleWalkingMovement() {
        randomMoveCounter++;

        if (randomMoveCounter >= RANDOM_MOVE_FREQUENCY) {
            currentDirection = getRandomDirection();
            randomMoveCounter = 0;
        }

        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, currentDirection);

        if (!collisionDetector.isBlocked(nextPos.x, nextPos.y)) {
            startMovement(currentDirection);
        } else {
            currentDirection = getRandomDirection();
            nextPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, currentDirection);

            if (!collisionDetector.isBlocked(nextPos.x, nextPos.y)) {
                startMovement(currentDirection);
            }
        }
    }

    private void continueCharge() {
        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, currentDirection);

        if (!grid.isInBounds(nextPos.x, nextPos.y) || isSolidWall(nextPos.x, nextPos.y)) {
            transitionToState(NarvalState.IMPACTING);
            return;
        }

        if (hasIceBlockAt(nextPos.x, nextPos.y)) {
            destroyIceInstantly(nextPos.x, nextPos.y);
        }

        startMovement(currentDirection);
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

    private boolean canChargeAtPlayer() {
        if (targetSelector == null || !targetSelector.hasActivePlayer()) {
            return false;
        }

        Player targetPlayer = targetSelector.findNearestActivePlayer(
                gridPosition.x, gridPosition.y);

        if (targetPlayer == null) {
            return false;
        }

        Point playerGrid = grid.pixelToGrid(
                targetPlayer.getX(), targetPlayer.getY());

        boolean aligned = false;
        Player.Direction detectedDirection = null;

        if (playerGrid.y == gridPosition.y && playerGrid.x != gridPosition.x) {
            aligned = true;
            detectedDirection = (playerGrid.x > gridPosition.x) ?
                    Player.Direction.RIGHT : Player.Direction.LEFT;
        } else if (playerGrid.x == gridPosition.x && playerGrid.y != gridPosition.y) {
            aligned = true;
            detectedDirection = (playerGrid.y > gridPosition.y) ?
                    Player.Direction.DOWN : Player.Direction.UP;
        }

        if (!aligned) {
            return false;
        }

        int distance = Math.abs(playerGrid.x - gridPosition.x) +
                Math.abs(playerGrid.y - gridPosition.y);

        if (distance > DETECTION_RANGE || distance == 0) {
            return false;
        }

        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, detectedDirection);

        if (hasIceBlockAt(nextPos.x, nextPos.y)) {
            return false;
        }

        currentDirection = detectedDirection;
        chargeDirection = detectedDirection;
        return true;
    }

    private boolean shouldBreakIceInFront() {
        Point nextPos = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, currentDirection);

        return hasIceBlockAt(nextPos.x, nextPos.y);
    }

    private boolean hasIceBlockAt(int gridX, int gridY) {
        if (!grid.isInBounds(gridX, gridY)) {
            return false;
        }

        return collisionDetector.isBlocked(gridX, gridY) && !isSolidWall(gridX, gridY);
    }

    private boolean isSolidWall(int gridX, int gridY) {
        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();

        return gridX <= 0 || gridX >= gridWidth - 1 ||
                gridY <= 0 || gridY >= gridHeight - 1;
    }

    private void destroyIceInstantly(int gridX, int gridY) {
        if (gameMap == null) {
            return;
        }

        java.util.List<Block> blocks = gameMap.getBlocks();

        for (Block block : blocks) {
            Point blockGrid = grid.pixelToGrid(block.getX(), block.getY());

            if (blockGrid.x == gridX && blockGrid.y == gridY &&
                    block.getType() == Block.BlockType.ICE) {

                block.startDestroy();
                System.out.println("Narval destruyo hielo en: (" + gridX + ", " + gridY + ")");
            }
        }
    }

    private Player.Direction getRandomDirection() {
        Player.Direction[] directions = {
                Player.Direction.UP, Player.Direction.DOWN,
                Player.Direction.LEFT, Player.Direction.RIGHT
        };
        return directions[(int)(Math.random() * directions.length)];
    }

    public boolean shouldDestroyIceNow() {
        return shouldDestroyIce;
    }

    public void resetIceDestruction() {
        shouldDestroyIce = false;
    }

    @Override
    public int getWidth() {
        switch (currentState) {
            case WALKING:
                return WALK_SPRITE_WIDTH;
            case PREPARING:
            case CHARGING:
            case STUCK_IMPACT:
                switch (currentDirection) {
                    case DOWN:
                        return CHARGE_DOWN_SPRITE_WIDTH;
                    case UP:
                        return CHARGE_UP_SPRITE_WIDTH;
                    case LEFT:
                    case RIGHT:
                        return CHARGE_SIDE_SPRITE_WIDTH;
                    default:
                        return CHARGE_DOWN_SPRITE_WIDTH;
                }
            case BREAKING_ICE:
                return BREAK_ICE_SPRITE_WIDTH;
            default:
                return WALK_SPRITE_WIDTH;
        }
    }

    @Override
    public int getHeight() {
        switch (currentState) {
            case WALKING:
                return WALK_SPRITE_HEIGHT;
            case PREPARING:
            case CHARGING:
            case STUCK_EXIT:
                switch (currentDirection) {
                    case DOWN:
                        return CHARGE_DOWN_SPRITE_HEIGHT;
                    case UP:
                        return CHARGE_UP_SPRITE_HEIGHT;
                    case LEFT:
                    case RIGHT:
                        return CHARGE_SIDE_SPRITE_HEIGHT;
                    default:
                        return CHARGE_DOWN_SPRITE_HEIGHT;
                }
            case BREAKING_ICE:
                return BREAK_ICE_SPRITE_HEIGHT;
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

            case PREPARING:
                if (preparingSprites != null && preparingSprites[dirIndex] != null &&
                        preparingFrame < PREPARING_FRAMES &&
                        preparingSprites[dirIndex][preparingFrame] != null) {
                    return preparingSprites[dirIndex][preparingFrame];
                }
                break;

            case CHARGING:
                if (chargingSprites != null && chargingSprites[dirIndex] != null &&
                        chargingFrame < CHARGING_FRAMES &&
                        chargingSprites[dirIndex][chargingFrame] != null) {
                    return chargingSprites[dirIndex][chargingFrame];
                }
                break;

            case IMPACTING:
                if (impactSprites != null && impactSprites[dirIndex] != null &&
                        impactFrame < IMPACT_FRAMES &&
                        impactSprites[dirIndex][impactFrame] != null) {
                    return impactSprites[dirIndex][impactFrame];
                }
                break;

            case STUCK_IMPACT:
                if (stuckImpactSprites != null && stuckImpactSprites[dirIndex] != null &&
                        stuckImpactSprites[dirIndex][0] != null) {
                    return stuckImpactSprites[dirIndex][0];
                }
                break;

            case STUCK_EXIT:
                if (stuckExitSprites != null && stuckExitSprites[dirIndex] != null &&
                        stuckExitFrame < STUCK_EXIT_FRAMES &&
                        stuckExitSprites[dirIndex][stuckExitFrame] != null) {
                    return stuckExitSprites[dirIndex][stuckExitFrame];
                }
                break;

            case BREAKING_ICE:
                if (breakIceSprites != null && breakIceSprites[dirIndex] != null &&
                        breakIceFrame < BREAK_ICE_FRAMES &&
                        breakIceSprites[dirIndex][breakIceFrame] != null) {
                    return breakIceSprites[dirIndex][breakIceFrame];
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
        return EnemyType.NARVAL;
    }

    public Player.Direction getCurrentDirection() {
        return currentDirection;
    }

    public boolean isPreparing() {
        return currentState == NarvalState.PREPARING;
    }

    public boolean isCharging() {
        return currentState == NarvalState.CHARGING;
    }

    public boolean isStuck() {
        return currentState == NarvalState.IMPACTING ||
                currentState == NarvalState.STUCK_IMPACT ||
                currentState == NarvalState.STUCK_EXIT;
    }

    public boolean isBreakingIce() {
        return currentState == NarvalState.BREAKING_ICE;
    }
}