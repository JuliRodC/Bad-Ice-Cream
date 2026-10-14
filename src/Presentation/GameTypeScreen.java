package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de seleccion de tipo de juego (cantidad de jugadores)
 * Ahora usa el GIF TJuego2Jugadores.gif
 */
public class GameTypeScreen implements Screen {
    private JPanel panel;
    private JLabel backgroundLabel;
    private final ScreenManager screenManager;

    public GameTypeScreen(ScreenManager screenManager) {
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
            String path = "Images/CantJugadores.gif";
            ImageIcon gifIcon = new ImageIcon(path);

            if (gifIcon.getIconWidth() > 0) {
                System.out.println("GIF de tipo de juego cargado desde: " + path);

                // Escalar el GIF para que llene la ventana
                Image scaledImage = gifIcon.getImage().getScaledInstance(
                        756, 752, Image.SCALE_DEFAULT
                );
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                backgroundLabel = new JLabel(scaledIcon);
                backgroundLabel.setLayout(null);
                backgroundLabel.setPreferredSize(new Dimension(756, 752));

                panel.add(backgroundLabel, BorderLayout.CENTER);
            } else {
                System.out.println("No se pudo cargar el GIF de tipo de juego");
            }
        } catch (Exception e) {
            System.out.println("Error cargando GIF de tipo de juego: " + e.getMessage());
        }
    }

    private void createButtons() {
        if (backgroundLabel == null) {
            System.out.println("No se pueden crear botones sin fondo");
            return;
        }

        // Boton para 1 jugador (helado de 1 bola - izquierda)
        JButton onePlayerButton = createInvisibleButton(155, 171, 95, 230);
        onePlayerButton.addActionListener(e -> {
            System.out.println("1 jugador seleccionado");
            screenManager.showScreen("flavorSelection");
        });

// Boton para 2 jugadores (helado de 2 bolas - derecha)
        JButton twoPlayersButton = createInvisibleButton(450, 171, 190, 230);
        twoPlayersButton.addActionListener(e -> {
            System.out.println("2 jugadores seleccionado");
            screenManager.showScreen("twoPlayerMode"); // CAMBIAR ESTA LINEA
        });

        // Boton BACK
        JButton backButton = createInvisibleButton(300, 600, 155, 50);
        backButton.addActionListener(e -> {
            System.out.println("Volver a opciones");
            screenManager.showScreen("options");
        });

        backgroundLabel.add(onePlayerButton);
        backgroundLabel.add(twoPlayersButton);
        backgroundLabel.add(backButton);
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);

        // Hacer el boton completamente transparente
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto visual opcional al pasar el mouse
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

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de seleccion de tipo de juego");
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de seleccion de tipo de juego");
    }
}