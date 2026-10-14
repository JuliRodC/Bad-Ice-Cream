package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de seleccion de sabor de helado con campo para nombre de jugador
 */
public class FlavorSelectionScreen implements Screen {
    private JPanel panel;
    private JLabel backgroundLabel;
    private JTextField playerNameField;
    private final ScreenManager screenManager;
    private String selectedFlavor = "";

    public FlavorSelectionScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        initializePanel();
    }

    private void initializePanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(770, 790));
        panel.setBackground(Color.BLACK);

        loadGifBackground();
        createPlayerNameField();
        createButtons();
    }

    private void loadGifBackground() {
        try {
            String[] paths = {"Images/seleccionHelado.gif"};

            for (String path : paths) {
                try {
                    ImageIcon gifIcon = new ImageIcon(path);

                    if (gifIcon.getIconWidth() > 0) {
                        System.out.println("GIF de seleccion de helado cargado desde: " + path);

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

            System.out.println("No se pudo cargar el GIF de seleccion de helado desde ninguna ruta");
        } catch (Exception e) {
            System.out.println("Error cargando GIF de seleccion de helado: " + e.getMessage());
        }
    }

    private void createPlayerNameField() {
        if (backgroundLabel == null) {
            System.out.println("No se puede crear campo de nombre sin fondo");
            return;
        }

        // Campo de texto para el nombre del jugador
        playerNameField = new JTextField("Your Name");
        playerNameField.setBounds(270, 460, 220, 40);

        // Estilo del campo de texto
        playerNameField.setFont(new Font("Arial", Font.BOLD, 18));
        playerNameField.setHorizontalAlignment(JTextField.CENTER);
        playerNameField.setForeground(new Color(139, 69, 19)); // Color cafe/marron
        playerNameField.setBackground(new Color(255, 248, 220)); // Fondo crema
        playerNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Comportamiento del placeholder
        playerNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (playerNameField.getText().equals("Your Name")) {
                    playerNameField.setText("");
                    playerNameField.setForeground(new Color(0, 0, 0));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (playerNameField.getText().trim().isEmpty()) {
                    playerNameField.setText("Your Name");
                    playerNameField.setForeground(new Color(139, 69, 19));
                }
            }
        });

        backgroundLabel.add(playerNameField);
    }

    // En FlavorSelectionScreen.java, modifica el metodo createButtons():

    private void createButtons() {
        if (backgroundLabel == null) {
            System.out.println("No se pueden crear botones sin fondo");
            return;
        }

        // Boton para helado marron (izquierda)
        JButton brownFlavorButton = createInvisibleButton(278, 185, 50, 120);
        brownFlavorButton.addActionListener(e -> {
            String playerName = getPlayerName();
            selectedFlavor = "chocolate";
            screenManager.getSessionData().setPlayer1Name(playerName);
            screenManager.getSessionData().setPlayer1Flavor(selectedFlavor);
            System.out.println("Sabor marron seleccionado por: " + playerName);
            screenManager.showScreen("levelSelection");
        });

        // Boton para helado vainilla (centro)
        JButton vanillaFlavorButton = createInvisibleButton(350, 195, 62, 110);
        vanillaFlavorButton.addActionListener(e -> {
            String playerName = getPlayerName();
            selectedFlavor = "vanilla";
            screenManager.getSessionData().setPlayer1Name(playerName);
            screenManager.getSessionData().setPlayer1Flavor(selectedFlavor);
            System.out.println("Sabor vainilla seleccionado por: " + playerName);
            screenManager.showScreen("levelSelection");
        });

        // Boton para helado rosa (derecha)
        JButton pinkFlavorButton = createInvisibleButton(420, 185, 52, 120);
        pinkFlavorButton.addActionListener(e -> {
            String playerName = getPlayerName();
            selectedFlavor = "strawberry";
            screenManager.getSessionData().setPlayer1Name(playerName);
            screenManager.getSessionData().setPlayer1Flavor(selectedFlavor);
            System.out.println("Sabor rosa seleccionado por: " + playerName);
            screenManager.showScreen("levelSelection");
        });

        // NUEVOS BOTONES PARA CHOCOLATE Y STRAWBERRY
        // Boton para helado chocolate
        JButton chocolateFlavorButton = createInvisibleButton(490, 185, 50, 120);
        chocolateFlavorButton.addActionListener(e -> {
            String playerName = getPlayerName();
            selectedFlavor = "chocolate";
            screenManager.getSessionData().setPlayer1Name(playerName);
            screenManager.getSessionData().setPlayer1Flavor(selectedFlavor);
            System.out.println("Sabor chocolate seleccionado por: " + playerName);
            screenManager.showScreen("levelSelection");
        });

        // Boton para helado strawberry
        JButton strawberryFlavorButton = createInvisibleButton(560, 185, 50, 120);
        strawberryFlavorButton.addActionListener(e -> {
            String playerName = getPlayerName();
            selectedFlavor = "strawberry";
            screenManager.getSessionData().setPlayer1Name(playerName);
            screenManager.getSessionData().setPlayer1Flavor(selectedFlavor);
            System.out.println("Sabor strawberry seleccionado por: " + playerName);
            screenManager.showScreen("levelSelection");
        });

        // Boton BACK
        JButton backButton = createInvisibleButton(300, 600, 155, 50);
        backButton.addActionListener(e -> {
            System.out.println("Volver a seleccion de tipo de juego");
            screenManager.showScreen("gameType");
        });

        backgroundLabel.add(brownFlavorButton);
        backgroundLabel.add(vanillaFlavorButton);
        backgroundLabel.add(pinkFlavorButton);
        backgroundLabel.add(chocolateFlavorButton);
        backgroundLabel.add(strawberryFlavorButton);
        backgroundLabel.add(backButton);
    }

    private JButton createInvisibleButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Obtiene el nombre del jugador ingresado
     * @return El nombre del jugador o "Player" si esta vacio
     */
    public String getPlayerName() {
        if (playerNameField != null) {
            String name = playerNameField.getText().trim();
            if (name.isEmpty() || name.equals("Your Name")) {
                return "Player";
            }
            return name;
        }
        return "Player";
    }

    /**
     * Obtiene el sabor seleccionado
     * @return El sabor seleccionado
     */
    public String getSelectedFlavor() {
        return selectedFlavor;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de seleccion de sabor");
        // Solo resetear el sabor, mantener el nombre
        selectedFlavor = "";
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de seleccion de sabor");
    }
}