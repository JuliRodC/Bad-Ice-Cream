package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Baldosa caliente que derrite el hielo inmediatamente
 * Si se crea hielo sobre esta baldosa, se derrite al instante
 */
public class HotTile implements Tile {
    private final int x, y;
    private final Point gridPosition;
    private final int cellSize;
    private Image[] sprites;
    private int animationFrame;
    private int animationCounter;
    private static final int ANIMATION_SPEED = 8;
    private static final int TOTAL_FRAMES = 4;

    public HotTile(int gridX, int gridY, GridSystem grid) {
        this.gridPosition = new Point(gridX, gridY);
        this.cellSize = grid.getCellSize();

        Point pixelPos = grid.gridToPixel(gridX, gridY);
        this.x = pixelPos.x;
        this.y = pixelPos.y;

        this.animationFrame = 0;
        this.animationCounter = 0;

        loadSprites();
    }

    private void loadSprites() {
        sprites = new Image[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            String path = "Images/Resources/HotBlock/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    sprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de baldosa caliente: " + path);
            }
        }

        System.out.println("Sprites de baldosa caliente cargados: " + TOTAL_FRAMES + " frames");
    }

    @Override
    public void update() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;

            if (animationFrame >= TOTAL_FRAMES) {
                animationFrame = 0;
            }
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
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
    public Point getGridPosition() {
        return gridPosition;
    }

    @Override
    public Image getSprite() {
        if (sprites != null && sprites.length > 0 && sprites[animationFrame] != null) {
            return sprites[animationFrame];
        }
        return null;
    }

    @Override
    public TileType getType() {
        return TileType.HOT_TILE;
    }
}