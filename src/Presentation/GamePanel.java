package Presentation;

import Domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel del juego que maneja renderizado y eventos de input
 * Solo interactua con GameController
 */
public class GamePanel extends JPanel {
    private GameController gameController;
    private ScreenManager screenManager;
    private GameRenderer gameRenderer;

    // Overlays
    private PauseOverlay pauseOverlay;
    private DeathOverlay deathOverlay;
    private VictoryOverlay victoryOverlay;

    // Callbacks para comunicacion con GameScreen
    private Runnable onStopGame;
    private Runnable onSaveGame;
    private Runnable onRestartGame;

    // Animacion del reloj
    private int clockFrame = 0;
    private int clockAnimationCounter = 0;
    private static final int CLOCK_ANIMATION_SPEED = 4;
    private static final int CLOCK_TOTAL_FRAMES = 16;
    private Image[] clockSprites;

    private static final int GAME_WIDTH = 756;
    private static final int GAME_HEIGHT = 756;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        this.gameRenderer = new GameRenderer();

        loadClockSprites();

        setupInputListeners();
    }

    /**
     * Carga los sprites animados del reloj
     */
    private void loadClockSprites() {
        clockSprites = new Image[CLOCK_TOTAL_FRAMES];
        for (int i = 0; i < CLOCK_TOTAL_FRAMES; i++) {
            String path = "Images/Resources/Clocks/" + (i + 1) + ".png";
            try {
                Image sprite = new javax.swing.ImageIcon(path).getImage();
                if (sprite.getWidth(null) > 0) {
                    clockSprites[i] = sprite;
                } else {
                    System.out.println("Advertencia: No se pudo cargar sprite de reloj " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de reloj: " + path);
            }
        }
        System.out.println("Sprites de reloj cargados: " + CLOCK_TOTAL_FRAMES + " frames");
    }

    /**
     * Obtiene el frame actual del reloj animado
     */
    private Image getClockSprite() {
        if (clockSprites == null || clockSprites.length == 0) {
            return null;
        }

        // Actualizar animacion
        clockAnimationCounter++;
        if (clockAnimationCounter >= CLOCK_ANIMATION_SPEED) {
            clockAnimationCounter = 0;
            clockFrame++;
            if (clockFrame >= CLOCK_TOTAL_FRAMES) {
                clockFrame = 0;
            }
        }

        return clockSprites[clockFrame];
    }

    /**
     * Inicializa el panel con todas las referencias necesarias
     */
    public void initialize(GameController controller, ScreenManager screenManager,
                           PauseOverlay pauseOverlay, DeathOverlay deathOverlay,
                           VictoryOverlay victoryOverlay,
                           Runnable onStopGame, Runnable onSaveGame, Runnable onRestartGame) {
        this.gameController = controller;
        this.screenManager = screenManager;
        this.pauseOverlay = pauseOverlay;
        this.deathOverlay = deathOverlay;
        this.victoryOverlay = victoryOverlay;
        this.onStopGame = onStopGame;
        this.onSaveGame = onSaveGame;
        this.onRestartGame = onRestartGame;
    }

    private void setupInputListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyInput(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyInput(e, false);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMotion(e.getPoint());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameController == null || !gameController.isInitialized()) {
            return;
        }

        // Obtener datos de renderizado
        GameController.RenderData renderData = gameController.getRenderData();

        // Renderizar usando GameRenderer
        if (gameRenderer != null) {
            gameRenderer.renderFromData(g, renderData, getWidth(), getHeight());
        }

        // Dibujar UI adicional
        drawUI(g);

        // Dibujar overlays
        if (pauseOverlay != null) {
            pauseOverlay.draw(g);
        }
        if (deathOverlay != null) {
            deathOverlay.draw(g);
        }
        if (victoryOverlay != null) {
            victoryOverlay.draw(g);
        }
    }

    private void drawUI(Graphics g) {
        if (gameController == null) {
            return;
        }

        // Dibujar informacion de jugadores
        drawPlayerScores(g);

        // Dibujar contador de enemigos
        drawEnemyCounter(g);

        // Dibujar contador de frutas
        drawFruitCounter(g);

        // Dibujar timer
        drawTimer(g);

        // Dibujar boton de pausa
        drawPauseButton(g);
    }

    private void drawPlayerScores(Graphics g) {
        Font scoreFont = new Font("Arial", Font.BOLD, 16);
        g.setFont(scoreFont);

        int playerCount = gameController.getPlayerCount();

        if (playerCount == 1) {
            // Un solo jugador - centrado a la izquierda
            drawSinglePlayerScore(g, 0, 30, 10, scoreFont);
        } else if (playerCount == 2) {
            // Dos jugadores - J1 a la izquierda, J2 hacia el centro-derecha
            drawSinglePlayerScore(g, 0, 30, 10, scoreFont);
            drawSinglePlayerScore(g, 1, 300, 10, scoreFont);
        }
    }

    private void drawSinglePlayerScore(Graphics g, int playerIndex, int x, int y, Font font) {
        String playerName = gameController.getPlayerName(playerIndex);
        String playerFlavor = gameController.getPlayerFlavor(playerIndex);
        int score = gameController.getPlayerScore(playerIndex);

        // Dibujar icono del jugador (J1 o J2)
        Image playerIcon = loadPlayerIcon(playerIndex + 1);
        if (playerIcon != null) {
            g.drawImage(playerIcon, x, y, 40, 40, null);
        }

        // Posiciones del texto (al lado del icono)
        int textX = x + 40 + 8;
        int scoreY = y + 18;
        int nameY = y + 32;
        int flavorY = y + 46;

        // Dibujar puntaje (formato 000000)
        Font scoreFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(scoreFont);
        String scoreText = String.valueOf(score);


        g.setColor(Color.BLACK);
        g.drawString(scoreText, textX + 1, scoreY + 1);
        g.setColor(Color.YELLOW);
        g.drawString(scoreText, textX, scoreY);

        // Dibujar nombre
        Font nameFont = new Font("Arial", Font.BOLD, 13);
        g.setFont(nameFont);

        g.setColor(Color.BLACK);
        g.drawString(playerName, textX + 1, nameY + 1);

        boolean isPlayer2 = (playerIndex == 1);
        g.setColor(isPlayer2 ? new Color(255, 105, 180) : Color.YELLOW);
        g.drawString(playerName, textX, nameY);

        // Dibujar sabor
        Font flavorFont = new Font("Arial", Font.PLAIN, 11);
        g.setFont(flavorFont);
        String flavorDisplay = getFlavorDisplayName(playerFlavor);

        g.setColor(Color.BLACK);
        g.drawString(flavorDisplay, textX + 1, flavorY + 1);
        g.setColor(new Color(225, 225, 50));
        g.drawString(flavorDisplay, textX, flavorY);
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

    private Image loadPlayerIcon(int playerNumber) {
        try {
            String path = "Images/Resources/J" + playerNumber + ".png";
            Image icon = new javax.swing.ImageIcon(path).getImage();
            if (icon.getWidth(null) > 0) {
                return icon;
            }
        } catch (Exception e) {
            System.out.println("Error cargando icono de jugador: J" + playerNumber);
        }
        return null;
    }

    private void drawEnemyCounter(Graphics g) {
        int enemyCount = gameController.getActiveEnemyCount();

        if (enemyCount == 0) {
            return;
        }

        Font countFont = new Font("Arial", Font.PLAIN, 15);
        g.setFont(countFont);

        int startX = 650;
        int startY = 20;
        int lineHeight = 20;
        int currentY = startY;

        // Obtener conteo por tipo de enemigo
        java.util.Map<String, Integer> enemyCounts = new java.util.HashMap<>();

        // Contar trolls
        int trollCount = gameController.getActiveTrollCount();
        if (trollCount > 0) {
            enemyCounts.put("Trolls", trollCount);
        }

        // Contar squids
        int squidCount = gameController.getActiveSquidCount();
        if (squidCount > 0) {
            enemyCounts.put("Calamares", squidCount);
        }

        // Contar pots
        int potCount = gameController.getActivePotCount();
        if (potCount > 0) {
            enemyCounts.put("Macetas", potCount);
        }

        // Contar narvales
        int narvalCount = gameController.getActiveNarvalCount();
        if (narvalCount > 0) {
            enemyCounts.put("Narvales", narvalCount);
        }

        // Dibujar cada tipo de enemigo
        for (java.util.Map.Entry<String, Integer> entry : enemyCounts.entrySet()) {
            String text = entry.getKey() + ": " + entry.getValue();

            g.setColor(Color.BLACK);
            g.drawString(text, startX + 1, currentY + 1);

            g.setColor(Color.WHITE);
            g.drawString(text, startX, currentY);

            currentY += lineHeight;
        }
    }

    private void drawFruitCounter(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String fruitText = "Frutas: " + gameController.getCollectedFruitsCount() +
                "/" + gameController.getTotalFruitsCount();

        g.setColor(Color.BLACK);
        g.drawString(fruitText, 21, 735);

        g.setColor(Color.WHITE);
        g.drawString(fruitText, 20, 735);
    }

    private void drawTimer(Graphics g) {
        int minutes = gameController.getRemainingMinutes();
        int seconds = gameController.getRemainingSeconds();
        String timeText = String.format("%d:%02d", minutes, seconds);

        Font timeFont = new Font("Arial", Font.BOLD, 24);
        g.setFont(timeFont);

        // Posicion del reloj (icono animado)
        int clockX = 540;
        int clockY = 22;
        int clockSize = 40;

        // Dibujar sprite del reloj animado
        Image clockSprite = getClockSprite();
        if (clockSprite != null) {
            g.drawImage(clockSprite, clockX, clockY, clockSize, clockSize, null);
        }

        // Posicion del texto (al lado del reloj)
        int textX = clockX + clockSize + 8;
        int textY = clockY + 22;

        // Sombra
        g.setColor(Color.BLACK);
        g.drawString(timeText, textX + 1, textY + 1);

        // Texto principal (rojo si quedan menos de 30 segundos)
        long remainingMs = (minutes * 60 + seconds) * 1000L;
        if (remainingMs <= 30000) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(timeText, textX, textY);
    }

    private void drawPauseButton(Graphics g) {
        Image buttonImage = gameController.getPauseButtonImage();
        if (buttonImage != null) {
            g.drawImage(buttonImage,
                    gameController.getPauseButtonX(),
                    gameController.getPauseButtonY(),
                    gameController.getPauseButtonWidth(),
                    gameController.getPauseButtonHeight(),
                    null);
        }
    }

    private void handleKeyInput(KeyEvent e, boolean pressed) {
        if (gameController == null || !gameController.isInitialized()) {
            return;
        }

        int key = e.getKeyCode();

        // Escape para salir
        if (key == KeyEvent.VK_ESCAPE && pressed) {
            if (onStopGame != null) {
                onStopGame.run();
            }
            if (screenManager != null) {
                screenManager.showScreen("levelSelection");
            }
            return;
        }

        // Pausa con tecla P
        if (key == KeyEvent.VK_P && pressed) {
            gameController.handleKeyPress(e, pressed);

            if (gameController.isPaused()) {
                pauseOverlay.show();
            } else {
                pauseOverlay.hide();
            }
            repaint();
            return;
        }

        // Delegar input al controlador
        gameController.handleKeyPress(e, pressed);
    }

    private void handleMouseClick(Point clickPos) {
        if (gameController == null || !gameController.isInitialized()) {
            return;
        }

        // Victory overlay
        if (victoryOverlay != null && victoryOverlay.isVisible()) {
            String action = victoryOverlay.handleClick(clickPos);
            if (action != null) {
                if (onStopGame != null) {
                    onStopGame.run();
                }
                if ("levelSelection".equals(action)) {
                    screenManager.showScreen("levelSelection");
                } else if ("menu".equals(action)) {
                    System.out.println("Volviendo al menu desde pantalla de victoria");
                    screenManager.showScreen("menu");
                }
            }
            return;
        }

        // Death overlay
        if (deathOverlay != null && deathOverlay.isVisible()) {
            String action = deathOverlay.handleClick(clickPos);
            if (action != null) {
                if ("restart".equals(action)) {
                    System.out.println("Ejecutando restart desde GamePanel");
                    if (onRestartGame != null) {
                        onRestartGame.run();
                    }
                } else if ("menu".equals(action)) {
                    System.out.println("Volviendo al menu desde pantalla de muerte");
                    if (onStopGame != null) {
                        onStopGame.run();
                    }
                    screenManager.showScreen("menu");
                }
            }
            return;
        }

        // Pause overlay
        if (pauseOverlay != null && pauseOverlay.isVisible()) {
            String action = pauseOverlay.handleClick(clickPos);
            if (action != null) {
                if ("continue".equals(action)) {
                    pauseOverlay.hide();
                    // El controlador manejara el resume
                } else if ("back".equals(action)) {
                    System.out.println("Volviendo al menu desde pausa");
                    pauseOverlay.hide();
                    if (onStopGame != null) {
                        onStopGame.run();
                    }
                    screenManager.showScreen("menu");
                } else if ("save".equals(action)) {
                    if (onSaveGame != null) {
                        onSaveGame.run();
                    }
                }
                repaint();
            }
            return;
        }

        // Click en boton de pausa
        gameController.handleMouseClick(clickPos);
        if (gameController.isPaused()) {
            pauseOverlay.show();
        } else {
            pauseOverlay.hide();
        }
        repaint();
    }

    private void handleMouseMotion(Point mousePos) {
        if (gameController == null || !gameController.isInitialized()) {
            return;
        }

        boolean needsRepaint = false;

        if (victoryOverlay != null && victoryOverlay.isVisible()) {
            if (victoryOverlay.updateHover(mousePos)) {
                needsRepaint = true;
            }
            setCursor(victoryOverlay.getCursor(mousePos));
        } else if (deathOverlay != null && deathOverlay.isVisible()) {
            if (deathOverlay.updateHover(mousePos)) {
                needsRepaint = true;
            }
            setCursor(deathOverlay.getCursor(mousePos));
        } else if (pauseOverlay != null && pauseOverlay.isVisible()) {
            if (pauseOverlay.updateHover(mousePos)) {
                needsRepaint = true;
            }
            setCursor(pauseOverlay.getCursor(mousePos));
        } else {
            gameController.updatePauseButtonHover(mousePos);
            needsRepaint = true;
            setCursor(Cursor.getDefaultCursor());
        }

        if (needsRepaint) {
            repaint();
        }
    }
}