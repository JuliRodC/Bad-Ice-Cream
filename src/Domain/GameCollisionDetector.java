package Domain;

import java.awt.Point;

/**
 * Implementacion concreta del detector de colisiones.
 * Verifica colisiones contra el mapa y los bloques.
 */
public class GameCollisionDetector implements CollisionDetector {
    private final GameMap gameMap;
    private final GridSystem grid;

    public GameCollisionDetector(GameMap gameMap, GridSystem grid) {
        this.gameMap = gameMap;
        this.grid = grid;
    }

    @Override
    public boolean isBlocked(int gridX, int gridY) {
        // Verificar limites del mapa
        if (!grid.isInBounds(gridX, gridY)) {
            return true;
        }

        // Verificar si hay un bloque solido en esta posicion
        return gameMap.hasBlockAtGrid(gridX, gridY);
    }

    @Override
    public boolean canMove(int fromGridX, int fromGridY, Player.Direction direction) {
        Point nextPos = grid.getNextGridPosition(fromGridX, fromGridY, direction);
        return !isBlocked(nextPos.x, nextPos.y);
    }

    @Override
    public Point getValidNextPosition(int currentGridX, int currentGridY, Player.Direction direction) {
        Point nextPos = grid.getNextGridPosition(currentGridX, currentGridY, direction);

        // Si la siguiente posicion esta bloqueada, retornar la actual
        if (isBlocked(nextPos.x, nextPos.y)) {
            return new Point(currentGridX, currentGridY);
        }

        return nextPos;
    }
}