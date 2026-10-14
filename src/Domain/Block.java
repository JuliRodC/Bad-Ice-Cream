package Domain;

import java.awt.Image;

public abstract class Block {
    protected int x, y;
    protected int animationFrame;
    protected int animationCounter;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.animationFrame = 0;
        this.animationCounter = 0;
    }

    // Metodos abstractos
    public abstract void update();
    public abstract Image getCurrentSprite();
    public abstract BlockType getType();
    public abstract boolean isAnimating();
    public abstract boolean isSolid();
    public abstract int getWidth();
    public abstract int getHeight();

    // Implementacion compartida
    public int getX() { return x; }
    public int getY() { return y; }

    public abstract void startDestroy();
    public abstract boolean isDestroyed();

    public enum BlockType {
        ICE, WALL, EMPTY, FIRE
    }
}