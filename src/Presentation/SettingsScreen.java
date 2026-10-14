package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de configuracion de recursos del nivel
 * Permite ajustar cantidades de frutas, enemigos y bloques antes de iniciar
 */
public class SettingsScreen implements Screen {
    private JPanel panel;
    private Image backgroundImage;
    private final ScreenManager screenManager;

    // Cantidades de recursos (valores por defecto del nivel 1)
    private int bananaCount = 20;
    private int grapeCount = 16;
    private int cherryCount = 0;
    private int pineappleCount = 0;
    private int cactusCount = 0;

    private int trollCount = 2;
    private int squidCount = 0;
    private int potCount = 0;
    private int narvalCount = 0;

    private int iceBlockCount = 80;
    private int fireBlockCount = 0;
    private int specialBlockCount = 0;
    private int hotTileCount = 0;

    // Labels para mostrar cantidades
    private JLabel bananaLabel;
    private JLabel grapeLabel;
    private JLabel cherryLabel;
    private JLabel pineappleLabel;
    private JLabel cactusLabel;

    private JLabel trollLabel;
    private JLabel squidLabel;
    private JLabel potLabel;
    private JLabel narvalLabel;

    private JLabel iceBlockLabel;
    private JLabel fireBlockLabel;

    private JLabel hotTileLabel;

    public SettingsScreen(ScreenManager screenManager) {
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
        createLabels();
        createButtons();
    }

    private void loadImage() {
        try {
            String path = "Images/SettingsResourses.png";
            backgroundImage = new ImageIcon(path).getImage();
            if (backgroundImage.getWidth(null) > 0) {
                System.out.println("Imagen de configuracion cargada desde: " + path);
            } else {
                System.out.println("No se pudo cargar la imagen de configuracion");
            }
        } catch (Exception e) {
            System.out.println("Error cargando imagen de configuracion: " + e.getMessage());
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

    private void createLabels() {
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Color textColor = new Color(139, 69, 19);

        // FRUTAS
        bananaLabel = createLabel(String.valueOf(bananaCount), 102, 218, labelFont, textColor);
        grapeLabel = createLabel(String.valueOf(grapeCount), 229, 218, labelFont, textColor);
        cherryLabel = createLabel(String.valueOf(cherryCount), 351, 218, labelFont, textColor);
        pineappleLabel = createLabel(String.valueOf(pineappleCount), 475, 218, labelFont, textColor);
        cactusLabel = createLabel(String.valueOf(cactusCount), 592, 218, labelFont, textColor);

        // ENEMIGOS
        trollLabel = createLabel(String.valueOf(trollCount), 142, 403, labelFont, textColor);
        squidLabel = createLabel(String.valueOf(squidCount), 283, 403, labelFont, textColor);
        potLabel  = createLabel(String.valueOf(potCount), 409, 403, labelFont, textColor);
        narvalLabel  = createLabel(String.valueOf(narvalCount), 547, 403, labelFont, textColor);

        // BLOQUES
        iceBlockLabel = createLabel(String.valueOf(iceBlockCount), 153, 576, labelFont, textColor);
        hotTileLabel = createLabel(String.valueOf(hotTileCount), 337, 576, labelFont, textColor);
        fireBlockLabel = createLabel(String.valueOf(fireBlockCount), 512, 576, labelFont, textColor);
    }

    private JLabel createLabel(String text, int x, int y, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, 60, 30);
        label.setFont(font);
        label.setForeground(color);
        panel.add(label);
        return label;
    }

    private void createButtons() {
        // BANANAS
        createResourceButtons(95, 150, 220, () -> bananaCount, val -> bananaCount = val, bananaLabel);

        // UVAS
        createResourceButtons(225, 280, 220, () -> grapeCount, val -> grapeCount = val, grapeLabel);

        // CEREZAS
        createResourceButtons(346, 406, 220, () -> cherryCount, val -> cherryCount = val, cherryLabel);

        // PINAS
        createResourceButtons(470, 530, 220, () -> pineappleCount, val -> pineappleCount = val, pineappleLabel);

        // CACTUS
        createResourceButtons(587, 647, 220, () -> cactusCount, val -> cactusCount = val, cactusLabel);

        // TROLLS
        createResourceButtons(140, 195, 405, () -> trollCount, val -> trollCount = val, trollLabel);

        // SQUID
        createResourceButtons(275, 326, 405, () -> squidCount, val -> squidCount = val, squidLabel);

        // ENEMY 3
        createResourceButtons(404, 464, 405, () -> potCount, val -> potCount  = val, potLabel);

        // ENEMY 4
        createResourceButtons(542, 602, 405, () -> narvalCount, val -> narvalCount = val, narvalLabel);

        // BLOQUES DE HIELO
        createResourceButtons(147, 202, 577, () -> iceBlockCount, val -> iceBlockCount = val, iceBlockLabel);

        // BALDOSAS CALIENTES (segundo espacio en la fila de bloques)
        createResourceButtons(332, 392, 577, () -> hotTileCount, val -> hotTileCount = val, hotTileLabel);

        // FOGATAS (tercer espacio - fireBlockCount)
        createResourceButtons(507, 567, 577, () -> fireBlockCount, val -> fireBlockCount = val, fireBlockLabel);


        // Boton GO
        JButton goButton = createTransparentButton(290, 620, 170, 65);
        goButton.addActionListener(e -> {
            System.out.println("=== INICIANDO JUEGO CON CONFIGURACION ===");
            System.out.println("  Bananas: " + bananaCount);
            System.out.println("  Uvas: " + grapeCount);
            System.out.println("  Cerezas: " + cherryCount);
            System.out.println("  Pinas: " + pineappleCount);
            System.out.println("  Cactus: " + cactusCount);
            System.out.println("  Trolls: " + trollCount);
            System.out.println("  Squids: " + squidCount);
            System.out.println("  Macetas: " + potCount);
            System.out.println("  Narvales: " + narvalCount);
            System.out.println("  Bloques de hielo: " + iceBlockCount);
            System.out.println("  Bloques de fuego: " + fireBlockCount);
            System.out.println("  Bloques especiales: " + specialBlockCount);
            System.out.println("  BALDOSAS CALIENTES: " + hotTileCount); // DEBUG CRITICO

            GameSessionData sessionData = screenManager.getSessionData();
            sessionData.setBananaCount(bananaCount);
            sessionData.setGrapeCount(grapeCount);
            sessionData.setCherryCount(cherryCount);
            sessionData.setPineappleCount(pineappleCount);
            sessionData.setCactusCount(cactusCount);
            sessionData.setTrollCount(trollCount);
            sessionData.setSquidCount(squidCount);
            sessionData.setPotCount(potCount);
            sessionData.setNarvalCount(narvalCount);
            sessionData.setIceBlockCount(iceBlockCount);
            sessionData.setFireBlockCount(fireBlockCount);
            sessionData.setSpecialBlockCount(specialBlockCount);
            sessionData.setHotTileCount(hotTileCount); // CRITICO

            // VERIFICACION INMEDIATA
            System.out.println("=== VERIFICACION EN SESSION DATA ===");
            System.out.println("  HotTileCount en SessionData: " + sessionData.getHotTileCount());

            screenManager.showScreen("game");
        });

        panel.add(goButton);
    }

    private void createResourceButtons(int minusX, int plusX, int y,
                                       ResourceGetter getter, ResourceSetter setter, JLabel label) {
        // Boton menos
        JButton minusBtn = createTransparentButton(minusX, y, 20, 28);
        minusBtn.addActionListener(e -> {
            int current = getter.get();
            if (current > 0) {
                setter.set(current - 1);
                label.setText(String.valueOf(current - 1));
                System.out.println("Decrementado a: " + (current - 1)); // DEBUG
            }
        });

        // Boton mas
        JButton plusBtn = createTransparentButton(plusX, y, 20, 28);
        plusBtn.addActionListener(e -> {
            int current = getter.get();
            setter.set(current + 1);
            label.setText(String.valueOf(current + 1));
            System.out.println("Incrementado a: " + (current + 1)); // DEBUG
        });

        panel.add(minusBtn);
        panel.add(plusBtn);
    }

    private JButton createTransparentButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de configuracion de recursos");
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de configuracion de recursos");
    }

    private interface ResourceGetter {
        int get();
    }

    private interface ResourceSetter {
        void set(int value);
    }

    public int getBananaCount() { return bananaCount; }
    public int getGrapeCount() { return grapeCount; }
    public int getCherryCount() { return cherryCount; }
    public int getPineappleCount() { return pineappleCount; }
    public int getCactusCount() { return cactusCount; }

    public int getTrollCount() { return trollCount; }
    public int getSquidCount() { return squidCount; }
    public int getPotCount() { return potCount; }
    public int getNarvalCount() { return narvalCount; }

    public int getIceBlockCount() { return iceBlockCount; }
    public int getFireBlockCount() { return fireBlockCount; }
    public int getSpecialBlockCount() { return specialBlockCount; }
    public int getHotTileCount() { return hotTileCount; }
}