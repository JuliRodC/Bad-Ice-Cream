package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Implementacion de la fruta Pineapple (movil con animacion de salto)
 * Se mueve aleatoriamente por el mapa cuando el jugador se mueve
 * Otorga 200 puntos al ser recolectada
 */
public class PineappleFruit extends Fruit {
    private Image[] sprites;
    private CollisionDetector collisionDetector;
    private static final int ANIMATION_SPEED = 8;
    private static final int TOTAL_FRAMES = 7;
    private static final int POINTS = 200;
    private static final int FRUIT_SIZE_WIDTH = 40;
    private static final int FRUIT_SIZE_HEIGHT = 54;

    private boolean isMoving;
    private int targetX, targetY;
    private Point targetGridPosition;
    private int moveSpeed;

    public PineappleFruit(int gridX, int gridY, GridSystem grid, CollisionDetector collisionDetector) {
        super(gridX, gridY, grid);
        this.collisionDetector = collisionDetector;
        this.targetX = this.x;
        this.targetY = this.y;
        this.isMoving = false;
        this.moveSpeed = 2;

        loadSprites();
    }

    private void loadSprites() {
        sprites = new Image[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            String path = "Images/Fruits/Pineaple/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    sprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de pina: " + path);
            }
        }

        System.out.println("Sprites de pina cargados: " + TOTAL_FRAMES + " frames");
    }

    @Override
    public void update() {
        if (collected) {
            return;
        }

        updateAnimation(ANIMATION_SPEED, TOTAL_FRAMES);

        if (isMoving) {
            performMovement();
        }
    }

    private void performMovement() {
        boolean reachedTarget = true;

        if (x < targetX) {
            x += moveSpeed;
            if (x >= targetX) x = targetX;
            else reachedTarget = false;
        } else if (x > targetX) {
            x -= moveSpeed;
            if (x <= targetX) x = targetX;
            else reachedTarget = false;
        }

        if (y < targetY) {
            y += moveSpeed;
            if (y >= targetY) y = targetY;
            else reachedTarget = false;
        } else if (y > targetY) {
            y -= moveSpeed;
            if (y <= targetY) y = targetY;
            else reachedTarget = false;
        }

        if (reachedTarget) {
            gridPosition = new Point(targetGridPosition.x, targetGridPosition.y);
            isMoving = false;
        }
    }

    public void moveRandomly() {
        if (isMoving || collected) {
            return;
        }

        Player.Direction randomDirection = getRandomDirection();
        Point nextGrid = grid.getNextGridPosition(
                gridPosition.x, gridPosition.y, randomDirection);

        if (grid.isInBounds(nextGrid.x, nextGrid.y) &&
                !collisionDetector.isBlocked(nextGrid.x, nextGrid.y)) {

            targetGridPosition = nextGrid;
            Point targetPixel = grid.gridToPixel(nextGrid.x, nextGrid.y);
            targetX = targetPixel.x;
            targetY = targetPixel.y;
            isMoving = true;
        }
    }

    private Player.Direction getRandomDirection() {
        Player.Direction[] directions = {
                Player.Direction.UP,
                Player.Direction.DOWN,
                Player.Direction.LEFT,
                Player.Direction.RIGHT
        };
        int randomIndex = (int)(Math.random() * directions.length);
        return directions[randomIndex];
    }

    @Override
    public Image getCurrentSprite() {
        if (sprites != null && sprites.length > 0 && sprites[animationFrame] != null) {
            return sprites[animationFrame];
        }
        return null;
    }

    @Override
    public FruitType getType() {
        return FruitType.PINEAPPLE;
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public int getWidth() {
        return FRUIT_SIZE_WIDTH;
    }

    @Override
    public int getHeight() {
        return FRUIT_SIZE_HEIGHT;
    }

    public boolean isMoving() {
        return isMoving;
    }
}