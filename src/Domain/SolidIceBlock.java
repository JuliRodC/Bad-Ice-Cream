package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Bloque de hielo que comienza directamente en estado solido
 * Se usa para hielos pre-existentes en el mapa
 */
public class SolidIceBlock extends Block {
    private BlockState state;
    private final int DESTROY_ANIMATION_SPEED = 8;
    private final int DESTROY_FRAMES = 9;

    private Image solidSprite;
    private Image[] destroySprites;

    private static final int SOLID_SPRITE_WIDTH = 42;
    private static final int SOLID_SPRITE_HEIGHT = 70;
    private static final int DESTROY_NORMAL_WIDTH = 42;
    private static final int DESTROY_NORMAL_HEIGHT = 60;
    private static final int DESTROY_BIG_WIDTH = 42;
    private static final int DESTROY_BIG_HEIGHT = 70;
    private static final int CELL_SIZE = 42;

    private enum BlockState {
        SOLID, DESTROYING, DESTROYED
    }

    public SolidIceBlock(int x, int y) {
        super(x, y);
        this.state = BlockState.SOLID;
        loadSprites();
    }

    private void loadSprites() {
        String solidPath = "Images/Ice/Create/Right/7.png";
        try {
            ImageIcon icon = new ImageIcon(solidPath);
            if (icon.getIconWidth() > 0) {
                solidSprite = icon.getImage();
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprite solido: " + solidPath);
        }

        destroySprites = new Image[DESTROY_FRAMES];
        for (int i = 0; i < DESTROY_FRAMES; i++) {
            String path = "Images/Ice/Destroy/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    destroySprites[i] = icon.getImage();
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de destruccion: " + path);
            }
        }
    }

    @Override
    public void update() {
        if (state == BlockState.DESTROYING) {
            animationCounter++;
            if (animationCounter >= DESTROY_ANIMATION_SPEED) {
                animationCounter = 0;
                animationFrame++;

                if (animationFrame >= DESTROY_FRAMES) {
                    state = BlockState.DESTROYED;
                }
            }
        }
    }

    @Override
    public void startDestroy() {
        if (state == BlockState.SOLID) {
            state = BlockState.DESTROYING;
            animationFrame = 0;
            animationCounter = 0;
        }
    }

    @Override
    public boolean isDestroyed() {
        return state == BlockState.DESTROYED;
    }

    @Override
    public Image getCurrentSprite() {
        switch (state) {
            case SOLID:
                return solidSprite;

            case DESTROYING:
                if (animationFrame < DESTROY_FRAMES) {
                    return destroySprites[animationFrame];
                }
                return null;

            case DESTROYED:
                return null;
        }
        return null;
    }

    @Override
    public int getX() {
        if (state == BlockState.DESTROYING) {
            if (animationFrame >= 7) {
                return x - (DESTROY_BIG_WIDTH - CELL_SIZE) / 2;
            } else {
                return x - (DESTROY_NORMAL_WIDTH - CELL_SIZE) / 2;
            }
        } else if (state == BlockState.SOLID) {
            return x - (SOLID_SPRITE_WIDTH - CELL_SIZE) / 2;
        }
        return x;
    }

    @Override
    public int getY() {
        if (state == BlockState.DESTROYING) {
            int offset = 10;
            if (animationFrame >= 7) {
                return y - (DESTROY_BIG_HEIGHT - CELL_SIZE) / 2 + offset;
            } else {
                return y - (DESTROY_NORMAL_HEIGHT - CELL_SIZE) / 2 + offset;
            }
        } else if (state == BlockState.SOLID) {
            int offset = 14;
            return y - (SOLID_SPRITE_HEIGHT - CELL_SIZE) / 2 + offset;
        }
        return y;
    }

    @Override
    public int getWidth() {
        if (state == BlockState.DESTROYING) {
            if (animationFrame >= 7) {
                return DESTROY_BIG_WIDTH;
            } else {
                return DESTROY_NORMAL_WIDTH;
            }
        } else if (state == BlockState.SOLID) {
            return SOLID_SPRITE_WIDTH;
        }
        return CELL_SIZE;
    }

    @Override
    public int getHeight() {
        if (state == BlockState.DESTROYING) {
            if (animationFrame >= 7) {
                return DESTROY_BIG_HEIGHT;
            } else {
                return DESTROY_NORMAL_HEIGHT;
            }
        } else if (state == BlockState.SOLID) {
            return SOLID_SPRITE_HEIGHT;
        }
        return CELL_SIZE;
    }

    @Override
    public boolean isAnimating() {
        return state == BlockState.DESTROYING;
    }

    @Override
    public boolean isSolid() {
        return state == BlockState.SOLID;
    }

    @Override
    public BlockType getType() {
        return BlockType.ICE;
    }
}