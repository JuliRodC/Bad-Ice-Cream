package Presentation;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.Color;

/**
 * Overlay de pausa que muestra la pantalla de pausa con botones interactivos
 * Continue, Back to Menu y Save
 */
public class PauseOverlay {
    private Image pauseImage;
    private boolean visible;

    // Botones
    private Rectangle continueButton;
    private Rectangle backButton;
    private Rectangle saveButton;

    // Estados de hover
    private boolean continueHovered;
    private boolean backHovered;
    private boolean saveHovered;

    private int screenWidth;
    private int screenHeight;

    // Posiciones de los botones (ajustar segun la imagen)
    private static final int CONTINUE_X = 305;
    private static final int CONTINUE_Y = 335;
    private static final int CONTINUE_WIDTH = 160;
    private static final int CONTINUE_HEIGHT = 25;

    private static final int BACK_X = 268;
    private static final int BACK_Y = 365;
    private static final int BACK_WIDTH = 240;
    private static final int BACK_HEIGHT = 22;

    private static final int SAVE_X = 310;
    private static final int SAVE_Y = 395;
    private static final int SAVE_WIDTH = 150;
    private static final int SAVE_HEIGHT = 25;

    public PauseOverlay(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.visible = false;
        this.continueHovered = false;
        this.backHovered = false;
        this.saveHovered = false;

        loadImage();
        initializeButtons();
    }

    private void loadImage() {
        try {
            String path = "Images/PantallaPausa.png";
            pauseImage = new ImageIcon(path).getImage();
            if (pauseImage.getWidth(null) > 0) {
                System.out.println("Imagen de pausa cargada desde: " + path);
            } else {
                System.out.println("Advertencia: No se pudo cargar la imagen de pausa");
            }
        } catch (Exception e) {
            System.out.println("Error cargando imagen de pausa: " + e.getMessage());
        }
    }

    private void initializeButtons() {
        continueButton = new Rectangle(CONTINUE_X, CONTINUE_Y, CONTINUE_WIDTH, CONTINUE_HEIGHT);
        backButton = new Rectangle(BACK_X, BACK_Y, BACK_WIDTH, BACK_HEIGHT);
        saveButton = new Rectangle(SAVE_X, SAVE_Y, SAVE_WIDTH, SAVE_HEIGHT);
    }

    /**
     * Muestra el overlay de pausa
     */
    public void show() {
        visible = true;
        continueHovered = false;
        backHovered = false;
        saveHovered = false;
        System.out.println("Mostrando overlay de pausa");
    }

    /**
     * Oculta el overlay de pausa
     */
    public void hide() {
        visible = false;
        System.out.println("Ocultando overlay de pausa");
    }

    /**
     * Verifica si el overlay esta visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Actualiza el estado de hover de los botones segun la posicion del mouse
     * @param mousePos posicion del mouse
     * @return true si cambio algun estado de hover
     */
    public boolean updateHover(Point mousePos) {
        boolean wasContinueHovered = continueHovered;
        boolean wasBackHovered = backHovered;
        boolean wasSaveHovered = saveHovered;

        continueHovered = continueButton.contains(mousePos);
        backHovered = backButton.contains(mousePos);
        saveHovered = saveButton.contains(mousePos);

        return wasContinueHovered != continueHovered ||
                wasBackHovered != backHovered ||
                wasSaveHovered != saveHovered;
    }

    /**
     * Maneja el clic del mouse en los botones
     * @param clickPos posicion del clic
     * @return "continue", "back", "save" o null si no se hizo clic en ningun boton
     */
    public String handleClick(Point clickPos) {
        if (!visible) {
            return null;
        }

        if (continueButton.contains(clickPos)) {
            System.out.println("Boton Continue presionado");
            return "continue";
        }

        if (backButton.contains(clickPos)) {
            System.out.println("Boton Back to Menu presionado");
            return "back";
        }

        if (saveButton.contains(clickPos)) {
            System.out.println("Boton Save presionado");
            return "save";
        }

        return null;
    }

    /**
     * Obtiene el cursor apropiado segun la posicion del mouse
     */
    public Cursor getCursor(Point mousePos) {
        if (!visible) {
            return Cursor.getDefaultCursor();
        }

        if (continueButton.contains(mousePos) ||
                backButton.contains(mousePos) ||
                saveButton.contains(mousePos)) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }

        return Cursor.getDefaultCursor();
    }

    /**
     * Dibuja el overlay de pausa
     */
    public void draw(Graphics g) {
        if (!visible) {
            return;
        }

        // Dibujar fondo negro semi-transparente
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Dibujar la imagen de pausa centrada
        if (pauseImage != null) {
            int imageWidth = 400;
            int imageHeight = 200;

            int x = (screenWidth - imageWidth) / 2;
            int y = (screenHeight - imageHeight) / 2;

            g.drawImage(pauseImage, x, y, imageWidth, imageHeight, null);
        }
    }
}