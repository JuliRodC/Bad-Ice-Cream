package Domain;

import java.awt.Image;
import java.awt.Point;

/**
 * Interfaz para los muros del juego
 * Los muros son obstaculos estaticos que bloquean el movimiento
 */
public interface Wall {
    /**
     * Obtiene la posicion en pixeles
     */
    int getX();
    int getY();

    /**
     * Obtiene las dimensiones en pixeles
     */
    int getWidth();
    int getHeight();

    /**
     * Obtiene la posicion inicial en la cuadricula
     */
    Point getGridPosition();

    /**
     * Obtiene cuantas celdas ocupa el muro
     */
    int getGridWidth();
    int getGridHeight();

    /**
     * Verifica si el muro ocupa una celda especifica
     */
    boolean occupiesCell(int gridX, int gridY);

    /**
     * Obtiene el sprite del muro
     */
    Image getSprite();

    /**
     * Tipo de muro
     */
    WallType getType();

    enum WallType {
        CORNER, HORIZONTAL, VERTICAL
    }
}