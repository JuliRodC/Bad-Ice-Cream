package Domain;

import java.awt.Point;

/**
 * Interfaz que define el comportamiento del sistema de cuadricula del juego.
 * Permite alinear elementos a una grid invisible.
 */
public interface GridSystem {
    /**
     * Convierte coordenadas de pixeles a coordenadas de cuadricula
     */
    Point pixelToGrid(int pixelX, int pixelY);

    /**
     * Convierte coordenadas de cuadricula a coordenadas de pixeles
     */
    Point gridToPixel(int gridX, int gridY);

    /**
     * Alinea una posicion en pixeles a la cuadricula mas cercana
     */
    Point snapToGrid(int pixelX, int pixelY);

    /**
     * Verifica si una posicion en cuadricula esta dentro de los limites
     */
    boolean isInBounds(int gridX, int gridY);

    /**
     * Obtiene el tamano de cada celda de la cuadricula
     */
    int getCellSize();

    /**
     * Obtiene las dimensiones de la cuadricula
     */
    int getGridWidth();
    int getGridHeight();

    /**
     * Calcula la siguiente posicion en la cuadricula dada una direccion
     */
    Point getNextGridPosition(int currentGridX, int currentGridY, Player.Direction direction);
}