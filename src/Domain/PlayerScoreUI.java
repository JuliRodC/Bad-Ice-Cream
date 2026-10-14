package Domain;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;

public class PlayerScoreUI {
    private Image playerIcon;
    private String playerName;
    private String playerFlavor;
    private Player player;
    private boolean isPlayer2;

    private static final int ICON_SIZE = 40;

    public PlayerScoreUI(String playerName, String playerFlavor, Player player, boolean isPlayer2) {
        this.playerName = playerName;
        this.playerFlavor = getFlavorDisplayName(playerFlavor);
        this.player = player;
        this.isPlayer2 = isPlayer2;

        loadIcon(isPlayer2);
    }

    private void loadIcon(boolean isPlayer2) {
        try {
            String path = isPlayer2 ? "Images/Resources/J2.png" : "Images/Resources/J1.png";
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                playerIcon = icon.getImage();
                System.out.println("Icono " + (isPlayer2 ? "J2" : "J1") + " cargado correctamente");
            }
        } catch (Exception e) {
            System.out.println("Error cargando icono: " + e.getMessage());
        }
    }

    private String getFlavorDisplayName(String flavor) {
        if (flavor == null || flavor.isEmpty()) return "Vainilla";

        switch (flavor.toLowerCase()) {
            case "vanilla":
                return "Vainilla";
            case "chocolate":
                return "Chocolate";
            case "strawberry":
                return "Fresa";
            default:
                return "Vainilla";
        }
    }

    public void draw(Graphics g, FruitManager fruitManager, int baseX, int baseY) {
        // Dibujar icono
        if (playerIcon != null) {
            g.drawImage(playerIcon, baseX, baseY, ICON_SIZE, ICON_SIZE, null);
        }

        // Obtener puntaje del jugador
        int score = fruitManager.getScore(player);

        // Posiciones del texto (al lado derecho del icono)
        int textX = baseX + ICON_SIZE + 8;
        int scoreY = baseY + 18;
        int nameY = baseY + 32;
        int flavorY = baseY + 46;

        // Dibujar puntaje (formato 000000)
        Font scoreFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(scoreFont);
        String scoreText = String.valueOf(score);

        // Sombra negra
        g.setColor(Color.BLACK);
        g.drawString(scoreText, textX + 1, scoreY + 1);

        // Texto amarillo
        g.setColor(Color.YELLOW);
        g.drawString(scoreText, textX, scoreY);

        // Dibujar nombre debajo del puntaje
        Font nameFont = new Font("Arial", Font.BOLD, 13);
        g.setFont(nameFont);

        g.setColor(Color.BLACK);
        g.drawString(playerName, textX + 1, nameY + 1);

        // Color segun jugador
        g.setColor(isPlayer2 ? new Color(255, 105, 180) : Color.YELLOW);
        g.drawString(playerName, textX, nameY);

        // Dibujar sabor debajo del nombre
        Font flavorFont = new Font("Arial", Font.PLAIN, 11);
        g.setFont(flavorFont);

        g.setColor(Color.BLACK);
        g.drawString(playerFlavor, textX + 1, flavorY + 1);

        // Color celeste para el sabor
        g.setColor(new Color(135, 206, 250));
        g.drawString(playerFlavor, textX, flavorY);
    }

    public Player getPlayer() {
        return player;
    }
}