package Domain;

import java.awt.Image;
import java.awt.Point;

public abstract class Enemy {
    protected int x, y;
    protected Point gridPosition;
    protected boolean active;
    protected GridSystem grid;
    protected CollisionDetector collisionDetector;
    protected Player.Direction currentDirection;
    protected boolean isMoving;
    protected int targetX, targetY;

    public Enemy(int gridX, int gridY, GridSystem grid,
                 CollisionDetector collisionDetector) {
        this.gridPosition = new Point(gridX, gridY);
        this.grid = grid;
        this.collisionDetector = collisionDetector;

        Point pixelPos = grid.gridToPixel(gridX, gridY);
        this.x = pixelPos.x;
        this.y = pixelPos.y;
        this.targetX = this.x;
        this.targetY = this.y;
        this.active = true;
        this.isMoving = false;
    }

    // Metodos abstractos (cada enemigo los implementa diferente)
    public abstract void update();
    public abstract Image getCurrentSprite();

    // Implementacion compartida
    public int getX() { return x; }
    public int getY() { return y; }
    public Point getGridPosition() { return gridPosition; }
    public boolean isActive() { return active; }

    public void deactivate() {
        active = false;
        System.out.println(getType() + " desactivado en: " + gridPosition);
    }

    public boolean collidesWith(int playerX, int playerY,
                                int playerWidth, int playerHeight) {
        if (!active) return false;

        Point playerGridPos = grid.pixelToGrid(playerX, playerY);
        return playerGridPos.x == gridPosition.x &&
                playerGridPos.y == gridPosition.y;
    }

    protected void moveTowardsTarget(int speed) {
        boolean reachedTarget = true;

        if (x < targetX) {
            x += speed;
            if (x >= targetX) x = targetX;
            else reachedTarget = false;
        } else if (x > targetX) {
            x -= speed;
            if (x <= targetX) x = targetX;
            else reachedTarget = false;
        }

        if (y < targetY) {
            y += speed;
            if (y >= targetY) y = targetY;
            else reachedTarget = false;
        } else if (y > targetY) {
            y -= speed;
            if (y <= targetY) y = targetY;
            else reachedTarget = false;
        }

        if (reachedTarget) {
            gridPosition = grid.pixelToGrid(x, y);
            isMoving = false;
        }
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract EnemyType getType();
    public abstract void setTargetSelector(TargetSelector selector);

    public enum EnemyType {
        TROLL, SQUID, POT, NARVAL
    }
}
