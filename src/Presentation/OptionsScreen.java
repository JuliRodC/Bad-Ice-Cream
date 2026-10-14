package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de opciones con GIF animado de fondo
 * Incluye botones para Play, Load, Help y Credits
 */
public class OptionsScreen implements Screen {
    private JPanel panel;
    private JLabel backgroundLabel;
    private ScreenManager screenManager;

    public OptionsScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        initializePanel();
    }

    private void initializePanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(770, 790));
        panel.setBackground(Color.BLACK);

        loadGifBackground();
        createButtons();
    }

    private void loadGifBackground() {
        try {
            String[] paths = {"Images/inicioBadIceCreamMENUN.gif"};

            for (String path : paths) {
                try {
                    ImageIcon gifIcon = new ImageIcon(path);

                    if (gifIcon.getIconWidth() > 0) {
                        System.out.println("GIF de opciones cargado desde: " + path);

                        Image scaledImage = gifIcon.getImage().getScaledInstance(
                                756, 752, Image.SCALE_DEFAULT
                        );
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);

                        backgroundLabel = new JLabel(scaledIcon);
                        backgroundLabel.setLayout(null);
                        backgroundLabel.setPreferredSize(new Dimension(756, 752));

                        panel.add(backgroundLabel, BorderLayout.CENTER);
                        return;
                    }
                } catch (Exception ex) {
                    System.out.println("Error con ruta: " + path);
                }
            }

            System.out.println("No se pudo cargar el GIF de opciones desde ninguna ruta");
        } catch (Exception e) {
            System.out.println("Error cargando GIF de opciones: " + e.getMessage());
        }
    }

    private void createButtons() {
        if (backgroundLabel == null) {
            System.out.println("No se pueden crear botones sin fondo");
            return;
        }

        // Boton PLAY
        JButton playButton = createInvisibleButton(332, 397, 102, 30);
        playButton.addActionListener(e -> {
            System.out.println("Play presionado - Iniciando juego");
            screenManager.showScreen("gameType");
        });
        backgroundLabel.add(playButton);

        // Boton LOAD
        JButton loadButton = createInvisibleButton(325, 442, 110, 30);
        loadButton.addActionListener(e -> {
            System.out.println("Load presionado - Cargando partida");
            screenManager.showScreen("loadGame");
        });
        backgroundLabel.add(loadButton);

        // Boton HELP
        JButton helpButton = createInvisibleButton(332, 492, 100, 30);
        helpButton.addActionListener(e -> {
            System.out.println("Help presionado");
            // Por ahora no hace nada
        });
        backgroundLabel.add(helpButton);

        // Boton CREDITS
        JButton creditsButton = createInvisibleButton(292, 542, 180, 30);
        creditsButton.addActionListener(e -> {
            System.out.println("Credits presionado");
            // Por ahora no hace nada
        });
        backgroundLabel.add(creditsButton);
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);

        // Hacer el boton completamente transparente
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
        System.out.println("Mostrando pantalla de opciones");
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de opciones");
    }
}