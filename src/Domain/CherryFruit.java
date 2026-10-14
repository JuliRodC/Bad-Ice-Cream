package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion de la fruta Cherry (teletransportacion)
 * Se teletransporta cada 20 segundos a una ubicacion aleatoria libre
 * Tiene animaciones de desaparicion y aparicion
 * Otorga 150 puntos al ser recolectada
 */
public class CherryFruit extends Fruit {
    private GameMap gameMap;
    private Image[] staySprites;
    private Image[] apparitionSprites;
    private Image[] disappearSprites;

    private static final int STAY_ANIMATION_SPEED = 10;
    private static final int TELEPORT_ANIMATION_SPEED = 3;
    private static final int STAY_FRAMES = 4;
    private static final int TELEPORT_FRAMES = 30;
    private static final int POINTS = 150;

    private static final int STAY_SPRITE_WIDTH = 37;
    private static final int STAY_SPRITE_HEIGHT = 42;
    private static final int APPARITION_SPRITE_WIDTH = 130;
    private static final int APPARITION_SPRITE_HEIGHT = 190;
    private static final int TELEPORT_Y_OFFSET = -40;

    private CherryState state;
    private int teleportTimer;
    private static final int TELEPORT_INTERVAL = 1200;
    private Point nextTeleportPosition;

    private enum CherryState {
        STAYING,
        DISAPPEARING,
        APPEARING
    }

    public CherryFruit(int gridX, int gridY, GridSystem grid, GameMap gameMap) {
        super(gridX, gridY, grid);
        this.gameMap = gameMap;
        this.state = CherryState.STAYING;
        this.teleportTimer = 0;
        this.nextTeleportPosition = null;

        loadSprites();
    }

    private void loadSprites() {
        staySprites = new Image[STAY_FRAMES];
        for (int i = 0; i < STAY_FRAMES; i++) {
            String path = "Images/Fruits/Cherry/Stay/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    staySprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de cereza: " + path);
            }
        }

        apparitionSprites = new Image[TELEPORT_FRAMES];
        for (int i = 0; i < TELEPORT_FRAMES; i++) {
            String path = "Images/Fruits/Cherry/Apparition/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    apparitionSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de aparicion: " + path);
            }
        }

        disappearSprites = new Image[TELEPORT_FRAMES];
        for (int i = 0; i < TELEPORT_FRAMES; i++) {
            int frameNumber = i + 1;
            String path = "Images/Fruits/Cherry/Apparition/" + frameNumber + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    disappearSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de desaparicion: " + path);
            }
        }

        System.out.println("Sprites de cereza cargados correctamente");
    }

    @Override
    public void update() {
        if (collected) {
            return;
        }

        switch (state) {
            case STAYING:
                updateStayAnimation();
                updateTeleportTimer();
                break;

            case DISAPPEARING:
                updateDisappearAnimation();
                break;

            case APPEARING:
                updateAppearAnimation();
                break;
        }
    }

    private void updateStayAnimation() {
        updateAnimation(STAY_ANIMATION_SPEED, STAY_FRAMES);
    }

    private void updateTeleportTimer() {
        teleportTimer++;

        if (teleportTimer >= TELEPORT_INTERVAL) {
            startTeleport();
        }
    }

    private void startTeleport() {
        nextTeleportPosition = findRandomFreePosition();

        if (nextTeleportPosition != null) {
            state = CherryState.DISAPPEARING;
            animationFrame = 0;
            animationCounter = 0;
            System.out.println("Cereza iniciando teletransporte desde " + gridPosition + " a " + nextTeleportPosition);
        } else {
            teleportTimer = 0;
            System.out.println("No se encontro posicion libre para teletransporte");
        }
    }

    private void updateDisappearAnimation() {
        animationCounter++;
        if (animationCounter >= TELEPORT_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= TELEPORT_FRAMES) {
                completeTeleport();
            }
        }
    }

    private void completeTeleport() {
        if (nextTeleportPosition != null) {
            gridPosition = nextTeleportPosition;
            Point pixelPos = grid.gridToPixel(gridPosition.x, gridPosition.y);
            x = pixelPos.x;
            y = pixelPos.y;
        }

        state = CherryState.APPEARING;
        animationFrame = 0;
        animationCounter = 0;
        teleportTimer = 0;
        nextTeleportPosition = null;

        System.out.println("Cereza teletransportada a " + gridPosition);
    }

    private void updateAppearAnimation() {
        animationCounter++;
        if (animationCounter >= TELEPORT_ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= TELEPORT_FRAMES) {
                state = CherryState.STAYING;
                animationFrame = 0;
                animationCounter = 0;
            }
        }
    }

    private Image getCurrentApparitionSprite() {
        int reversedIndex = TELEPORT_FRAMES - 1 - animationFrame;
        if (apparitionSprites != null && reversedIndex >= 0 &&
                reversedIndex < TELEPORT_FRAMES &&
                apparitionSprites[reversedIndex] != null) {
            return apparitionSprites[reversedIndex];
        }
        return null;
    }

    private Point findRandomFreePosition() {
        List<Point> freePositions = new ArrayList<Point>();

        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();

        for (int x = 2; x < gridWidth - 2; x++) {
            for (int y = 2; y < gridHeight - 2; y++) {
                if (isPositionFree(x, y)) {
                    freePositions.add(new Point(x, y));
                }
            }
        }

        if (freePositions.isEmpty()) {
            return null;
        }

        int randomIndex = (int)(Math.random() * freePositions.size());
        return freePositions.get(randomIndex);
    }

    private boolean isPositionFree(int gridX, int gridY) {
        if (gameMap.hasBlockAtGrid(gridX, gridY)) {
            return false;
        }

        if (gameMap.hasHotTileAt(gridX, gridY)) {
            return false;
        }

        if (gameMap.hasCampfireAt(gridX, gridY)) {
            return false;
        }

        return true;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        if (state == CherryState.DISAPPEARING || state == CherryState.APPEARING) {
            return y + TELEPORT_Y_OFFSET;
        }
        return y;
    }

    @Override
    public int getWidth() {
        switch (state) {
            case STAYING:
                return STAY_SPRITE_WIDTH;
            case DISAPPEARING:
            case APPEARING:
                return APPARITION_SPRITE_WIDTH;
            default:
                return STAY_SPRITE_WIDTH;
        }
    }

    @Override
    public int getHeight() {
        switch (state) {
            case STAYING:
                return STAY_SPRITE_HEIGHT;
            case DISAPPEARING:
            case APPEARING:
                return APPARITION_SPRITE_HEIGHT;
            default:
                return STAY_SPRITE_HEIGHT;
        }
    }

    @Override
    public Image getCurrentSprite() {
        switch (state) {
            case STAYING:
                if (staySprites != null && staySprites.length > 0 &&
                        staySprites[animationFrame] != null) {
                    return staySprites[animationFrame];
                }
                break;

            case DISAPPEARING:
                if (disappearSprites != null && animationFrame < TELEPORT_FRAMES &&
                        disappearSprites[animationFrame] != null) {
                    return disappearSprites[animationFrame];
                }
                break;

            case APPEARING:
                return getCurrentApparitionSprite();
        }
        return null;
    }

    @Override
    public FruitType getType() {
        return FruitType.CHERRY;
    }

    @Override
    public int getPoints() {
        return POINTS;
    }
}