package test.java.Test;

import Domain.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;
import java.awt.event.KeyEvent;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad para GameController
 * Cubren inicializacion, actualizacion, condiciones de victoria/derrota,
 * guardado/carga, y manejo de eventos
 */
public class GameControllerTest {

    private GameController controller;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    @Before
    public void setUp() {
        controller = new GameController(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    // ==================== PRUEBAS DE INICIALIZACION ====================

    @Test
    public void testControllerInitializedCorrectly() {
        assertNotNull("El controlador debe ser creado", controller);
        assertFalse("No debe estar inicializado al crear", controller.isInitialized());
        assertEquals("Ancho de pantalla correcto", SCREEN_WIDTH, controller.getScreenWidth());
        assertEquals("Alto de pantalla correcto", SCREEN_HEIGHT, controller.getScreenHeight());
    }

    @Test
    public void testSinglePlayerGameInitialization() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();

        controller.initializeNewGame(
                1, config,
                "TestPlayer", "vanilla",
                false, null, null, null
        );

        assertTrue("El juego debe estar inicializado", controller.isInitialized());
        assertEquals("Debe haber 1 jugador", 1, controller.getPlayerCount());
        assertFalse("No debe ser modo 2 jugadores", controller.isTwoPlayerMode());
        assertEquals("Nombre correcto", "TestPlayer", controller.getPlayerName(0));
        assertEquals("Sabor correcto", "vanilla", controller.getPlayerFlavor(0));
    }

    @Test
    public void testTwoPlayerGameInitialization() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();

        controller.initializeNewGame(
                1, config,
                "Player1", "vanilla",
                true, "Player2", "chocolate", null
        );

        assertTrue("El juego debe estar inicializado", controller.isInitialized());
        assertEquals("Debe haber 2 jugadores", 2, controller.getPlayerCount());
        assertTrue("Debe ser modo 2 jugadores", controller.isTwoPlayerMode());
        assertEquals("Nombre jugador 1", "Player1", controller.getPlayerName(0));
        assertEquals("Nombre jugador 2", "Player2", controller.getPlayerName(1));
    }

    @Test
    public void testAIPlayerInitialization() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();

        controller.initializeNewGame(
                1, config,
                "Player1", "vanilla",
                true, "Player2", "chocolate", "fearful"
        );

        assertTrue("Debe haber un jugador IA", controller.hasAIPlayer());
        assertEquals("Debe haber 2 jugadores", 2, controller.getPlayerCount());
    }

    // ==================== PRUEBAS DE ESTADO DE JUEGO ====================

    @Test
    public void testGameStartsInPlayingState() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        assertTrue("Debe estar en estado jugando", controller.isPlaying());
        assertFalse("No debe mostrar pantalla de victoria", controller.shouldShowVictoryScreen());
        assertFalse("No debe mostrar pantalla de muerte", controller.shouldShowDeathScreen());
    }

    @Test
    public void testPauseToggle() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        assertFalse("Inicialmente no debe estar pausado", controller.isPaused());

        // Simular ESC para pausar
        KeyEvent escPress = new KeyEvent(
                new java.awt.Canvas(),
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_ESCAPE,
                KeyEvent.CHAR_UNDEFINED
        );

        controller.handleKeyPress(escPress, true);
        assertTrue("Debe estar pausado despues de ESC", controller.isPaused());

        controller.handleKeyPress(escPress, true);
        assertFalse("Debe estar despausado despues de segundo ESC", controller.isPaused());
    }

    @Test
    public void testPauseButtonClick() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        Point buttonPos = new Point(
                controller.getPauseButtonX() + 10,
                controller.getPauseButtonY() + 10
        );

        controller.handleMouseClick(buttonPos);
        assertTrue("Debe pausar al hacer click en el boton", controller.isPaused());
    }

    // ==================== PRUEBAS DE TIEMPO ====================

    @Test
    public void testTimerInitialization() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        int initialMinutes = controller.getRemainingMinutes();
        int initialSeconds = controller.getRemainingSeconds();

        assertTrue("Debe haber tiempo restante", initialMinutes > 0 || initialSeconds > 0);
        assertFalse("El tiempo no debe haber terminado", controller.isTimeUp());
    }

    @Test
    public void testUpdateDecrementsTimer() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        int initialMinutes = controller.getRemainingMinutes();
        int initialSeconds = controller.getRemainingSeconds();

        // Actualizar muchas veces para que pase al menos 1 segundo
        for (int i = 0; i < 100; i++) {
            controller.update();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Ignorar
            }
        }

        int finalMinutes = controller.getRemainingMinutes();
        int finalSeconds = controller.getRemainingSeconds();

        boolean timeDecreased = finalMinutes < initialMinutes ||
                (finalMinutes == initialMinutes && finalSeconds < initialSeconds);

        assertTrue("El tiempo debe disminuir despues de updates", timeDecreased);
    }

    // ==================== PRUEBAS DE PUNTUACION Y FRUTAS ====================

    @Test
    public void testInitialScoreIsZero() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        assertEquals("Puntaje inicial debe ser 0", 0, controller.getTotalScore());
        assertEquals("Frutas recolectadas iniciales deben ser 0", 0, controller.getCollectedFruitsCount());
    }

    @Test
    public void testTotalFruitsCountIsPositive() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        int totalFruits = controller.getTotalFruitsCount();
        assertTrue("Debe haber frutas en el nivel", totalFruits > 0);
    }

    @Test
    public void testPlayerScoreTracking() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(
                1, config,
                "Player1", "vanilla",
                true, "Player2", "chocolate", null
        );

        int p1Score = controller.getPlayerScore(0);
        int p2Score = controller.getPlayerScore(1);

        assertEquals("Puntaje jugador 1 inicial", 0, p1Score);
        assertEquals("Puntaje jugador 2 inicial", 0, p2Score);
    }

    // ==================== PRUEBAS DE ENEMIGOS ====================

    @Test
    public void testEnemyCountInitialization() {
        LevelConfiguration config = new LevelConfiguration.Builder()
                .setTrollCount(5)
                .setSquidCount(3)
                .setPotCount(2)
                .setNarvalCount(1)
                .build();

        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        int totalEnemies = controller.getActiveEnemyCount();
        assertTrue("Debe haber enemigos activos", totalEnemies > 0);
    }

    @Test
    public void testSpecificEnemyTypeCounts() {
        LevelConfiguration config = new LevelConfiguration.Builder()
                .setTrollCount(3)
                .setSquidCount(2)
                .setPotCount(1)
                .setNarvalCount(1)
                .build();

        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        assertTrue("Debe haber trolls", controller.getActiveTrollCount() > 0);
        assertTrue("Debe haber squids", controller.getActiveSquidCount() > 0);
    }

    // ==================== PRUEBAS DE GUARDADO Y CARGA ====================

    @Test
    public void testSaveGameCreatesFile() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        String filename = "test_save_" + System.currentTimeMillis();
        boolean saved = controller.saveGame(filename);

        assertTrue("El guardado debe ser exitoso", saved);
    }

    @Test
    public void testLoadGameRestoresPlayerName() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "TestPlayer", "vanilla", false, null, null, null);

        String filename = "test_load_" + System.currentTimeMillis();
        controller.saveGame(filename);

        GameController newController = new GameController(SCREEN_WIDTH, SCREEN_HEIGHT);
        boolean loaded = newController.loadGameFromFile(filename);

        assertTrue("La carga debe ser exitosa", loaded);
        assertEquals("El nombre del jugador debe ser el mismo",
                "TestPlayer", newController.getLoadedPlayerName());
    }

    @Test
    public void testLoadGameRestoresScore() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        String filename = "test_score_" + System.currentTimeMillis();
        controller.saveGame(filename);

        GameController newController = new GameController(SCREEN_WIDTH, SCREEN_HEIGHT);
        newController.loadGameFromFile(filename);

        int originalScore = controller.getTotalScore();
        int loadedScore = newController.getLoadedScore();

        assertEquals("El puntaje debe ser el mismo", originalScore, loadedScore);
    }

    // ==================== PRUEBAS DE REINICIO ====================

    @Test
    public void testRestartLevelResetsScore() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        controller.restartLevel();

        assertEquals("El puntaje debe reiniciarse a 0", 0, controller.getTotalScore());
        assertEquals("Frutas recolectadas deben reiniciarse", 0, controller.getCollectedFruitsCount());
    }

    @Test
    public void testRestartLevelKeepsPlayerInfo() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "TestPlayer", "vanilla", false, null, null, null);

        String originalName = controller.getPlayerName(0);
        String originalFlavor = controller.getPlayerFlavor(0);

        controller.restartLevel();

        assertEquals("El nombre debe mantenerse", originalName, controller.getPlayerName(0));
        assertEquals("El sabor debe mantenerse", originalFlavor, controller.getPlayerFlavor(0));
    }

    // ==================== PRUEBAS DE RENDERIZADO ====================

    @Test
    public void testRenderDataContainsElements() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        GameController.RenderData data = controller.getRenderData();

        assertNotNull("RenderData no debe ser null", data);
        assertNotNull("Lista de jugadores no debe ser null", data.players);
        assertNotNull("Lista de frutas no debe ser null", data.fruits);
        assertNotNull("Lista de enemigos no debe ser null", data.enemies);

        assertFalse("Debe haber jugadores", data.players.isEmpty());
        assertFalse("Debe haber frutas", data.fruits.isEmpty());
    }

    @Test
    public void testRenderDataPlayerCount() {
        LevelConfiguration config = LevelConfiguration.createDefaultLevel1();
        controller.initializeNewGame(
                1, config,
                "P1", "vanilla",
                true, "P2", "chocolate", null
        );

        GameController.RenderData data = controller.getRenderData();

        assertEquals("Debe haber 2 jugadores en RenderData",
                2, data.players.size());
    }

    // ==================== PRUEBAS DE CONFIGURACION DE NIVEL ====================

    @Test
    public void testCustomLevelConfiguration() {
        LevelConfiguration config = new LevelConfiguration.Builder()
                .setBananaCount(10)
                .setGrapeCount(8)
                .setCherryCount(5)
                .setPineappleCount(3)
                .setCactusCount(2)
                .setTrollCount(4)
                .setSquidCount(3)
                .build();

        controller.initializeNewGame(1, config, "Test", "vanilla", false, null, null, null);

        assertTrue("Debe inicializarse con configuracion personalizada",
                controller.isInitialized());
    }

    @Test
    public void testLevelConfigurationFromCounts() {
        LevelConfiguration config = controller.createLevelConfigFromCounts(
                15, 10, 5, 3, 2, 4, 3, 2, 1, 10, 8, 5
        );

        assertNotNull("La configuracion no debe ser null", config);
        assertEquals("Bananas correctas", 15, config.getBananaCount());
        assertEquals("Uvas correctas", 10, config.getGrapeCount());
        assertEquals("Cerezas correctas", 5, config.getCherryCount());
    }
}
