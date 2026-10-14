package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Implementacion de una fogata
 * La fogata empieza encendida y mata al jugador al contacto
 * Puede apagarse con hielo y se reenciende despues de 10 segundos
 */
public class Campfire extends FireBlock {
    private final Point gridPosition;
    private final int cellSize;
    private FireState state;
    private boolean hasIceAbove;
    private int relightTimer;
    private static final int RELIGHT_TIME = 600;

    private Image rocksSprite;
    private Image[] flameSprites;

    private static final int ANIMATION_SPEED = 8;
    private static final int TOTAL_FLAME_FRAMES = 4;
    private static final int ROCKS_SPRITE_WIDTH = 105;
    private static final int ROCKS_SPRITE_HEIGHT = 105;
    private static final int FLAME_SPRITE_WIDTH = 30;
    private static final int FLAME_SPRITE_HEIGHT = 50;
    private static final int FLAME_Y_OFFSET = -10;

    private enum FireState {
        LIT,
        EXTINGUISHED,
        WAITING_RELIGHT
    }

    public Campfire(int gridX, int gridY, GridSystem grid) {
        super(grid.gridToPixel(gridX, gridY).x, grid.gridToPixel(gridX, gridY).y);
        this.gridPosition = new Point(gridX, gridY);
        this.cellSize = grid.getCellSize();
        this.state = FireState.LIT;
        this.hasIceAbove = false;
        this.relightTimer = 0;

        loadSprites();
    }

    private void loadSprites() {
        try {
            String rocksPath = "Images/Resources/Campfire/Rocks.png";
            ImageIcon icon = new ImageIcon(rocksPath);
            if (icon.getIconWidth() > 0) {
                rocksSprite = icon.getImage();
            } else {
                System.out.println("Advertencia: No se pudo cargar Rocks.png");
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprite de rocas: " + e.getMessage());
        }

        flameSprites = new Image[TOTAL_FLAME_FRAMES];
        for (int i = 0; i < TOTAL_FLAME_FRAMES; i++) {
            String path = "Images/Resources/Campfire/Flame/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    flameSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de llama: " + path);
            }
        }

        System.out.println("Sprites de fogata cargados correctamente");
    }

    @Override
    public void update() {
        if (state == FireState.LIT) {
            animationCounter++;
            if (animationCounter >= ANIMATION_SPEED) {
                animationCounter = 0;
                animationFrame++;

                if (animationFrame >= TOTAL_FLAME_FRAMES) {
                    animationFrame = 0;
                }
            }
        }

        if (state == FireState.WAITING_RELIGHT && !hasIceAbove) {
            relightTimer++;

            if (relightTimer >= RELIGHT_TIME) {
                state = FireState.LIT;
                relightTimer = 0;
                System.out.println("Fogata reencendida en: " + gridPosition);
            }
        }

        if (hasIceAbove && state != FireState.EXTINGUISHED) {
            state = FireState.EXTINGUISHED;
            relightTimer = 0;
        }

        if (!hasIceAbove && state == FireState.EXTINGUISHED) {
            state = FireState.WAITING_RELIGHT;
            relightTimer = 0;
        }
    }

    @Override
    public boolean isLit() {
        return state == FireState.LIT;
    }

    @Override
    public void extinguish() {
        if (state == FireState.LIT) {
            state = FireState.EXTINGUISHED;
            relightTimer = 0;
            System.out.println("Fogata apagada en: " + gridPosition);
        }
    }

    @Override
    public boolean isExtinguished() {
        return state == FireState.EXTINGUISHED || state == FireState.WAITING_RELIGHT;
    }

    @Override
    public boolean hasIceAbove() {
        return hasIceAbove;
    }

    @Override
    public void setHasIceAbove(boolean hasIce) {
        this.hasIceAbove = hasIce;
    }

    @Override
    public int getRemainingTimeToRelight() {
        if (state == FireState.WAITING_RELIGHT) {
            int remaining = (RELIGHT_TIME - relightTimer) / 60;
            return Math.max(0, remaining);
        }
        return 0;
    }

    @Override
    public int getWidth() {
        return cellSize;
    }

    @Override
    public int getHeight() {
        return cellSize;
    }

    @Override
    public Image getCurrentSprite() {
        return rocksSprite;
    }

    public Image getFlameSprite() {
        if (state == FireState.LIT && flameSprites != null &&
                flameSprites[animationFrame] != null) {
            return flameSprites[animationFrame];
        }
        return null;
    }

    public int getRocksSpriteWidth() {
        return ROCKS_SPRITE_WIDTH;
    }

    public int getRocksSpriteHeight() {
        return ROCKS_SPRITE_HEIGHT;
    }

    public int getFlameSpriteWidth() {
        return FLAME_SPRITE_WIDTH;
    }

    public int getFlameSpriteHeight() {
        return FLAME_SPRITE_HEIGHT;
    }

    public int getRocksRenderX() {
        return x + (cellSize - ROCKS_SPRITE_WIDTH) / 2;
    }

    public int getRocksRenderY() {
        return y + (cellSize - ROCKS_SPRITE_HEIGHT) / 2;
    }

    public int getFlameRenderX() {
        return x + (cellSize - FLAME_SPRITE_WIDTH) / 2;
    }

    public int getFlameRenderY() {
        return y + (cellSize - FLAME_SPRITE_HEIGHT) / 2 + FLAME_Y_OFFSET;
    }

    @Override
    public boolean isAnimating() {
        return state == FireState.LIT;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockType getType() {
        return BlockType.FIRE;
    }

    @Override
    public void startDestroy() {
        // Las fogatas no se destruyen como bloques normales
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    public Point getGridPosition() {
        return gridPosition;
    }
}