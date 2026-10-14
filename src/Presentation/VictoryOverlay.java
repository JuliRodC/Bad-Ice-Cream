package Presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Overlay que muestra la pantalla de victoria sobre el juego
 * Incluye botones para continuar (ir a seleccion de nivel) o volver al menu y muestra el puntaje final
 */
public class VictoryOverlay {
    private Image victoryScreenImage;
    private boolean visible;
    private int finalScore;

    // Botones con areas clickeables
    private Rectangle continueButton;
    private Rectangle backToMenuButton;

    // Estado de hover
    private boolean continueHovered;
    private boolean menuHovered;

    // Dimensiones de la pantalla
    private final int screenWidth;
    private final int screenHeight;

    // Posicion y tamano de la imagen de victoria
    private int imageX;
    private int imageY;
    private int imageWidth;
    private int imageHeight;

    public VictoryOverlay(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.visible = false;
        this.finalScore = 0;
        this.continueHovered = false;
        this.menuHovered = false;

        loadVictoryScreen();
        setupButtons();
    }

    private void loadVictoryScreen() {
        try {
            String path = "Images/PantallaGanar.png";
            ImageIcon icon = new ImageIcon(path);

            if (icon.getIconWidth() > 0) {
                victoryScreenImage = icon.getImage();

                // Dimensiones de la imagen
                imageWidth = 480;
                imageHeight = 300;
                imageX = (screenWidth - imageWidth) / 2;
                imageY = (screenHeight - imageHeight) / 2;

                System.out.println("Pantalla de victoria cargada desde: " + path);
            } else {
                System.out.println("Error: No se pudo cargar la imagen de victoria");
            }
        } catch (Exception e) {
            System.out.println("Error cargando pantalla de victoria: " + e.getMessage());
        }
    }

    private void setupButtons() {
        // Boton CONTINUE
        int continueX = 296;
        int continueY = 423;
        int continueWidth = 160;
        int continueHeight = 22;
        continueButton = new Rectangle(continueX, continueY, continueWidth, continueHeight);

        // Boton BACK TO MENU
        int backX = 262;
        int backY = 453;
        int backWidth = 233;
        int backHeight = 22;
        backToMenuButton = new Rectangle(backX, backY, backWidth, backHeight);
    }

    /**
     * Muestra el overlay de victoria con el puntaje final
     */
    public void show(int score) {
        this.finalScore = score;
        this.visible = true;
    }

    /**
     * Oculta el overlay de victoria
     */
    public void hide() {
        visible = false;
        finalScore = 0;
        continueHovered = false;
        menuHovered = false;
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

        boolean wasHovered = continueHovered || menuHovered;
        continueHovered = continueButton.contains(mousePos);
        menuHovered = backToMenuButton.contains(mousePos);
        boolean isHovered = continueHovered || menuHovered;

        return wasHovered != isHovered;
    }

    /**
     * Maneja el click del mouse
     * @return "levelSelection" si se presiono continue, "menu" si se presiono back to menu, null si no se presiono nada
     */
    public String handleClick(Point clickPos) {
        if (!visible) return null;

        if (continueButton.contains(clickPos)) {
            System.out.println("Boton CONTINUE presionado - ir a seleccion de nivel");
            return "levelSelection";
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

        // Imagen de victoria centrada
        if (victoryScreenImage != null) {
            g.drawImage(victoryScreenImage, imageX, imageY, imageWidth, imageHeight, null);
        }

        // Dibujar el puntaje final
        drawScore(g);

        // Dibujar botones
        drawButtons(g);
    }

    /**
     * Dibuja el puntaje final en la pantalla de victoria
     */
    private void drawScore(Graphics g) {
        Font scoreFont = new Font("Arial", Font.BOLD, 26);
        g.setFont(scoreFont);

        String scoreText = "" + finalScore;

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);

        int scoreX = screenWidth / 2 - textWidth / 2 + 50;
        int scoreY = screenHeight / 2 + 22;

        Color outline = new Color(45, 32, 2);

        // Borde mas grueso (2 pixeles)
        g.setColor(outline);
        int o = 2;

        // Capa 1 (1 pixel)
        g.drawString(scoreText, scoreX - 1, scoreY);
        g.drawString(scoreText, scoreX + 1, scoreY);
        g.drawString(scoreText, scoreX, scoreY - 1);
        g.drawString(scoreText, scoreX, scoreY + 1);

        // Capa 2 (2 pixeles)
        g.drawString(scoreText, scoreX - o, scoreY);
        g.drawString(scoreText, scoreX + o, scoreY);
        g.drawString(scoreText, scoreX, scoreY - o);
        g.drawString(scoreText, scoreX, scoreY + o);

        // Relleno claro
        g.setColor(new Color(255, 254, 252));
        g.drawString(scoreText, scoreX, scoreY);
    }

    /**
     * Dibuja los botones solo con transparencia en hover
     */
    private void drawButtons(Graphics g) {
        // Boton CONTINUE (solo transparencia blanca en hover)
        if (continueHovered) {
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        }

        // Boton BACK TO MENU (solo transparencia blanca en hover)
        if (menuHovered) {
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRect(backToMenuButton.x, backToMenuButton.y, backToMenuButton.width, backToMenuButton.height);
        }
    }

    /**
     * Obtiene el cursor apropiado segun la posicion del mouse
     */
    public Cursor getCursor(Point mousePos) {
        if (!visible) return Cursor.getDefaultCursor();

        if (continueButton.contains(mousePos) || backToMenuButton.contains(mousePos)) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }

        return Cursor.getDefaultCursor();
    }
}