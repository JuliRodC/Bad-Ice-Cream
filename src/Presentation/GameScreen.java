package Presentation;

import Domain.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Pantalla del juego que SOLO interactua con GameController
 * NO tiene referencias directas a otras clases de Domain
 */
public class GameScreen implements Screen {
    private JPanel panel;
    private GamePanel gamePanel;
    private final ScreenManager screenManager;

    // UNICA referencia a Domain
    private GameController gameController;

    private Timer gameTimer;

    private PauseOverlay pauseOverlay;
    private DeathOverlay deathOverlay;
    private VictoryOverlay victoryOverlay;

    private static final int GAME_WIDTH = 756;
    private static final int GAME_HEIGHT = 756;

    public GameScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        initializePanel();
    }

    private void initializePanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        panel.setBackground(Color.BLACK);

        gamePanel = new GamePanel();
        panel.add(gamePanel, BorderLayout.CENTER);
    }

    private void startGame() {
        System.out.println("=== INICIANDO JUEGO ===");

        gameController = new GameController(GAME_WIDTH, GAME_HEIGHT);

        GameSessionData sessionData = screenManager.getSessionData();
        String loadedSaveFile = screenManager.getLoadedSaveFile();

        if (loadedSaveFile != null) {
            System.out.println("Cargando juego desde archivo guardado...");
            boolean success = gameController.loadGameFromFile(loadedSaveFile);

            if (!success) {
                JOptionPane.showMessageDialog(panel,
                        "Error al cargar la partida",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                screenManager.clearLoadedSaveData();
                screenManager.showScreen("menu");
                return;
            }

            System.out.println("Partida cargada:");
            System.out.println("  Jugador: " + gameController.getLoadedPlayerName());
            System.out.println("  Puntuacion: " + gameController.getLoadedScore());
        } else {
            // CORRECCION: Obtener nombres y sabores correctamente
            String player1Name = sessionData.getPlayer1Name();
            String player1Flavor = sessionData.getPlayer1Flavor();
            String player2Name = sessionData.getPlayer2Name();
            String player2Flavor = sessionData.getPlayer2Flavor();

            // DEBUG: Verificar que los datos lleguen correctamente
            System.out.println("=== DATOS DE SESION ===");
            System.out.println("  Player 1 Name: " + player1Name);
            System.out.println("  Player 1 Flavor: " + player1Flavor);
            System.out.println("  Player 2 Name: " + player2Name);
            System.out.println("  Player 2 Flavor: " + player2Flavor);
            System.out.println("  Game Mode: " + sessionData.getGameMode());
            System.out.println("=======================");

            String player2Difficulty = null;
            if (sessionData.isTwoPlayerMode() && "J1VSIA".equals(sessionData.getGameMode())) {
                player2Difficulty = extractDifficultyFromName(player2Name);
            }

            gameController.initializeNewGame(
                    1,
                    gameController.createLevelConfigFromCounts(
                            sessionData.getBananaCount(),
                            sessionData.getGrapeCount(),
                            sessionData.getCherryCount(),
                            sessionData.getPineappleCount(),
                            sessionData.getCactusCount(),
                            sessionData.getTrollCount(),
                            sessionData.getSquidCount(),
                            sessionData.getPotCount(),
                            sessionData.getNarvalCount(),
                            sessionData.getIceBlockCount(),
                            sessionData.getFireBlockCount(),
                            sessionData.getHotTileCount()
                    ),
                    player1Name,
                    player1Flavor,
                    sessionData.isTwoPlayerMode(),
                    player2Name,
                    player2Flavor,
                    player2Difficulty
            );
        }

        pauseOverlay = new PauseOverlay(GAME_WIDTH, GAME_HEIGHT);
        deathOverlay = new DeathOverlay(GAME_WIDTH, GAME_HEIGHT);
        victoryOverlay = new VictoryOverlay(GAME_WIDTH, GAME_HEIGHT);

        gamePanel.initialize(
                gameController,
                screenManager,
                pauseOverlay,
                deathOverlay,
                victoryOverlay,
                this::stopGame,
                this::saveGame,
                this::restartGame
        );

        startGameLoop();

        System.out.println("Juego iniciado exitosamente");
    }

    private void startGameLoop() {
        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();
    }

    private void updateGame() {
        if (gameController == null || !gameController.isInitialized()) {
            return;
        }

        // Actualizar logica del juego
        gameController.update();

        // Actualizar overlays segun el estado del juego
        updateOverlays();

        // Repintar
        gamePanel.repaint();
    }

    private void updateOverlays() {
        // Mostrar overlay de victoria si es necesario
        if (gameController.shouldShowVictoryScreen() && !victoryOverlay.isVisible()) {
            int finalScore = gameController.getTotalScore();
            victoryOverlay.show(finalScore);
            System.out.println("=== PANTALLA DE VICTORIA MOSTRADA - Puntaje: " + finalScore + " ===");
        }

        // Mostrar overlay de muerte si es necesario
        if (gameController.shouldShowDeathScreen() && !deathOverlay.isVisible()) {
            int finalScore = gameController.getTotalScore();
            deathOverlay.show(finalScore);
            System.out.println("=== PANTALLA DE MUERTE MOSTRADA - Puntaje: " + finalScore + " ===");
        }
    }

    private String extractDifficultyFromName(String name) {
        if (name != null && name.startsWith("IA-")) {
            return name.substring(3).toLowerCase();
        }
        return "hungry";
    }

    private void saveGame() {
        if (gameController == null || !gameController.isInitialized()) {
            System.out.println("No se puede guardar: juego no inicializado");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Partida");
        fileChooser.setCurrentDirectory(new java.io.File("saves"));

        String playerName = gameController.getPlayerName(0);
        String player2Name = gameController.isTwoPlayerMode() ?
                "_" + gameController.getPlayerName(1) : "";
        String defaultName = "partida_" + playerName + player2Name +
                "_" + System.currentTimeMillis();
        fileChooser.setSelectedFile(new java.io.File(defaultName + ".sav"));

        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".sav");
            }

            @Override
            public String getDescription() {
                return "Archivos de guardado (*.sav)";
            }
        });

        int result = fileChooser.showSaveDialog(panel);

        if (result != JFileChooser.APPROVE_OPTION) {
            System.out.println("Guardado cancelado por el usuario");
            return;
        }

        java.io.File selectedFile = fileChooser.getSelectedFile();
        String filepath = selectedFile.getAbsolutePath();

        if (!filepath.toLowerCase().endsWith(".sav")) {
            filepath += ".sav";
            selectedFile = new java.io.File(filepath);
        }

        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(panel,
                    "El archivo ya existe. Deseas sobrescribirlo?",
                    "Confirmar sobrescritura",
                    JOptionPane.YES_NO_OPTION);

            if (overwrite != JOptionPane.YES_OPTION) {
                System.out.println("Guardado cancelado por el usuario");
                return;
            }
        }

        System.out.println("=== GUARDANDO PARTIDA ===");

        String filename = selectedFile.getName();
        if (filename.endsWith(".sav")) {
            filename = filename.substring(0, filename.length() - 4);
        }

        boolean success = gameController.saveGame(filename);

        if (success) {
            String mensaje = gameController.isTwoPlayerMode() ?
                    "Partida de 2 jugadores guardada exitosamente" :
                    "Partida guardada exitosamente";
            JOptionPane.showMessageDialog(panel,
                    mensaje + " en:\n" + selectedFile.getName(),
                    "Guardar",
                    JOptionPane.INFORMATION_MESSAGE);
            System.out.println("=== PARTIDA GUARDADA EXITOSAMENTE ===");
            System.out.println("  Archivo: " + selectedFile.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(panel,
                    "Error al guardar la partida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("=== ERROR AL GUARDAR PARTIDA ===");
        }
    }

    private void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void restartGame() {
        System.out.println("=== REINICIANDO JUEGO ===");

        stopGame();

        if (deathOverlay != null) {
            deathOverlay.hide();
        }
        if (victoryOverlay != null) {
            victoryOverlay.hide();
        }

        // CORRECCION: Llamar a restartLevel() en lugar de startGame()
        if (gameController != null && gameController.isInitialized()) {
            gameController.restartLevel();

            // CRITICO: Reiniciar el game loop
            startGameLoop();
        } else {
            // Si por alguna razon no esta inicializado, usar startGame()
            if (screenManager.getLoadedSaveFile() != null) {
                screenManager.clearLoadedSaveData();
            }
            startGame();
        }

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            gamePanel.requestFocus();
        });

        System.out.println("=== JUEGO REINICIADO ===");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando pantalla de juego");
        startGame();

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            gamePanel.requestFocus();
        });
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de juego");
        stopGame();
    }
}

