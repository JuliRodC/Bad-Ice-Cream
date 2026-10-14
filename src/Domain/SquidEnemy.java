package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;
import java.util.List;

/**
 * Implementacion del enemigo Squid (Calamar)
 * Persigue al jugador activamente y rompe bloques de hielo en su camino
 */
public class SquidEnemy extends Enemy {
    private PathFinder pathFinder;
    private TargetSelector targetSelector;
    private int pathUpdateCounter;
    private static final int PATH_UPDATE_FREQUENCY = 20;
    private List<Player.Direction> currentPath;
    private static final int MOVE_SPEED = 2;

    private boolean breakingIce;
    private int breakIceFrame;
    private int breakIceCounter;
    private static final int BREAK_ICE_ANIMATION_SPEED = 5;
    private static final int BREAK_ICE_FRAMES = 12;
    private boolean shouldDestroyIce;

    private Image[] walkSprites;
    private int animationFrame;
    private int animationCounter;
    private static final int ANIMATION_SPEED = 7;
    private static final int WALK_FRAMES = 11;

    private Image[][] breakIceSprites;

    private static final int WALK_SPRITE_WIDTH = 45;
    private static final int WALK_SPRITE_HEIGHT = 85;
    private static final int BREAK_ICE_SPRITE_WIDTH = 75;
    private static final int BREAK_ICE_SPRITE_HEIGHT = 105;

    public SquidEnemy(int gridX, int gridY, GridSystem grid,
                      CollisionDetector collisionDetector, PathFinder pathFinder) {
        super(gridX, gridY, grid, collisionDetector);
        this.pathFinder = pathFinder;
        this.currentDirection = Player.Direction.DOWN;
        this.animationFrame = 0;
        this.animationCounter = 0;
        this.pathUpdateCounter = 0;
        this.currentPath = null;

        this.breakingIce = false;
        this.breakIceFrame = 0;
        this.breakIceCounter = 0;
        this.shouldDestroyIce = false;

        loadSprites();
    }

    private void loadSprites() {
        walkSprites = new Image[WALK_FRAMES];

        for (int i = 0; i < WALK_FRAMES; i++) {
            String path = "Images/Enemies/Squid/Walk/Down/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    walkSprites[i] = icon.getImage();
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de squid walk: " + path);
            }
        }

        breakIceSprites = new Image[4][BREAK_ICE_FRAMES];
        String[] directions = {"Down", "Up", "Left", "Right"};

        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < BREAK_ICE_FRAMES; frame++) {
                String path = "Images/Enemies/Squid/Broke Ice/" +
                        directions[dir] + "/" + (frame + 1) + ".png";
                try {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        breakIceSprites[dir][frame] = icon.getImage();
                    }
                } catch (Exception e) {
                    System.out.println("Error cargando sprite de squid break ice: " + path);
                }
            }
        }

        System.out.println("Sprites de squid cargados correctamente");
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

        if (breakingIce) {
            updateBreakIceAnimation();
        } else if (isMoving) {
            updateAnimation();
            moveTowardsTarget(MOVE_SPEED);
        } else {
            updatePathAndMovement();
        }
    }

    private void updateBreakIceAnimation() {
        breakIceCounter++;
        if (breakIceCounter >= BREAK_ICE_ANIMATION_SPEED) {
            breakIceCounter = 0;
            breakIceFrame++;

            if (breakIceFrame == 6) {
                shouldDestroyIce = true;
            }

            if (breakIceFrame >= BREAK_ICE_FRAMES) {
                breakingIce = false;
                breakIceFrame = 0;
                shouldDestroyIce = false;
            }
        }
    }

    private void updateAnimation() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= WALK_FRAMES) {
                animationFrame = 0;
            }
        }
    }

    private void updatePathAndMovement() {
        if (targetSelector == null || !targetSelector.hasActivePlayer()) {
            return;
        }

        Player targetPlayer = targetSelector.findNearestActivePlayer(
                gridPosition.x, gridPosition.y);

        if (targetPlayer == null) {
            return;
        }

        pathUpdateCounter++;

        if (currentPath == null || currentPath.isEmpty() ||
                pathUpdateCounter >= PATH_UPDATE_FREQUENCY) {
            calculatePathToPlayer();
            pathUpdateCounter = 0;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            Player.Direction nextDirection = currentPath.get(0);
            Point nextPos = grid.getNextGridPosition(
                    gridPosition.x, gridPosition.y, nextDirection);

            if (hasIceBlockAt(nextPos.x, nextPos.y)) {
                currentDirection = nextDirection;
                startBreakingIce();
                currentPath.remove(0);
            } else if (!collisionDetector.isBlocked(nextPos.x, nextPos.y)) {
                currentDirection = nextDirection;
                startMovement(nextDirection);
                currentPath.remove(0);
            } else {
                calculatePathToPlayer();
            }
        }
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

    private void startBreakingIce() {
        breakingIce = true;
        breakIceFrame = 0;
        breakIceCounter = 0;
        shouldDestroyIce = false;
    }

    private boolean hasIceBlockAt(int gridX, int gridY) {
        if (!grid.isInBounds(gridX, gridY)) {
            return false;
        }

        return collisionDetector.isBlocked(gridX, gridY) && !isWall(gridX, gridY);
    }

    private boolean isWall(int gridX, int gridY) {
        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();

        return gridX <= 1 || gridX >= gridWidth - 2 ||
                gridY <= 1 || gridY >= gridHeight - 2;
    }

    public boolean shouldDestroyIceNow() {
        return shouldDestroyIce;
    }

    public void resetIceDestruction() {
        shouldDestroyIce = false;
    }

    @Override
    public int getWidth() {
        if (breakingIce) {
            return BREAK_ICE_SPRITE_WIDTH;
        }
        return WALK_SPRITE_WIDTH;
    }

    @Override
    public int getHeight() {
        if (breakingIce) {
            return BREAK_ICE_SPRITE_HEIGHT;
        }
        return WALK_SPRITE_HEIGHT;
    }

    @Override
    public Image getCurrentSprite() {
        if (breakingIce) {
            int dirIndex = getDirectionIndexForBreakIce();
            if (breakIceSprites != null &&
                    breakIceSprites[dirIndex] != null &&
                    breakIceFrame < BREAK_ICE_FRAMES &&
                    breakIceSprites[dirIndex][breakIceFrame] != null) {
                return breakIceSprites[dirIndex][breakIceFrame];
            }
        }

        if (walkSprites != null &&
                animationFrame < WALK_FRAMES &&
                walkSprites[animationFrame] != null) {
            return walkSprites[animationFrame];
        }

        return null;
    }

    private int getDirectionIndexForBreakIce() {
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
        return EnemyType.SQUID;
    }

    public Player.Direction getCurrentDirection() {
        return currentDirection;
    }

    public boolean isBreakingIce() {
        return breakingIce;
    }
}