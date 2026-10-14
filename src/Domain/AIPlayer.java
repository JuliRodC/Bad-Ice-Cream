package Domain;

import java.awt.Point;
import java.util.List;

/**
 * Clase abstracta para jugadores controlados por inteligencia artificial
 * Extiende IceCreamPlayer y agrega funcionalidad especifica para IA
 */

public abstract class AIPlayer extends IceCreamPlayer {
    protected PathFinder pathFinder;
    protected List<Player.Direction> currentPath;
    protected int pathUpdateCounter;
    protected Point targetPosition;

    public AIPlayer(int startX, int startY, String spriteBasePath,
                    GridSystem grid, PathFinder pathFinder) {
        super(startX, startY, spriteBasePath);
        this.pathFinder = pathFinder;
        this.currentPath = null;
        this.pathUpdateCounter = 0;
    }

    // Metodo abstracto que cada IA implementa diferente
    public abstract void updateAI(FruitManager fruitManager, EnemyManager enemyManager);

    public abstract AIType getAIType();

    // Implementacion compartida
    public void resetAIState() {
        currentPath = null;
        pathUpdateCounter = 0;
        stopMoving();
    }

    protected boolean hasReachedPosition(Point targetPos) {
        Point currentPos = pathFinder.grid.pixelToGrid(getX(), getY());
        return currentPos.equals(targetPos);
    }

    protected int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public enum AIType {
        HUNGRY, FEARFUL, EXPERT
    }
}