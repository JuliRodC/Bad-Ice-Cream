package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Implementacion de la fruta Cactus (con puas peligrosas)
 * Alterna entre estar seguro y peligroso cada 30 segundos
 * Otorga 250 puntos al ser recolectada
 */
public class CactusFruit extends Fruit {
    private Image[] staySprites;
    private Image[] dangerOutSprites;
    private Image[] stayDangerSprites;
    private Image[] dangerInSprites;

    private static final int STAY_ANIMATION_SPEED = 10;
    private static final int DANGER_ANIMATION_SPEED = 4;
    private static final int STAY_FRAMES = 6;
    private static final int DANGER_OUT_FRAMES = 11;
    private static final int STAY_DANGER_FRAMES = 8;
    private static final int DANGER_IN_FRAMES = 3;
    private static final int POINTS = 250;

    private static final int SPRITE_WIDTH = 70;
    private static final int SPRITE_HEIGHT = 74;

    private CactusState state;
    private int stateTimer;
    private static final int STATE_DURATION = 1800;

    private enum CactusState {
        SAFE,
        GROWING_SPIKES,
        DANGEROUS,
        RETRACTING_SPIKES
    }

    public CactusFruit(int gridX, int gridY, GridSystem grid) {
        super(gridX, gridY, grid);
        this.state = CactusState.SAFE;
        this.stateTimer = 0;

        loadSprites();
    }

    private void loadSprites() {
        staySprites = new Image[STAY_FRAMES];
        for (int i = 0; i < STAY_FRAMES; i++) {
            String path = "Images/Fruits/Captus/Stay/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    staySprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de cactus: " + path);
            }
        }

        dangerOutSprites = new Image[DANGER_OUT_FRAMES];
        for (int i = 0; i < DANGER_OUT_FRAMES; i++) {
            String path = "Images/Fruits/Captus/DangerOut/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    dangerOutSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de puas saliendo: " + path);
            }
        }

        stayDangerSprites = new Image[STAY_DANGER_FRAMES];
        for (int i = 0; i < STAY_DANGER_FRAMES; i++) {
            String path = "Images/Fruits/Captus/StayDanger/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    stayDangerSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de cactus peligroso: " + path);
            }
        }

        dangerInSprites = new Image[DANGER_IN_FRAMES];
        for (int i = 0; i < DANGER_IN_FRAMES; i++) {
            String path = "Images/Fruits/Captus/DangerIn/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    dangerInSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de puas entrando: " + path);
            }
        }

        System.out.println("Sprites de cactus cargados correctamente");
    }

    @Override
    public void update() {
        if (collected) {
            return;
        }

        switch (state) {
            case SAFE:
                updateSafeAnimation();
                updateStateTimer();
                break;

            case GROWING_SPIKES:
                updateGrowingSpikesAnimation();
                break;

            case DANGEROUS:
                updateDangerousAnimation();
                updateStateTimer();
                break;

            case RETRACTING_SPIKES:
                updateRetractingSpikesAnimation();
                break;
        }
    }

    private void updateSafeAnimation() {
        animationCounter++;
        if (animationCounter >= STAY_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= STAY_FRAMES) {
                animationFrame = 0;
            }
        }
    }

    private void updateGrowingSpikesAnimation() {
        animationCounter++;
        if (animationCounter >= DANGER_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= DANGER_OUT_FRAMES) {
                state = CactusState.DANGEROUS;
                animationFrame = 0;
                animationCounter = 0;
                stateTimer = 0;
                System.out.println("Cactus ahora es peligroso en: " + gridPosition);
            }
        }
    }

    private void updateDangerousAnimation() {
        animationCounter++;
        if (animationCounter >= STAY_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= STAY_DANGER_FRAMES) {
                animationFrame = 0;
            }
        }
    }

    private void updateRetractingSpikesAnimation() {
        animationCounter++;
        if (animationCounter >= DANGER_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= DANGER_IN_FRAMES) {
                state = CactusState.SAFE;
                animationFrame = 0;
                animationCounter = 0;
                stateTimer = 0;
                System.out.println("Cactus ahora es seguro en: " + gridPosition);
            }
        }
    }

    private void updateStateTimer() {
        stateTimer++;

        if (stateTimer >= STATE_DURATION) {
            if (state == CactusState.SAFE) {
                state = CactusState.GROWING_SPIKES;
                animationFrame = 0;
                animationCounter = 0;
                System.out.println("Cactus sacando puas en: " + gridPosition);
            } else if (state == CactusState.DANGEROUS) {
                state = CactusState.RETRACTING_SPIKES;
                animationFrame = 0;
                animationCounter = 0;
                System.out.println("Cactus metiendo puas en: " + gridPosition);
            }
        }
    }

    public boolean isDangerous() {
        return state == CactusState.DANGEROUS || state == CactusState.GROWING_SPIKES;
    }

    public boolean isSafe() {
        return state == CactusState.SAFE || state == CactusState.RETRACTING_SPIKES;
    }

    @Override
    public void collect() {
        if (isSafe()) {
            collected = true;
            System.out.println("Cactus recolectado en posicion: " + gridPosition + " (+250 puntos)");
        }
    }

    @Override
    public Image getCurrentSprite() {
        switch (state) {
            case SAFE:
                if (staySprites != null && staySprites.length > 0 &&
                        staySprites[animationFrame] != null) {
                    return staySprites[animationFrame];
                }
                break;

            case GROWING_SPIKES:
                if (dangerOutSprites != null && animationFrame < DANGER_OUT_FRAMES &&
                        dangerOutSprites[animationFrame] != null) {
                    return dangerOutSprites[animationFrame];
                }
                break;

            case DANGEROUS:
                if (stayDangerSprites != null && stayDangerSprites.length > 0 &&
                        stayDangerSprites[animationFrame] != null) {
                    return stayDangerSprites[animationFrame];
                }
                break;

            case RETRACTING_SPIKES:
                if (dangerInSprites != null && animationFrame < DANGER_IN_FRAMES &&
                        dangerInSprites[animationFrame] != null) {
                    return dangerInSprites[animationFrame];
                }
                break;
        }
        return null;
    }

    @Override
    public FruitType getType() {
        return FruitType.CACTUS;
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public int getWidth() {
        return SPRITE_WIDTH;
    }

    @Override
    public int getHeight() {
        return SPRITE_HEIGHT;
    }

    public boolean shouldKillPlayer(int playerX, int playerY) {
        if (collected) {
            return false;
        }

        Point playerGridPos = grid.pixelToGrid(playerX, playerY);

        boolean samePosition = playerGridPos.x == gridPosition.x &&
                playerGridPos.y == gridPosition.y;

        return samePosition && isDangerous();
    }
}