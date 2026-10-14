package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;

/**
 * Muro de esquina que ocupa 2x2 celdas
 */
public class CornerWall implements Wall {
    private final int x, y;
    private final Point gridPosition;
    private final Image sprite;
    private final int cellSize;

    public CornerWall(int gridX, int gridY, GridSystem grid, String imagePath) {
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
                System.out.println("Advertencia: No se pudo cargar muro de esquina: " + path);
            }
        } catch (Exception e) {
            System.out.println("Error cargando muro de esquina: " + path);
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
        return cellSize * 2;
    }

    @Override
    public int getHeight() {
        return cellSize * 2;
    }

    @Override
    public Point getGridPosition() {
        return gridPosition;
    }

    @Override
    public int getGridWidth() {
        return 2;
    }

    @Override
    public int getGridHeight() {
        return 2;
    }

    @Override
    public boolean occupiesCell(int gridX, int gridY) {
        return gridX >= gridPosition.x && gridX < gridPosition.x + 2 &&
                gridY >= gridPosition.y && gridY < gridPosition.y + 2;
    }

    @Override
    public Image getSprite() {
        return sprite;
    }

    @Override
    public WallType getType() {
        return WallType.CORNER;
    }
}