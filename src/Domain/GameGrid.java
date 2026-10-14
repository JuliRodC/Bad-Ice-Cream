package Domain;

import java.awt.Point;

/**
 * Implementacion concreta del sistema de cuadricula del juego.
 * Maneja la conversion entre coordenadas de pixeles y coordenadas de grid.
 */
public class GameGrid implements GridSystem {
    private final int mapWidth;
    private final int mapHeight;
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;

    public GameGrid(int mapWidth, int mapHeight, int cellSize) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.cellSize = cellSize;
        this.gridWidth = mapWidth / cellSize;
        this.gridHeight = mapHeight / cellSize;
    }

    @Override
    public Point pixelToGrid(int pixelX, int pixelY) {
        int gridX = pixelX / cellSize;
        int gridY = pixelY / cellSize;
        return new Point(gridX, gridY);
    }

    @Override
    public Point gridToPixel(int gridX, int gridY) {
        int pixelX = gridX * cellSize;
        int pixelY = gridY * cellSize;
        return new Point(pixelX, pixelY);
    }

    @Override
    public Point snapToGrid(int pixelX, int pixelY) {
        // Convertir a grid y luego de vuelta a pixeles para alinear
        Point gridPos = pixelToGrid(pixelX, pixelY);
        return gridToPixel(gridPos.x, gridPos.y);
    }

    @Override
    public boolean isInBounds(int gridX, int gridY) {
        return gridX >= 0 && gridX < gridWidth &&
                gridY >= 0 && gridY < gridHeight;
    }

    @Override
    public int getCellSize() {
        return cellSize;
    }

    @Override
    public int getGridWidth() {
        return gridWidth;
    }

    @Override
    public int getGridHeight() {
        return gridHeight;
    }

    @Override
    public Point getNextGridPosition(int currentGridX, int currentGridY, Player.Direction direction) {
        int nextX = currentGridX;
        int nextY = currentGridY;

        switch (direction) {
            case UP:
                nextY--;
                break;
            case DOWN:
                nextY++;
                break;
            case LEFT:
                nextX--;
                break;
            case RIGHT:
                nextX++;
                break;
            case IDLE_DOWN:
                // No se mueve en idle
                break;
        }

        return new Point(nextX, nextY);
    }
}