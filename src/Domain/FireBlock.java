package Domain;

import java.awt.Point;

/**
 * Clase abstracta para bloques de fuego (fogatas)
 * Extiende Block para mantener el polimorfismo con otros bloques
 */
public abstract class FireBlock extends Block {

    public FireBlock(int x, int y) {
        super(x, y);
    }

    /**
     * Verifica si la fogata esta encendida
     */
    public abstract boolean isLit();

    /**
     * Apaga la fogata temporalmente
     */
    public abstract void extinguish();

    /**
     * Verifica si la fogata esta apagada temporalmente
     */
    public abstract boolean isExtinguished();

    /**
     * Verifica si hay hielo sobre la fogata
     */
    public abstract boolean hasIceAbove();

    /**
     * Establece si hay hielo sobre la fogata
     */
    public abstract void setHasIceAbove(boolean hasIce);

    /**
     * Obtiene el tiempo restante para que se reencienda
     */
    public abstract int getRemainingTimeToRelight();

    /**
     * Obtiene la posicion en la cuadricula
     */
    public abstract Point getGridPosition();
}