package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de seleccion de modo para 2 jugadores
 * Muestra la imagen TJuego2Jugadores.png con opciones:
 * J1 VS J2, J1 VS IA, IA VS IA
 */
public class TwoPlayerModeScreen implements Screen {
    private JPanel panel;
    private Image backgroundImage;
    private final ScreenManager screenManager;
    private String selectedMode = "";

    public TwoPlayerModeScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        initializePanel();
    }

    private void initializePanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };

        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(680, 680));
        panel.setBackground(Color.WHITE);

        loadImage();
        createButtons();
    }

    private void loadImage() {
        try {
            String path = "Images/TJuego2Jugadores.png";
            backgroundImage = new ImageIcon(path).getImage();

            if (backgroundImage.getWidth(null) > 0) {
                System.out.println("Imagen de modos 2 jugadores cargada desde: " + path);
            } else {
                System.out.println("No se pudo cargar la imagen de modos 2 jugadores");
            }
        } catch (Exception e) {
            System.out.println("Error cargando imagen de modos 2 jugadores: " + e.getMessage());
        }
    }

    private void drawBackground(Graphics g) {
        if (backgroundImage != null) {
            int panelWidth = panel.getWidth();
            int panelHeight = panel.getHeight();

            int imgWidth = backgroundImage.getWidth(null);
            int imgHeight = backgroundImage.getHeight(null);

            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.min(scaleX, scaleY);

            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);

            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);
        }
    }

    private void createButtons() {
        // Boton J1 VS J2 (primer helado - izquierda)
        JButton j1VsJ2Button = createInvisibleButton(70, 156, 180, 230);
        j1VsJ2Button.addActionListener(e -> {
            System.out.println("Modo J1 VS J2 seleccionado");
            selectedMode = "J1VSJ2";
            screenManager.showScreen("twoPlayerFlavorSelection");
        });

        // Boton J1 VS IA (segundo helado - centro)
        JButton j1VsIaButton = createInvisibleButton(282, 156, 180, 230);
        j1VsIaButton.addActionListener(e -> {
            System.out.println("Modo J1 VS IA seleccionado");
            selectedMode = "J1VSIA";
            screenManager.showScreen("twoPlayerFlavorSelection");
        });

        // Boton IA VS IA (tercer helado - derecha)
        JButton iaVsIaButton = createInvisibleButton(492, 156, 175, 230);
        iaVsIaButton.addActionListener(e -> {
            System.out.println("Modo IA VS IA seleccionado");
            selectedMode = "IAVSIA";
            screenManager.showScreen("twoPlayerFlavorSelection");
        });

        // Boton BACK
        JButton backButton = createInvisibleButton(300, 590, 150, 50);
        backButton.addActionListener(e -> {
            System.out.println("Volver a seleccion de cantidad de jugadores");
            screenManager.showScreen("gameType");
        });

        panel.add(j1VsJ2Button);
        panel.add(j1VsIaButton);
        panel.add(iaVsIaButton);
        panel.add(backButton);
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(
                        new Color(255, 200, 100, 150), 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(null);
            }
        });

        return button;
    }

    /**
     * Obtiene el modo de juego seleccionado
     * @return El modo seleccionado (J1VSJ2, J1VSIA, IAVSIA)
     */
    public String getSelectedMode() {
        return selectedMode;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de modos para 2 jugadores");
        selectedMode = "";
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de modos para 2 jugadores");
    }
}