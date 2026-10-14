package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Implementacion del enemigo Troll
 * Se mueve en un patron cuadrado, girando a la derecha cuando encuentra un obstaculo
 */
public class TrollEnemy extends Enemy {
    private Image[][] sprites;
    private int animationFrame;
    private int animationCounter;
    private static final int ANIMATION_SPEED = 8;
    private static final int TOTAL_FRAMES = 8;
    private static final int MOVE_SPEED = 1;
    private static final int ENEMY_WIDTH = 52;
    private static final int ENEMY_HEIGHT = 62;

    public TrollEnemy(int gridX, int gridY, GridSystem grid, CollisionDetector collisionDetector) {
        super(gridX, gridY, grid, collisionDetector);
        this.currentDirection = Player.Direction.RIGHT;
        this.animationFrame = 0;
        this.animationCounter = 0;

        loadSprites();
        startNextMove();
    }

    private void loadSprites() {
        sprites = new Image[4][TOTAL_FRAMES];

        String[] directions = {"Down", "Up", "Left", "Right"};

        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < TOTAL_FRAMES; frame++) {
                String path = "Images/Enemies/Troll/Walk/" + directions[dir] + "/" + (frame + 1) + ".png";
                try {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        sprites[dir][frame] = icon.getImage();
                    } else {
                        System.out.println("Advertencia: No se pudo cargar " + path);
                    }
                } catch (Exception e) {
                    System.out.println("Error cargando sprite de troll: " + path);
                }
            }
        }

        System.out.println("Sprites de troll cargados: " + directions.length + " direcciones, " + TOTAL_FRAMES + " frames");
    }

    @Override
    public void update() {
        if (!active) {
            return;
        }

        updateAnimation();

        if (isMoving) {
            moveTowardsTarget(MOVE_SPEED);
        } else {
            startNextMove();
        }
    }

    private void updateAnimation() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= TOTAL_FRAMES) {
                animationFrame = 0;
            }
        }
    }

    private void startNextMove() {
        Point nextPos = collisionDetector.getValidNextPosition(
                gridPosition.x, gridPosition.y, currentDirection);

        if (nextPos.equals(gridPosition)) {
            turnRight();
            nextPos = collisionDetector.getValidNextPosition(
                    gridPosition.x, gridPosition.y, currentDirection);

            if (nextPos.equals(gridPosition)) {
                turnRight();
                nextPos = collisionDetector.getValidNextPosition(
                        gridPosition.x, gridPosition.y, currentDirection);

                if (nextPos.equals(gridPosition)) {
                    turnRight();
                    nextPos = collisionDetector.getValidNextPosition(
                            gridPosition.x, gridPosition.y, currentDirection);
                }
            }
        }

        Point targetPixel = grid.gridToPixel(nextPos.x, nextPos.y);
        targetX = targetPixel.x;
        targetY = targetPixel.y;

        if (targetX != x || targetY != y) {
            isMoving = true;
        }
    }

    private void turnRight() {
        switch (currentDirection) {
            case UP:
                currentDirection = Player.Direction.RIGHT;
                break;
            case RIGHT:
                currentDirection = Player.Direction.DOWN;
                break;
            case DOWN:
                currentDirection = Player.Direction.LEFT;
                break;
            case LEFT:
                currentDirection = Player.Direction.UP;
                break;
        }
    }

    @Override
    public int getWidth() {
        return ENEMY_WIDTH;
    }

    @Override
    public int getHeight() {
        return ENEMY_HEIGHT;
    }

    @Override
    public Image getCurrentSprite() {
        int dirIndex = getDirectionIndex();
        if (sprites != null && sprites[dirIndex] != null && sprites[dirIndex][animationFrame] != null) {
            return sprites[dirIndex][animationFrame];
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
    public void setTargetSelector(TargetSelector selector) {
        // Trolls no persiguen, no hacen nada
    }

    @Override
    public EnemyType getType() {
        return EnemyType.TROLL;
    }
}