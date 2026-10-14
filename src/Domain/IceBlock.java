package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Implementacion del bloque de hielo con animacion de creacion y destruccion
 */
public class IceBlock extends Block {
    private BlockState state;
    private final int CREATE_ANIMATION_SPEED = 5;
    private final int DESTROY_ANIMATION_SPEED = 8;
    private final int CREATE_FRAMES = 7;
    private final int DESTROY_FRAMES = 9;

    private Image[] createSprites;
    private Image[] destroySprites;
    private Image finalSprite;

    private static final int CREATE_SPRITE_WIDTH = 42;
    private static final int CREATE_SPRITE_HEIGHT = 60;
    private static final int SOLID_SPRITE_WIDTH = 42;
    private static final int SOLID_SPRITE_HEIGHT = 70;
    private static final int DESTROY_NORMAL_WIDTH = 42;
    private static final int DESTROY_NORMAL_HEIGHT = 60;
    private static final int DESTROY_BIG_WIDTH = 42;
    private static final int DESTROY_BIG_HEIGHT = 70;
    private static final int CELL_SIZE = 42;

    private enum BlockState {
        CREATING, SOLID, DESTROYING, DESTROYED
    }

    public IceBlock(int x, int y) {
        super(x, y);
        this.state = BlockState.CREATING;
        loadSprites();
    }

    private void loadSprites() {
        createSprites = new Image[CREATE_FRAMES];
        for (int i = 0; i < CREATE_FRAMES; i++) {
            String path = "Images/Ice/Create/Right/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    createSprites[i] = icon.getImage();
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de creacion: " + path);
            }
        }

        if (createSprites[CREATE_FRAMES - 1] != null) {
            finalSprite = createSprites[CREATE_FRAMES - 1];
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
        if (state == BlockState.CREATING) {
            animationCounter++;
            if (animationCounter >= CREATE_ANIMATION_SPEED) {
                animationCounter = 0;
                animationFrame++;

                if (animationFrame >= CREATE_FRAMES) {
                    animationFrame = CREATE_FRAMES - 1;
                    state = BlockState.SOLID;
                }
            }
        } else if (state == BlockState.DESTROYING) {
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
            case CREATING:
                if (animationFrame < CREATE_FRAMES) {
                    return createSprites[animationFrame];
                }
                return finalSprite;

            case SOLID:
                return finalSprite;

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
        if (state == BlockState.CREATING) {
            return x - (CREATE_SPRITE_WIDTH - CELL_SIZE) / 2;
        } else if (state == BlockState.DESTROYING) {
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
        if (state == BlockState.CREATING) {
            int offset = 10;
            return y - (CREATE_SPRITE_HEIGHT - CELL_SIZE) / 2 + offset;
        } else if (state == BlockState.DESTROYING) {
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
        if (state == BlockState.CREATING) {
            return CREATE_SPRITE_WIDTH;
        } else if (state == BlockState.DESTROYING) {
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
        if (state == BlockState.CREATING) {
            return CREATE_SPRITE_HEIGHT;
        } else if (state == BlockState.DESTROYING) {
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
        return state == BlockState.CREATING || state == BlockState.DESTROYING;
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