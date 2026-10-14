package Domain;

import java.awt.Image;
import java.awt.Point;

public abstract class Fruit {
    protected int x, y;
    protected Point gridPosition;
    protected boolean collected;
    protected GridSystem grid;
    protected int animationFrame;
    protected int animationCounter;

    public Fruit(int gridX, int gridY, GridSystem grid) {
        this.gridPosition = new Point(gridX, gridY);
        this.grid = grid;

        Point pixelPos = grid.gridToPixel(gridX, gridY);
        this.x = pixelPos.x;
        this.y = pixelPos.y;
        this.collected = false;
        this.animationFrame = 0;
        this.animationCounter = 0;
    }

    // Metodos abstractos
    public abstract void update();
    public abstract Image getCurrentSprite();
    public abstract FruitType getType();
    public abstract int getPoints();
    public abstract int getWidth();
    public abstract int getHeight();

    // Implementacion compartida
    public int getX() { return x; }
    public int getY() { return y; }
    public Point getGridPosition() { return gridPosition; }
    public boolean isCollected() { return collected; }

    public void collect() {
        collected = true;
        System.out.println(getType() + " recolectada en: " +
                gridPosition + " (+" + getPoints() + " puntos)");
    }

    public boolean collidesWith(int playerX, int playerY,
                                int playerWidth, int playerHeight) {
        if (collected) return false;

        Point playerGridPos = grid.pixelToGrid(playerX, playerY);
        return playerGridPos.x == gridPosition.x &&
                playerGridPos.y == gridPosition.y;
    }

    protected void updateAnimation(int animationSpeed, int totalFrames) {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            animationFrame++;
            if (animationFrame >= totalFrames) {
                animationFrame = 0;
            }
        }
    }

    public enum FruitType {
        BANANA, GRAPE, CHERRY, PINEAPPLE, CACTUS
    }
}
