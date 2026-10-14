package Domain;

import java.awt.Point;
import java.util.*;

/**
 * Sistema de busqueda de caminos para la IA
 * Usa BFS (Breadth-First Search) para encontrar el camino mas corto
 */
public class PathFinder {
    GridSystem grid;
    CollisionDetector collisionDetector;
    private GameMap gameMap;

    public PathFinder(GridSystem grid, CollisionDetector collisionDetector, GameMap gameMap) {
        this.grid = grid;
        this.collisionDetector = collisionDetector;
        this.gameMap = gameMap;
    }

    /**
     * Encuentra el camino mas corto desde una posicion inicial hacia un objetivo
     * @param startX posicion inicial X en grid
     * @param startY posicion inicial Y en grid
     * @param targetX posicion objetivo X en grid
     * @param targetY posicion objetivo Y en grid
     * @return lista de direcciones para seguir el camino, o null si no hay camino
     */
    public List<Player.Direction> findPath(int startX, int startY, int targetX, int targetY) {
        if (startX == targetX && startY == targetY) {
            return new ArrayList<>();
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Node> parentMap = new HashMap<>();

        Point start = new Point(startX, startY);
        Point target = new Point(targetX, targetY);

        queue.offer(new Node(start, null, null));
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            Point currentPos = current.position;

            if (currentPos.equals(target)) {
                return reconstructPath(current);
            }

            for (Player.Direction direction : getPossibleDirections()) {
                Point nextPos = grid.getNextGridPosition(currentPos.x, currentPos.y, direction);

                if (!visited.contains(nextPos) &&
                        grid.isInBounds(nextPos.x, nextPos.y) &&
                        !collisionDetector.isBlocked(nextPos.x, nextPos.y)) {

                    Node nextNode = new Node(nextPos, current, direction);
                    queue.offer(nextNode);
                    visited.add(nextPos);
                }
            }
        }

        return null;
    }

    /**
     * Encuentra la fruta mas cercana desde una posicion
     * @param startX posicion inicial X en grid
     * @param startY posicion inicial Y en grid
     * @param fruitManager gestor de frutas
     * @return punto con la posicion de la fruta mas cercana, o null si no hay frutas
     */
    public Point findNearestFruit(int startX, int startY, FruitManager fruitManager) {
        Point nearestFruit = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Fruit fruit : fruitManager.getFruits()) {
            if (fruit.isCollected()) {
                continue;
            }

            Point fruitPos = fruit.getGridPosition();
            int distance = manhattanDistance(startX, startY, fruitPos.x, fruitPos.y);

            if (distance < shortestDistance) {
                List<Player.Direction> path = findPath(startX, startY, fruitPos.x, fruitPos.y);
                if (path != null) {
                    shortestDistance = distance;
                    nearestFruit = fruitPos;
                }
            }
        }

        return nearestFruit;
    }

    /**
     * Calcula la distancia Manhattan entre dos puntos
     */
    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Reconstruye el camino desde el nodo objetivo hacia el inicio
     */
    private List<Player.Direction> reconstructPath(Node target) {
        List<Player.Direction> path = new ArrayList<>();
        Node current = target;

        while (current.parent != null) {
            path.add(0, current.direction);
            current = current.parent;
        }

        return path;
    }

    /**
     * Obtiene las direcciones posibles de movimiento
     */
    private Player.Direction[] getPossibleDirections() {
        return new Player.Direction[]{
                Player.Direction.UP,
                Player.Direction.DOWN,
                Player.Direction.LEFT,
                Player.Direction.RIGHT
        };
    }

    /**
     * Clase interna para representar un nodo en el camino
     */
    private static class Node {
        Point position;
        Node parent;
        Player.Direction direction;

        Node(Point position, Node parent, Player.Direction direction) {
            this.position = position;
            this.parent = parent;
            this.direction = direction;
        }
    }
}