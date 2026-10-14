package Domain;

import java.awt.Point;

/**
 * Interfaz para detectar colisiones en el juego.
 * Permite verificar si un movimiento es valido.
 */
public interface CollisionDetector {
    /**
     * Verifica si una posicion en la cuadricula esta bloqueada
     * @param gridX coordenada X en la cuadricula
     * @param gridY coordenada Y en la cuadricula
     * @return true si hay un obstaculo solido en esa posicion
     */
    boolean isBlocked(int gridX, int gridY);

    /**
     * Verifica si un movimiento desde una posicion es valido
     * @param fromGridX posicion actual X en la cuadricula
     * @param fromGridY posicion actual Y en la cuadricula
     * @param direction direccion del movimiento
     * @return true si el movimiento es valido
     */
    boolean canMove(int fromGridX, int fromGridY, Player.Direction direction);

    /**
     * Obtiene la siguiente posicion valida dado un movimiento
     * Si el movimiento esta bloqueado, retorna la posicion actual
     * @param currentGridX posicion actual X
     * @param currentGridY posicion actual Y
     * @param direction direccion del movimiento
     * @return punto con la posicion resultante
     */
    Point getValidNextPosition(int currentGridX, int currentGridY, Player.Direction direction);
}