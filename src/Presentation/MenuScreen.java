package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Pantalla de menu principal con GIF animado de fondo
public class MenuScreen implements Screen {
    private JPanel panel;
    private JLabel backgroundLabel;
    private ScreenManager screenManager;

    public MenuScreen(ScreenManager screenManager) {
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
            String[] paths = {"Images/inicioBadIceCream.gif"};

            for (String path : paths) {
                try {
                    ImageIcon gifIcon = new ImageIcon(path);

                    // Verificar que el GIF se cargo correctamente
                    if (gifIcon.getIconWidth() > 0) {
                        System.out.println("GIF cargado desde: " + path);

                        // Escalar el GIF para que llene la ventana (ajustado a nuevo tamano)
                        Image scaledImage = gifIcon.getImage().getScaledInstance(
                                756, 752, Image.SCALE_DEFAULT
                        );
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);

                        // Crear JLabel con el GIF animado
                        backgroundLabel = new JLabel(scaledIcon);
                        backgroundLabel.setLayout(null); // Layout absoluto para botones
                        backgroundLabel.setPreferredSize(new Dimension(756, 752));

                        panel.add(backgroundLabel, BorderLayout.CENTER);
                        return;
                    }
                } catch (Exception ex) {
                    System.out.println("Error con ruta: " + path);
                }
            }

            System.out.println("No se pudo cargar el GIF desde ninguna ruta");
        } catch (Exception e) {
            System.out.println("Error cargando GIF: " + e.getMessage());
        }
    }

    private void createButtons() {
        if (backgroundLabel == null) {
            System.out.println("No se pueden crear botones sin fondo");
            return;
        }

        // Boton start en el area donde dice "CLICK TO LICK"
        JButton startButton = createInvisibleButton(195, 638, 370, 60);
        startButton.addActionListener(e -> {
            System.out.println("Boton presionado - Cambiando a pantalla de opciones");
            screenManager.showScreen("options");
        });

        backgroundLabel.add(startButton);
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

        // Efecto visual al pasar el mouse
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(
                        new Color(255, 255, 255, 100), 2));
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
        System.out.println("Mostrando menu principal");
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando menu principal");
    }
}