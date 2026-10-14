package Domain;

import java.awt.Image;
import java.awt.Point;

/**
 * Interfaz para las baldosas especiales del juego
 * Las baldosas son elementos del mapa que afectan el comportamiento de otros elementos
 */
public interface Tile {
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
     * Obtiene la posicion en la cuadricula
     */
    Point getGridPosition();

    /**
     * Obtiene el sprite de la baldosa
     */
    Image getSprite();

    /**
     * Tipo de baldosa
     */
    TileType getType();

    /**
     * Actualiza la baldosa (para animaciones)
     */
    void update();

    enum TileType {
        HOT_TILE
    }
}