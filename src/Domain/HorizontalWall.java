package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Muro horizontal que ocupa 1 celda
 * Se usa en los bordes superior e inferior
 */
public class HorizontalWall implements Wall {
    private final int x, y;
    private final Point gridPosition;
    private final Image sprite;
    private final int cellSize;

    public HorizontalWall(int gridX, int gridY, GridSystem grid, String imagePath) {
        this.gridPosition = new Point(gridX, gridY);
        this.cellSize = grid.getCellSize();

        Point pixelPos = grid.gridToPixel(gridX, gridY);
        this.x = pixelPos.x;
        this.y = pixelPos.y;

        this.sprite = loadSprite(imagePath);
    }

    private Image loadSprite(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                return icon.getImage();
            } else {
                System.out.println("Advertencia: No se pudo cargar muro horizontal: " + path);
            }
        } catch (Exception e) {
            System.out.println("Error cargando muro horizontal: " + path);
        }
        return null;
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
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public boolean occupiesCell(int gridX, int gridY) {
        return gridX == gridPosition.x && gridY == gridPosition.y;
    }

    @Override
    public Image getSprite() {
        return sprite;
    }

    @Override
    public WallType getType() {
        return WallType.HORIZONTAL;
    }
}