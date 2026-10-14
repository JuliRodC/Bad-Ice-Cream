package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Point;

/**
 * Boton de pausa del juego
 * Maneja su posicion, imagenes, estado de hover y estado de pausa
 */
public class PauseButton {
    private Rectangle bounds;
    private Image normalImage;
    private Image hoverImage;
    private boolean hovered;
    private boolean paused;

    private static final int BUTTON_SIZE = 21;
    private static final int BUTTON_MARGIN = 30;

    public PauseButton(int screenWidth, int screenHeight) {
        this.hovered = false;
        this.paused = false;

        // Posicionar en esquina superior derecha
        int x = 650;
        int y = BUTTON_MARGIN;
        this.bounds = new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE);

        loadImages();
    }

    private void loadImages() {
        try {
            String normalPath = "Images/Resources/Pause/PauseNormal.png";
            normalImage = new ImageIcon(normalPath).getImage();
            if (normalImage.getWidth(null) > 0) {
                System.out.println("Boton de pausa normal cargado");
            }
        } catch (Exception e) {
            System.out.println("Error cargando boton de pausa normal: " + e.getMessage());
        }

        try {
            String hoverPath = "Images/Resources/Pause/PauseSelec.png";
            hoverImage = new ImageIcon(hoverPath).getImage();
            if (hoverImage.getWidth(null) > 0) {
                System.out.println("Boton de pausa hover cargado");
            }
        } catch (Exception e) {
            System.out.println("Error cargando boton de pausa hover: " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado de hover segun la posicion del mouse
     * @param mousePos posicion del mouse
     * @return true si cambio el estado de hover
     */
    public boolean updateHover(Point mousePos) {
        boolean wasHovered = hovered;
        hovered = bounds.contains(mousePos);
        return wasHovered != hovered;
    }

    /**
     * Maneja el clic del mouse
     * @param clickPos posicion del clic
     * @return true si el clic fue dentro del boton
     */
    public boolean handleClick(Point clickPos) {
        if (bounds.contains(clickPos)) {
            toggle();
            return true;
        }
        return false;
    }

    /**
     * Alterna entre pausado y no pausado
     */
    public void toggle() {
        paused = !paused;
        System.out.println(paused ? "Juego pausado" : "Juego reanudado");
    }

    /**
     * Pausa el juego
     */
    public void pause() {
        if (!paused) {
            paused = true;
            System.out.println("Juego pausado");
        }
    }

    /**
     * Reanuda el juego
     */
    public void resume() {
        if (paused) {
            paused = false;
            System.out.println("Juego reanudado");
        }
    }

    /**
     * Verifica si el juego esta pausado
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Obtiene la imagen actual del boton segun el estado
     */
    public Image getCurrentImage() {
        return hovered ? hoverImage : normalImage;
    }

    /**
     * Obtiene los limites del boton para dibujar
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Obtiene la posicion X del boton
     */
    public int getX() {
        return bounds.x;
    }

    /**
     * Obtiene la posicion Y del boton
     */
    public int getY() {
        return bounds.y;
    }

    /**
     * Obtiene el ancho del boton
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Obtiene el alto del boton
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Verifica si el boton esta siendo hovereado
     */
    public boolean isHovered() {
        return hovered;
    }
}