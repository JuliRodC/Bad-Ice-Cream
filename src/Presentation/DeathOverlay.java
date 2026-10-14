package Presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Overlay que muestra la pantalla de muerte sobre el juego
 * Incluye botones para reiniciar o volver al menu y muestra el puntaje final
 */
public class DeathOverlay {
    private Image deathScreenImage;
    private boolean visible;
    private int finalScore;

    // Botones con areas clickeables
    private Rectangle restartButton;
    private Rectangle backToMenuButton;

    // Dimensiones de la pantalla
    private final int screenWidth;
    private final int screenHeight;

    // Posicion y tamano de la imagen de muerte
    private int imageX;
    private int imageY;
    private int imageWidth;
    private int imageHeight;

    public DeathOverlay(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.visible = false;
        this.finalScore = 0;

        loadDeathScreen();
        setupButtons();
    }

    private void loadDeathScreen() {
        try {
            String path = "Images/PantallaMuerte.png";
            ImageIcon icon = new ImageIcon(path);

            if (icon.getIconWidth() > 0) {
                deathScreenImage = icon.getImage();

                // Dimensiones de la imagen
                imageWidth = 480;
                imageHeight = 300;
                imageX = (screenWidth - imageWidth) / 2;
                imageY = (screenHeight - imageHeight) / 2;

                System.out.println("Pantalla de muerte cargada desde: " + path);
            } else {
                System.out.println("Error: No se pudo cargar la imagen de muerte");
            }
        } catch (Exception e) {
            System.out.println("Error cargando pantalla de muerte: " + e.getMessage());
        }
    }

    private void setupButtons() {
        // Boton RESTART
        int restartX = 310;
        int restartY = 400;
        int restartWidth = 140;
        int restartHeight = 20;
        restartButton = new Rectangle(restartX, restartY, restartWidth, restartHeight);

        // Boton BACK TO MENU
        int backX = 262;
        int backY = 455;
        int backWidth = 232;
        int backHeight = 20;
        backToMenuButton = new Rectangle(backX, backY, backWidth, backHeight);
    }

    /**
     * Muestra el overlay de muerte con el puntaje final
     */
    public void show(int score) {
        this.finalScore = score;
        this.visible = true;
        System.out.println("Mostrando pantalla de muerte con puntaje: " + score);
    }

    /**
     * Oculta el overlay de muerte
     */
    public void hide() {
        visible = false;
        finalScore = 0;
    }

    /**
     * Verifica si el overlay esta visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Actualiza el estado de hover basado en la posicion del mouse
     * @return true si cambio el estado de hover
     */
    public boolean updateHover(Point mousePos) {
        if (!visible) return false;
        return false;
    }

    /**
     * Maneja el click del mouse
     * @return "restart" si se presiono restart, "menu" si se presiono back to menu, null si no se presiono nada
     */
    public String handleClick(Point clickPos) {
        if (!visible) return null;

        if (restartButton.contains(clickPos)) {
            System.out.println("Boton RESTART presionado");
            return "restart";
        }

        if (backToMenuButton.contains(clickPos)) {
            System.out.println("Boton BACK TO MENU presionado");
            return "menu";
        }

        return null;
    }

    /**
     * Dibuja el overlay en la pantalla
     */
    public void draw(Graphics g) {
        if (!visible) return;

        // Fondo semi-transparente oscuro
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Imagen de muerte centrada
        if (deathScreenImage != null) {
            g.drawImage(deathScreenImage, imageX, imageY, imageWidth, imageHeight, null);
        }

        // Dibujar el puntaje final
        drawScore(g);
    }


    /**
     * Dibuja el puntaje final en la pantalla de muerte
     */
    private void drawScore(Graphics g) {
        Font scoreFont = new Font("Arial", Font.BOLD, 26);
        g.setFont(scoreFont);

        String scoreText = "" + finalScore;

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);

        int scoreX = screenWidth / 2 - textWidth / 2 + 50;
        int scoreY = screenHeight / 2 - 5;

        Color outline = new Color(45, 32, 2);

        // Borde más grueso (2 píxeles)
        g.setColor(outline);
        int o = 2; // <<--- aumenta este valor para hacerlo más grueso

        // Capa 1 (1 píxel)
        g.drawString(scoreText, scoreX - 1, scoreY);
        g.drawString(scoreText, scoreX + 1, scoreY);
        g.drawString(scoreText, scoreX, scoreY - 1);
        g.drawString(scoreText, scoreX, scoreY + 1);

        // Capa 2 (2 píxeles)
        g.drawString(scoreText, scoreX - o, scoreY);
        g.drawString(scoreText, scoreX + o, scoreY);
        g.drawString(scoreText, scoreX, scoreY - o);
        g.drawString(scoreText, scoreX, scoreY + o);

        // Relleno claro
        g.setColor(new Color(255, 254, 252));
        g.drawString(scoreText, scoreX, scoreY);
    }




    /**
     * Obtiene el cursor apropiado segun la posicion del mouse
     */
    public Cursor getCursor(Point mousePos) {
        if (!visible) return Cursor.getDefaultCursor();

        if (restartButton.contains(mousePos) || backToMenuButton.contains(mousePos)) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }

        return Cursor.getDefaultCursor();
    }
}