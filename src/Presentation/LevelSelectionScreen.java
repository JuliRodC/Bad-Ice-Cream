package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de seleccion de nivel con botones invisibles
 */
public class LevelSelectionScreen implements Screen {
    private JPanel panel;
    private Image backgroundImage;
    private final ScreenManager screenManager;

    public LevelSelectionScreen(ScreenManager screenManager) {
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
            String[] paths = {"Images/SelcNiveles.png"};

            for (String path : paths) {
                try {
                    backgroundImage = new ImageIcon(path).getImage();
                    if (backgroundImage.getWidth(null) > 0) {
                        System.out.println("Imagen de seleccion de nivel cargada desde: " + path);
                        return;
                    }
                } catch (Exception ex) {
                    // Intenta siguiente ruta
                }
            }

            System.out.println("No se pudo cargar la imagen de seleccion de nivel desde ninguna ruta");
        } catch (Exception e) {
            System.out.println("Error cargando imagen de seleccion de nivel: " + e.getMessage());
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
        // Boton para nivel 1 - INVISIBLE
        JButton level1Button = createInvisibleButton(232, 245, 90, 65);
        level1Button.addActionListener(e -> {
            System.out.println("Nivel 1 seleccionado");
            handleLevelSelection();
        });

        // Boton para nivel 2 - INVISIBLE
        JButton level2Button = createInvisibleButton(335, 245, 90, 65);
        level2Button.addActionListener(e -> {
            System.out.println("Nivel 2 seleccionado (no implementado)");
        });

        // Boton para nivel 3 - INVISIBLE
        JButton level3Button = createInvisibleButton(440, 245, 90, 65);
        level3Button.addActionListener(e -> {
            System.out.println("Nivel 3 seleccionado (no implementado)");
        });

        // Boton para nivel 4 - INVISIBLE
        JButton level4Button = createInvisibleButton(337, 356, 85, 65);
        level4Button.addActionListener(e -> {
            System.out.println("Nivel 4 seleccionado (no implementado)");
        });

        // Boton BACK - INVISIBLE
        JButton backButton = createInvisibleButton(295, 480, 170, 72);
        backButton.addActionListener(e -> {
            System.out.println("Volver a seleccion de sabor");

            // Verificar si venimos de modo 2 jugadores
            GameSessionData sessionData = screenManager.getSessionData();
            if (sessionData.isTwoPlayerMode()) {
                screenManager.showScreen("twoPlayerFlavorSelection");
            } else {
                screenManager.showScreen("flavorSelection");
            }
        });

        panel.add(level1Button);
        panel.add(level2Button);
        panel.add(level3Button);
        panel.add(level4Button);
        panel.add(backButton);
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);

        // Hacer el boton completamente invisible
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Cambiar cursor al pasar sobre el boton
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de seleccion de nivel");
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de seleccion de nivel");
    }

    /**
     * Maneja la seleccion de nivel y configura los datos de sesion
     * segun el modo de juego (1 jugador o 2 jugadores)
     */
    /**
     * Maneja la seleccion de nivel y configura los datos de sesion
     * segun el modo de juego (1 jugador o 2 jugadores)
     */
    private void handleLevelSelection() {
        GameSessionData sessionData = screenManager.getSessionData();

        // Intentar obtener datos del modo de 2 jugadores
        Screen twoPlayerScreen = screenManager.screens.get("twoPlayerFlavorSelection");

        if (twoPlayerScreen != null) {
            TwoPlayerFlavorSelectionScreen flavorScreen = (TwoPlayerFlavorSelectionScreen) twoPlayerScreen;
            String mode = flavorScreen.getGameMode();

            // CORRECCION: Agregar soporte para IAVSIA
            if ("J1VSJ2".equals(mode) || "J1VSIA".equals(mode) || "IAVSIA".equals(mode)) {
                // Modo 2 jugadores (humano vs humano, humano vs IA, o IA vs IA)
                sessionData.setGameMode(mode);
                sessionData.setPlayer1Name(flavorScreen.getPlayer1NamePublic());
                sessionData.setPlayer1Flavor(flavorScreen.getPlayer1Flavor());
                sessionData.setPlayer2Name(flavorScreen.getPlayer2NamePublic());
                sessionData.setPlayer2Flavor(flavorScreen.getPlayer2Flavor());

                System.out.println("Modo " + mode + " configurado:");
                System.out.println("  Jugador 1: " + sessionData.getPlayer1Name() + " (" + sessionData.getPlayer1Flavor() + ")");
                System.out.println("  Jugador 2: " + sessionData.getPlayer2Name() + " (" + sessionData.getPlayer2Flavor() + ")");

                // Ir a la pantalla de configuracion de recursos
                screenManager.showScreen("settings");
                return;
            }
        }

        // Si llegamos aqui, es modo 1 jugador (desde FlavorSelectionScreen)
        Screen singlePlayerScreen = screenManager.screens.get("flavorSelection");

        if (singlePlayerScreen != null) {
            FlavorSelectionScreen flavorScreen = (FlavorSelectionScreen) singlePlayerScreen;

            sessionData.setGameMode("SINGLE");
            sessionData.setPlayer1Name(flavorScreen.getPlayerName());
            sessionData.setPlayer1Flavor(flavorScreen.getSelectedFlavor());

            System.out.println("Modo SINGLE configurado:");
            System.out.println("  Jugador: " + sessionData.getPlayer1Name() + " (" + sessionData.getPlayer1Flavor() + ")");
        }

        // Ir a la pantalla de configuracion de recursos
        screenManager.showScreen("settings");
    }
}
