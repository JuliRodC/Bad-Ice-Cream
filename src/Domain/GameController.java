package Domain;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Controlador principal del juego (Facade Pattern)
 * Esta es la UNICA clase que Presentation debe conocer de Domain
 * Encapsula toda la logica del juego y coordina todos los componentes
 */
public class GameController {
    private GameLevel currentLevel;
    private PlayerManager playerManager;
    private InputHandler inputHandler;
    private CollisionManager collisionManager;
    private GameStateManager stateManager;
    private GameTimer timer;
    private PauseButton pauseButton;

    private int screenWidth;
    private int screenHeight;

    private boolean initialized;

    private LevelConfiguration originalLevelConfig;

    private String player1Name;
    private String player1Flavor;

    private String player2Name;
    private String player2Flavor;
    private boolean player2IsAI;
    private String player2AIType;
    private boolean hasTwoPlayers;

    public GameController(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.initialized = false;
        this.stateManager = new GameStateManager();
        this.timer = new GameTimer(screenWidth);
        this.pauseButton = new PauseButton(screenWidth, screenHeight);
        this.playerManager = new PlayerManager();
    }

    /**
     * Inicializa un nuevo juego con la configuracion especificada
     */
    public void initializeNewGame(int levelNumber, LevelConfiguration config,
                                  String player1Name, String player1Flavor,
                                  boolean hasTwoPlayers, String player2Name,
                                  String player2Flavor, String player2Difficulty) {

        // DEBUG: Verificar nombres recibidos
        System.out.println("=== DEBUG initializeNewGame ===");
        System.out.println("  player1Name: " + player1Name);
        System.out.println("  player1Flavor: " + player1Flavor);
        System.out.println("  hasTwoPlayers: " + hasTwoPlayers);
        System.out.println("  player2Name: " + player2Name);
        System.out.println("  player2Flavor: " + player2Flavor);
        System.out.println("  player2Difficulty: " + player2Difficulty);
        System.out.println("===============================");

        this.originalLevelConfig = config;

        this.player1Name = player1Name;
        this.player1Flavor = player1Flavor;
        this.hasTwoPlayers = hasTwoPlayers;

        if (hasTwoPlayers) {
            this.player2Name = player2Name;
            this.player2Flavor = player2Flavor;
            this.player2IsAI = player2Difficulty != null && !player2Difficulty.isEmpty();
            this.player2AIType = player2Difficulty;
        }


        currentLevel = LevelFactory.createLevel(levelNumber, config);
        currentLevel.initialize();

        playerManager.clear();

        String p1FlavorCapitalized = capitalizeFirstLetter(player1Flavor);
        String p1SpritePath = "Images/IceCreams/" + p1FlavorCapitalized;

        boolean player1IsAI = player1Name != null && player1Name.startsWith("IA-");
        String player1AIType = null;

        Player player1;
        if (player1IsAI) {
            // Extraer dificultad del nombre (ej: "IA-hungry" -> "hungry")
            player1AIType = player1Name.substring(3).toLowerCase();
            player1 = AIPlayerFactory.createAIFromDifficulty(
                    player1AIType, 210, 210, p1SpritePath,
                    currentLevel.getGameMap().getGrid(),
                    currentLevel.getPathFinder()
            );
        } else {
            player1 = new IceCreamPlayer(210, 210, p1SpritePath);
        }

        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );
        playerManager.registerPlayer(player1, player1Name, player1Flavor, player1IsAI);
        System.out.println("DEBUG GameController: Player1 registrado - isAI=" + player1IsAI + ", tipo=" + player1AIType);  // â­ DEBUG
        currentLevel.getFruitManager().registerPlayer(player1);




        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );


        if (hasTwoPlayers) {
            String p2FlavorCapitalized = capitalizeFirstLetter(player2Flavor);
            String p2SpritePath = "Images/IceCreams/" + p2FlavorCapitalized;

            // â­ DETECTAR si player2 es IA (igual que con player1)
            boolean player2IsAI = player2Name != null && player2Name.startsWith("IA-");
            String player2AIType = null;

            Player player2;
            if (player2IsAI) {
                // Extraer dificultad del nombre (ej: "IA-fearful" -> "fearful")
                player2AIType = player2Name.substring(3).toLowerCase();
                player2 = AIPlayerFactory.createAIFromDifficulty(
                        player2AIType, 546, 546, p2SpritePath,
                        currentLevel.getGameMap().getGrid(),
                        currentLevel.getPathFinder()
                );
            } else if (player2Difficulty != null && !player2Difficulty.isEmpty()) {
                // Compatibilidad con el sistema anterior (J1VSIA)
                player2 = AIPlayerFactory.createAIFromDifficulty(
                        player2Difficulty, 546, 546, p2SpritePath,
                        currentLevel.getGameMap().getGrid(),
                        currentLevel.getPathFinder()
                );
                player2IsAI = true;
            } else {
                player2 = new IceCreamPlayer(546, 546, p2SpritePath);
            }

            player2.setCollisionDetector(
                    currentLevel.getCollisionDetector(),
                    currentLevel.getGameMap().getGrid()
            );

            playerManager.registerPlayer(player2, player2Name, player2Flavor, player2IsAI);
            System.out.println("DEBUG GameController: Player2 registrado - isAI=" + player2IsAI + ", tipo=" + player2AIType);
            currentLevel.getFruitManager().registerPlayer(player2);
        }

        System.out.println("DEBUG: p1SpritePath = " + p1SpritePath);
        System.out.println("DEBUG: player1Name = " + player1Name);
        System.out.println("DEBUG: player1Flavor = " + player1Flavor);

        currentLevel.initializeSquids(playerManager);

        playerManager.setFruitManager(currentLevel.getFruitManager());

        inputHandler = new InputHandler(playerManager, currentLevel.getGameMap());

        collisionManager = new CollisionManager(
                currentLevel.getFruitManager(),
                currentLevel.getEnemyManager(),
                currentLevel.getGameMap()
        );

        stateManager.reset();
        timer.reset();
        pauseButton.resume();

        initialized = true;
        System.out.println("GameController: Juego inicializado correctamente");
    }

    public void initializeFromSaveData(GameSaveData saveData) {
        if (saveData == null) {
            System.out.println("Error: SaveData es null");
            return;
        }

        // Verificar si el save tiene levelConfig
        GameSaveData.LevelConfigData configData = saveData.getLevelConfig();

        LevelConfiguration config;
        if (configData == null) {
            System.out.println("ADVERTENCIA: Save antiguo detectado, usando configuracion por defecto");
            config = LevelConfiguration.createDefaultLevel1();
        } else {
            config = new LevelConfiguration.Builder()
                    .setBananaCount(configData.getBananaCount())
                    .setGrapeCount(configData.getGrapeCount())
                    .setCherryCount(configData.getCherryCount())
                    .setPineappleCount(configData.getPineappleCount())
                    .setCactusCount(configData.getCactusCount())
                    .setTrollCount(configData.getTrollCount())
                    .setSquidCount(configData.getSquidCount())
                    .setPotCount(configData.getPotCount())
                    .setNarvalCount(configData.getNarvalCount())
                    .setIceBlockCount(configData.getIceBlockCount())
                    .setFireBlockCount(configData.getFireBlockCount())
                    .setHotTileCount(configData.getHotTileCount())
                    .build();
        }

        // GUARDAR configuracion original
        this.originalLevelConfig = config;

        currentLevel = new LevelOne(config);
        currentLevel.initialize();

        playerManager.clear();

        // CARGAR JUGADOR 1
        String p1FlavorCapitalized = capitalizeFirstLetter(saveData.getPlayerFlavor());
        String p1SpritePath = "Images/IceCreams/" + p1FlavorCapitalized;

        Point gridPos = new Point(saveData.getPlayerX(), saveData.getPlayerY());
        Point pixelPos = currentLevel.getGameMap().getGrid().gridToPixel(gridPos.x, gridPos.y);

        boolean player1IsAI = saveData.getPlayerName() != null &&
                saveData.getPlayerName().startsWith("IA-");

        Player player1;
        if (player1IsAI) {
            String player1AIType = saveData.getPlayerName().substring(3).toLowerCase();
            player1 = AIPlayerFactory.createAIFromDifficulty(
                    player1AIType,
                    pixelPos.x, pixelPos.y,
                    p1SpritePath,
                    currentLevel.getGameMap().getGrid(),
                    currentLevel.getPathFinder()
            );
            System.out.println("Jugador 1 IA cargado: " + player1AIType);
        } else {
            player1 = new IceCreamPlayer(pixelPos.x, pixelPos.y, p1SpritePath);
        }

        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );

        playerManager.registerPlayer(player1, saveData.getPlayerName(),
                saveData.getPlayerFlavor(), player1IsAI);
        currentLevel.getFruitManager().registerPlayer(player1);


        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );

        // GUARDAR datos de jugador 1
        this.player1Name = saveData.getPlayerName();
        this.player1Flavor = saveData.getPlayerFlavor();

        System.out.println("Jugador 1 cargado: " + saveData.getPlayerName() +
                " en grid (" + gridPos.x + ", " + gridPos.y + ")");

        // CARGAR JUGADOR 2 SI EXISTE
        this.hasTwoPlayers = saveData.isTwoPlayerMode();
        if (saveData.isTwoPlayerMode()) {
            String p2FlavorCapitalized = capitalizeFirstLetter(saveData.getPlayer2Flavor());
            String p2SpritePath = "Images/IceCreams/" + p2FlavorCapitalized;

            Point gridPos2 = new Point(saveData.getPlayer2GridX(), saveData.getPlayer2GridY());
            Point pixelPos2 = currentLevel.getGameMap().getGrid().gridToPixel(
                    gridPos2.x, gridPos2.y);

            Player player2;

            // Verificar si el jugador 2 es IA
            if (saveData.isPlayer2AI() && saveData.getPlayer2AIType() != null) {
                // Crear jugador IA del tipo guardado
                String aiTypeStr = saveData.getPlayer2AIType().toLowerCase();
                player2 = AIPlayerFactory.createAIFromDifficulty(
                        aiTypeStr,
                        pixelPos2.x,
                        pixelPos2.y,
                        p2SpritePath,
                        currentLevel.getGameMap().getGrid(),
                        currentLevel.getPathFinder()
                );
                System.out.println("Jugador 2 IA cargado: " + saveData.getPlayer2AIType());
            } else {
                // Crear jugador humano normal
                player2 = new IceCreamPlayer(pixelPos2.x, pixelPos2.y, p2SpritePath);
            }

            player2.setCollisionDetector(
                    currentLevel.getCollisionDetector(),
                    currentLevel.getGameMap().getGrid()
            );

            playerManager.registerPlayer(player2, saveData.getPlayer2Name(),
                    saveData.getPlayer2Flavor(), saveData.isPlayer2AI());
            currentLevel.getFruitManager().registerPlayer(player2);

            // GUARDAR datos de jugador 2
            this.player2Name = saveData.getPlayer2Name();
            this.player2Flavor = saveData.getPlayer2Flavor();
            this.player2IsAI = saveData.isPlayer2AI();
            this.player2AIType = saveData.getPlayer2AIType();

            System.out.println("Jugador 2 cargado: " + saveData.getPlayer2Name() +
                    " en grid (" + gridPos2.x + ", " + gridPos2.y + ")" +
                    (saveData.isPlayer2AI() ? " [IA]" : ""));
        }

        reconstructSavedGameState(saveData, currentLevel.getCollisionDetector());

        // CRITICO: Asegurar que FruitManager tenga GameMap para pinas
        currentLevel.getFruitManager().setGameMap(currentLevel.getGameMap());

        inputHandler = new InputHandler(playerManager, currentLevel.getGameMap());

        collisionManager = new CollisionManager(
                currentLevel.getFruitManager(),
                currentLevel.getEnemyManager(),
                currentLevel.getGameMap()
        );

        stateManager.reset();
        timer.setRemainingTime(saveData.getRemainingTime());
        pauseButton.resume();

        initialized = true;

        System.out.println("GameController: Juego cargado desde save data");
        System.out.println("Modo 2 jugadores: " + saveData.isTwoPlayerMode());
    }

    /**
     * Reconstruye el estado guardado del juego
     */
    private void reconstructSavedGameState(GameSaveData saveData, CollisionDetector levelCollisionDetector) {
        System.out.println("=== RECONSTRUYENDO ESTADO GUARDADO ===");

        GameMap gameMap = currentLevel.getGameMap();
        GridSystem grid = gameMap.getGrid();

        // 1. RECONSTRUIR BLOQUES DE HIELO
        System.out.println("Bloques de hielo a reconstruir: " + saveData.getIceBlocksData().size());
        gameMap.getBlocks().clear();
        for (GameSaveData.IceBlockData blockData : saveData.getIceBlocksData()) {
            Point pixelPos = grid.gridToPixel(blockData.getGridX(), blockData.getGridY());
            Block ice = new SolidIceBlock(pixelPos.x, pixelPos.y);
            gameMap.getBlocks().add(ice);
            System.out.println("Bloque de hielo reconstruido en grid (" + blockData.getGridX() + ", " + blockData.getGridY() + ")");
        }
        System.out.println("Total bloques reconstruidos: " + gameMap.getBlocks().size());

        // 2. RECONSTRUIR FOGATAS
        System.out.println("Fogatas a reconstruir: " + saveData.getCampfiresData().size());
        gameMap.getCampfires().clear();
        for (GameSaveData.CampfireData campfireData : saveData.getCampfiresData()) {
            Campfire campfire = new Campfire(
                    campfireData.getGridX(),
                    campfireData.getGridY(),
                    grid
            );
            if (!campfireData.isLit()) {
                campfire.extinguish();
            }
            campfire.setHasIceAbove(campfireData.hasIceAbove());
            gameMap.getCampfires().add(campfire);
            System.out.println("Fogata reconstruida en grid (" + campfireData.getGridX() + ", " + campfireData.getGridY() + ")");
        }
        System.out.println("Total fogatas reconstruidas: " + gameMap.getCampfires().size());

        // 3. RECONSTRUIR BALDOSAS CALIENTES
        System.out.println("Baldosas calientes a reconstruir: " + saveData.getHotTilesData().size());
        gameMap.getTiles().clear();
        for (GameSaveData.HotTileData tileData : saveData.getHotTilesData()) {
            HotTile hotTile = new HotTile(tileData.getGridX(), tileData.getGridY(), grid);
            gameMap.getTiles().add(hotTile);
            System.out.println("Baldosa caliente reconstruida en grid (" + tileData.getGridX() + ", " + tileData.getGridY() + ")");
        }
        System.out.println("Total baldosas reconstruidas: " + gameMap.getTiles().size());

        // 4. RECONSTRUIR ENEMIGOS
        System.out.println("Enemigos a reconstruir: " + saveData.getEnemiesData().size());
        EnemyManager enemyManager = currentLevel.getEnemyManager();
        enemyManager.clear();

        TargetSelector targetSelector = new TargetSelector(playerManager, grid);
        enemyManager.setTargetSelector(targetSelector);

        for (GameSaveData.EnemyData enemyData : saveData.getEnemiesData()) {
            String enemyType = enemyData.getEnemyType();
            int gridX = enemyData.getGridX();
            int gridY = enemyData.getGridY();

            if ("TROLL".equals(enemyType)) {
                enemyManager.createTroll(gridX, gridY);
            } else if ("SQUID".equals(enemyType)) {
                enemyManager.createSquid(gridX, gridY);
            } else if ("POT".equals(enemyType)) {
                enemyManager.createPot(gridX, gridY);
            } else if ("NARVAL".equals(enemyType)) {
                enemyManager.createNarval(gridX, gridY);
            }
        }
        System.out.println("Total enemigos reconstruidos: " + enemyManager.getEnemies().size());

        // 5. RECONSTRUIR FRUTAS
        FruitManager fruitManager = currentLevel.getFruitManager();
        fruitManager.setGameMap(gameMap);

        // 6. Verificar si levelConfig existe
        GameSaveData.LevelConfigData configData = saveData.getLevelConfig();
        if (configData != null) {
            fruitManager.configureWaveCounts(
                    configData.getBananaCount(),
                    configData.getGrapeCount(),
                    configData.getCherryCount(),
                    configData.getPineappleCount(),
                    configData.getCactusCount()
            );
            System.out.println("Configuracion de oleadas cargada:");
            System.out.println("  Bananas totales: " + configData.getBananaCount());
            System.out.println("  Uvas totales: " + configData.getGrapeCount());
            System.out.println("  Cerezas totales: " + configData.getCherryCount());
            System.out.println("  Pinas totales: " + configData.getPineappleCount());
            System.out.println("  Cactus totales: " + configData.getCactusCount());
        } else {
            System.out.println("ADVERTENCIA: LevelConfig es null, usando valores por defecto");
            fruitManager.configureWaveCounts(20, 16, 0, 0, 0);
        }

        // Limpiar SOLO la lista de frutas, no los jugadores registrados
        fruitManager.getFruits().clear();

        int bananasFound = 0;
        int grapesFound = 0;
        int cherriesFound = 0;
        int pineapplesFound = 0;
        int cactusesFound = 0;

        // Contar frutas recolectadas por tipo
        int bananasCollected = 0;
        int grapesCollected = 0;
        int cherriesCollected = 0;
        int pineapplesCollected = 0;
        int cactusesCollected = 0;

        System.out.println("Frutas a reconstruir: " + saveData.getFruitsData().size());

        for (GameSaveData.FruitData fruitData : saveData.getFruitsData()) {
            String fruitType = fruitData.getFruitType();
            int gridX = fruitData.getGridX();
            int gridY = fruitData.getGridY();

            Fruit fruit = null;

            if ("BANANA".equals(fruitType)) {
                fruit = new BananaFruit(gridX, gridY, grid);
                bananasFound++;
                if (fruitData.isCollected()) {
                    bananasCollected++;
                }
            } else if ("GRAPE".equals(fruitType)) {
                fruit = new GrapeFruit(gridX, gridY, grid);
                grapesFound++;
                if (fruitData.isCollected()) {
                    grapesCollected++;
                }
            } else if ("CHERRY".equals(fruitType)) {
                fruit = new CherryFruit(gridX, gridY, grid, gameMap);
                cherriesFound++;
                if (fruitData.isCollected()) {
                    cherriesCollected++;
                }
                if ("PINEAPPLE".equals(fruitType)) {
                    fruit = new PineappleFruit(gridX, gridY, grid, levelCollisionDetector);
                    pineapplesFound++;
                    if (fruitData.isCollected()) {
                        pineapplesCollected++;
                    }
                }



            } else if ("CACTUS".equals(fruitType)) {
                fruit = new CactusFruit(gridX, gridY, grid);
                cactusesFound++;
                if (fruitData.isCollected()) {
                    cactusesCollected++;
                }
            }

            if (fruit != null) {
                if (fruitData.isCollected()) {
                    fruit.collect();
                }
                fruitManager.getFruits().add(fruit);
            }
        }

        // CONFIGURAR ESTADO DE OLEADAS CORRECTAMENTE
        // Bananas completadas si todas fueron recolectadas
        if (bananasFound > 0 && bananasCollected == bananasFound) {
            fruitManager.setBananasCompleted(true);
        } else if (bananasFound == 0) {
            fruitManager.setBananasCompleted(true);
        }

        // Uvas spawneadas si hay al menos una uva
        if (grapesFound > 0) {
            fruitManager.setGrapesSpawned(true);
        }

        // Uvas completadas si todas fueron recolectadas
        if (grapesFound > 0 && grapesCollected == grapesFound) {
            fruitManager.setGrapesCompleted(true);
        }

        // Cerezas spawneadas si hay al menos una cereza
        if (cherriesFound > 0) {
            fruitManager.setCherriesSpawned(true);
        }

        // Cerezas completadas si todas fueron recolectadas
        if (cherriesFound > 0 && cherriesCollected == cherriesFound) {
            fruitManager.setCherriesCompleted(true);
        }

        // Pinas spawneadas si hay al menos una pina
        if (pineapplesFound > 0) {
            fruitManager.setPineapplesSpawned(true);
        }

        // Pinas completadas si todas fueron recolectadas
        if (pineapplesFound > 0 && pineapplesCollected == pineapplesFound) {
            fruitManager.setPineapplesCompleted(true);
        }

        // Cactus spawneados si hay al menos uno
        if (cactusesFound > 0) {
            fruitManager.setCactusesSpawned(true);
        }

        // Cactus completados si todos fueron recolectados
        if (cactusesFound > 0 && cactusesCollected == cactusesFound) {
            fruitManager.setCactusesCompleted(true);
        }

        // Sobrescribir con los valores guardados si existen
        fruitManager.setBananasCompleted(saveData.isBananasCompleted());
        fruitManager.setGrapesCompleted(saveData.isGrapesCompleted());
        fruitManager.setCherriesCompleted(saveData.isCherriesCompleted());
        fruitManager.setPineapplesCompleted(saveData.isPineapplesCompleted());
        fruitManager.setCactusesCompleted(saveData.isCactusesCompleted());

        if (saveData.isGrapesSpawned()) {
            fruitManager.setGrapesSpawned(true);
        }
        if (saveData.isCherriesSpawned()) {
            fruitManager.setCherriesSpawned(true);
        }
        if (saveData.isPineapplesSpawned()) {
            fruitManager.setPineapplesSpawned(true);
        }
        if (saveData.isCactusesSpawned()) {
            fruitManager.setCactusesSpawned(true);
        }

        // 7. RESTAURAR PUNTAJE Y CONTADOR
        Player player1 = playerManager.getPlayer(0);
        if (player1 != null) {
            fruitManager.setPlayerScore(player1, saveData.getScore());
            fruitManager.setPlayerCollectedFruits(player1, saveData.getCollectedFruits());
        }

        // RESTAURAR puntaje y contador del jugador 2 si existe
        if (saveData.isTwoPlayerMode()) {
            Player player2 = playerManager.getPlayer(1);
            if (player2 != null) {
                fruitManager.setPlayerScore(player2, saveData.getPlayer2Score());
                fruitManager.setPlayerCollectedFruits(player2, saveData.getPlayer2CollectedFruits());
            }
        }

        // Calcular total de frutas
        int totalFruitsInGame = fruitManager.getFruits().size();
        fruitManager.setTotalFruits(totalFruitsInGame);

        // 8. RECONSTRUIR MAPAS DE OCUPACION
        gameMap.rebuildOccupancyMaps();

// 9. ASEGURAR QUE FRUITMANAGER TENGA GAMEMAP CONFIGURADO
// Esto es critico para que las pinas funcionen correctamente


        System.out.println("=== ESTADO RECONSTRUIDO EXITOSAMENTE ===");
        System.out.println("Frutas reconstruidas:");
        System.out.println("  Bananas: " + bananasFound + " (recolectadas: " + bananasCollected + ")");
        System.out.println("  Uvas: " + grapesFound + " (recolectadas: " + grapesCollected + ")");
        System.out.println("  Cerezas: " + cherriesFound + " (recolectadas: " + cherriesCollected + ")");
        System.out.println("  Pinas: " + pineapplesFound + " (recolectadas: " + pineapplesCollected + ")");
        System.out.println("  Cactus: " + cactusesFound + " (recolectados: " + cactusesCollected + ")");
        System.out.println("Estado de oleadas:");
        System.out.println("  Bananas completadas: " + fruitManager.areBananasCompleted());
        System.out.println("  Uvas spawneadas: " + fruitManager.areGrapesSpawned());
        System.out.println("  Uvas completadas: " + fruitManager.areGrapesCompleted());
        System.out.println("  Cerezas spawneadas: " + fruitManager.areCherriesSpawned());
        System.out.println("  Cerezas completadas: " + fruitManager.areCherriesCompleted());
        System.out.println("  Pinas spawneadas: " + fruitManager.arePineapplesSpawned());
        System.out.println("  Pinas completadas: " + fruitManager.arePineapplesCompleted());
        System.out.println("  Cactus spawneados: " + fruitManager.areCactusesSpawned());
        System.out.println("  Cactus completados: " + fruitManager.areCactusesCompleted());
        System.out.println("Puntaje restaurado: " + saveData.getScore());
        System.out.println("Frutas recolectadas: " + saveData.getCollectedFruits() + "/" + totalFruitsInGame);
    }


    /**
     * Actualiza la logica del juego
     */
    public void update() {
        if (!initialized) {
            return;
        }

        if (pauseButton.isPaused()) {
            stateManager.pause();
            return;
        } else if (stateManager.isPaused()) {
            stateManager.resume();
            timer.resume();
        }

        if (!stateManager.canUpdateGame()) {
            return;
        }

        stateManager.update();
        timer.update();

        if (timer.isTimeUp() && stateManager.isPlaying()) {
            stateManager.triggerTimeUp();
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null && !playerManager.isPlayerDead(i)) {
                    player.startDeathAnimation();
                }
            }
            return;
        }

        if (stateManager.isPlaying()) {
            inputHandler.updatePlayerMovement();

            playerManager.updateAllPlayers(
                    currentLevel.getFruitManager(),
                    currentLevel.getEnemyManager()
            );

            currentLevel.update();

            collisionManager.processAllIceActions(playerManager);

            checkGameConditions();
        }

        // CRITICO: Actualizar jugadores durante animacion de muerte
        if (stateManager.getCurrentState() == GameStateManager.GameState.DEATH_ANIMATION) {
            // Actualizar solo los jugadores para que avance la animacion
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null) {
                    player.update();
                }
            }

            // Verificar si todas las animaciones terminaron
            boolean allAnimationsComplete = true;
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null && !player.isDeathAnimationComplete()) {
                    allAnimationsComplete = false;
                    break;
                }
            }

            if (allAnimationsComplete) {
                stateManager.showDeathScreen();
                System.out.println("Todas las animaciones de muerte completadas - mostrando pantalla");
            }
        }

        // CRITICO: Actualizar jugadores durante animacion de victoria
        if (stateManager.getCurrentState() == GameStateManager.GameState.VICTORY_ANIMATION) {
            // Actualizar solo los jugadores para que avance la animacion
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null) {
                    player.update();
                }
            }
        }
    }

    /**
     * Verifica las condiciones de victoria y derrota
     */
    private void checkGameConditions() {
        if (currentLevel.isLevelComplete() && stateManager.isPlaying()) {
            stateManager.triggerVictory();
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null && !playerManager.isPlayerDead(i)) {
                    player.startWinAnimation();
                }
            }
            timer.pause();
            return;
        }

        boolean collision = collisionManager.checkAllPlayerCollisions(playerManager);

        if (collision) {
            for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                Player player = playerManager.getPlayer(i);
                if (player != null) {
                    Point playerGrid = currentLevel.getGameMap().getGrid().pixelToGrid(
                            player.getX(), player.getY()
                    );

                    boolean hitEnemy = currentLevel.getEnemyManager().checkCollisionWithPlayer(player);
                    boolean hitFire = currentLevel.getGameMap().hasLitCampfireAt(
                            playerGrid.x, playerGrid.y
                    );
                    boolean hitCactus = currentLevel.getFruitManager().checkDangerousCactusCollision(player);

                    if (hitEnemy || hitFire || hitCactus) {
                        playerManager.markPlayerDead(i);
                    }
                }
            }

            if (playerManager.areAllPlayersDead() && stateManager.isPlaying()) {
                stateManager.triggerDeath();
                for (int i = 0; i < playerManager.getPlayerCount(); i++) {
                    Player player = playerManager.getPlayer(i);
                    if (player != null) {
                        player.startDeathAnimation();
                    }
                }
                timer.pause();
            }
        }
    }

    /**
     * Maneja eventos de teclado
     */
    public void handleKeyPress(KeyEvent e, boolean pressed) {
        if (!initialized || inputHandler == null) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && pressed) {
            pauseButton.toggle();
            if (pauseButton.isPaused()) {
                timer.pause();
            } else {
                timer.resume();
            }
            return;
        }

        if (pauseButton.isPaused()) {
            return;
        }

        inputHandler.handleKeyPress(e, pressed);
    }

    /**
     * Maneja clics del mouse
     */
    public void handleMouseClick(Point clickPos) {
        if (!initialized) {
            return;
        }

        if (pauseButton.handleClick(clickPos)) {
            if (pauseButton.isPaused()) {
                timer.pause();
            } else {
                timer.resume();
            }
        }
    }

    /**
     * Actualiza el estado de hover del boton de pausa
     */
    public void updatePauseButtonHover(Point mousePos) {
        if (initialized && pauseButton != null) {
            pauseButton.updateHover(mousePos);
        }
    }

    /**
     * Reinicia el nivel actual
     */
    // En GameController.java

    public void restartLevel() {
        if (!initialized) {
            return;
        }

        System.out.println("=== REINICIANDO NIVEL ===");

        // USAR configuracion original guardada
        if (originalLevelConfig == null) {
            System.out.println("ERROR: No hay configuracion original guardada!");
            return;
        }

        if (player1Name == null) {
            System.out.println("ERROR: No hay datos de jugador 1!");
            return;
        }

        System.out.println("Usando configuracion original:");
        System.out.println("  Bananas: " + originalLevelConfig.getBananaCount());
        System.out.println("  Uvas: " + originalLevelConfig.getGrapeCount());
        System.out.println("  Cerezas: " + originalLevelConfig.getCherryCount());
        System.out.println("  Pinas: " + originalLevelConfig.getPineappleCount());
        System.out.println("  Cactus: " + originalLevelConfig.getCactusCount());

        // Limpiar todo
        playerManager.clear();
        currentLevel.clear();

        // Recrear el nivel con la configuracion ORIGINAL
        currentLevel = LevelFactory.createLevel(1, originalLevelConfig);
        currentLevel.initialize();

        // Recrear jugador 1 con datos guardados
        String p1FlavorCapitalized = capitalizeFirstLetter(player1Flavor);
        String p1SpritePath = "Images/IceCreams/" + p1FlavorCapitalized;

        boolean player1IsAI = player1Name != null && player1Name.startsWith("IA-");
        Player player1;
        if (player1IsAI) {
            String player1AIType = player1Name.substring(3).toLowerCase();
            player1 = AIPlayerFactory.createAIFromDifficulty(
                    player1AIType, 210, 210, p1SpritePath,
                    currentLevel.getGameMap().getGrid(),
                    currentLevel.getPathFinder()
            );
        } else {
            player1 = new IceCreamPlayer(210, 210, p1SpritePath);
        }

        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );
        playerManager.registerPlayer(player1, player1Name, player1Flavor, player1IsAI);
        currentLevel.getFruitManager().registerPlayer(player1);


        player1.setCollisionDetector(
                currentLevel.getCollisionDetector(),
                currentLevel.getGameMap().getGrid()
        );


        // Recrear jugador 2 si existe
        if (hasTwoPlayers) {
            String p2FlavorCapitalized = capitalizeFirstLetter(player2Flavor);
            String p2SpritePath = "Images/IceCreams/" + p2FlavorCapitalized;
            Player player2;

            if (player2IsAI && player2AIType != null) {
                player2 = AIPlayerFactory.createAIFromDifficulty(
                        player2AIType.toLowerCase(),
                        546, 546,
                        p2SpritePath,
                        currentLevel.getGameMap().getGrid(),
                        currentLevel.getPathFinder()
                );
            } else {
                player2 = new IceCreamPlayer(546, 546, p2SpritePath);
            }

            player2.setCollisionDetector(
                    currentLevel.getCollisionDetector(),
                    currentLevel.getGameMap().getGrid()
            );

            playerManager.registerPlayer(player2, player2Name, player2Flavor, player2IsAI);
            currentLevel.getFruitManager().registerPlayer(player2);
        }

        // Inicializar squids con los jugadores
        currentLevel.initializeSquids(playerManager);

        // Establecer FruitManager en playerManager
        playerManager.setFruitManager(currentLevel.getFruitManager());

        // Recrear InputHandler y CollisionManager
        inputHandler = new InputHandler(playerManager, currentLevel.getGameMap());

        collisionManager = new CollisionManager(
                currentLevel.getFruitManager(),
                currentLevel.getEnemyManager(),
                currentLevel.getGameMap()
        );

        // Reiniciar managers de estado
        stateManager.reset();
        timer.reset();
        pauseButton.resume();

        System.out.println("=== REINICIO COMPLETADO ===");
    }
    /**
     * Carga un juego desde un archivo de guardado
     */
    public boolean loadGame(String filename) {
        GameSaveData saveData = GameSaveData.loadFromFile(filename);
        if (saveData == null) {
            return false;
        }

        initializeFromSaveData(saveData);
        System.out.println("GameController: Juego cargado desde " + filename);
        return true;
    }

    /**
     * Guarda el juego actual
     */
    /**
     * Guarda el juego actual
     */
    public boolean saveGame(String filename) {
        if (!initialized) {
            return false;
        }

        GameSaveData saveData = new GameSaveData();

        // Guardar modo de juego
        saveData.setTwoPlayerMode(playerManager.isTwoPlayerMode());

        // Guardar jugador 1
        Player player1 = playerManager.getPlayer(0);
        PlayerManager.PlayerInfo info1 = playerManager.getPlayerInfo(0);

        if (player1 != null && info1 != null) {
            Point gridPos = currentLevel.getGameMap().getGrid().pixelToGrid(
                    player1.getX(), player1.getY());
            saveData.setPlayerName(info1.getName());
            saveData.setPlayerFlavor(info1.getFlavor());
            saveData.setPlayerX(gridPos.x);
            saveData.setPlayerY(gridPos.y);
            saveData.setScore(currentLevel.getFruitManager().getScore(player1));
            saveData.setCollectedFruits(currentLevel.getFruitManager().getCollectedCount(player1));
        }

        // Guardar jugador 2 si existe
        // Guardar jugador 2 si existe
        if (playerManager.isTwoPlayerMode()) {
            Player player2 = playerManager.getPlayer(1);
            PlayerManager.PlayerInfo info2 = playerManager.getPlayerInfo(1);

            if (player2 != null && info2 != null) {
                Point gridPos2 = currentLevel.getGameMap().getGrid().pixelToGrid(
                        player2.getX(), player2.getY());
                saveData.setPlayer2Name(info2.getName());
                saveData.setPlayer2Flavor(info2.getFlavor());
                saveData.setPlayer2GridX(gridPos2.x);
                saveData.setPlayer2GridY(gridPos2.y);
                saveData.setPlayer2Score(currentLevel.getFruitManager().getScore(player2));
                saveData.setPlayer2CollectedFruits(
                        currentLevel.getFruitManager().getCollectedCount(player2));

                // NUEVO: Guardar si es IA y su tipo
                saveData.setPlayer2IsAI(info2.isAI());
                if (info2.isAI() && player2 instanceof AIPlayer) {
                    AIPlayer aiPlayer = (AIPlayer) player2;
                    saveData.setPlayer2AIType(aiPlayer.getAIType().toString());
                    System.out.println("Guardando IA tipo: " + aiPlayer.getAIType());
                }
            }
        }

        saveData.setRemainingTime(timer.getRemainingTimeMs());
        saveData.setTotalFruits(currentLevel.getFruitManager().getTotalCount());

        // Guardar estado de oleadas
        saveData.setBananasCompleted(currentLevel.getFruitManager().areBananasCompleted());
        saveData.setGrapesSpawned(currentLevel.getFruitManager().areGrapesSpawned());
        saveData.setGrapesCompleted(currentLevel.getFruitManager().areGrapesCompleted());
        saveData.setCherriesSpawned(currentLevel.getFruitManager().areCherriesSpawned());
        saveData.setCherriesCompleted(currentLevel.getFruitManager().areCherriesCompleted());
        saveData.setPineapplesSpawned(currentLevel.getFruitManager().arePineapplesSpawned());
        saveData.setPineapplesCompleted(currentLevel.getFruitManager().arePineapplesCompleted());
        saveData.setCactusesSpawned(currentLevel.getFruitManager().areCactusesSpawned());
        saveData.setCactusesCompleted(currentLevel.getFruitManager().areCactusesCompleted());

        // Guardar frutas
        for (Fruit fruit : currentLevel.getFruitManager().getFruits()) {
            Point fruitPos = fruit.getGridPosition();
            String fruitType = fruit.getType().toString();
            GameSaveData.FruitData fruitData = new GameSaveData.FruitData(
                    fruitPos.x, fruitPos.y, fruit.isCollected(), fruitType
            );
            saveData.getFruitsData().add(fruitData);
        }

        // Guardar enemigos
        for (Enemy enemy : currentLevel.getEnemyManager().getEnemies()) {
            if (enemy.isActive()) {
                Point enemyPos = enemy.getGridPosition();
                String enemyType = enemy.getType().toString();
                GameSaveData.EnemyData enemyData = new GameSaveData.EnemyData(
                        enemyPos.x, enemyPos.y, true, enemyType, "DOWN"
                );
                saveData.getEnemiesData().add(enemyData);
            }
        }

        GameMap gameMap = currentLevel.getGameMap();
        GridSystem grid = gameMap.getGrid();

        // Guardar bloques de hielo
        for (Block block : gameMap.getBlocks()) {
            if (block.getType() == Block.BlockType.ICE && block.isSolid()) {
                Point blockPos = grid.pixelToGrid(block.getX(), block.getY());
                GameSaveData.IceBlockData blockData = new GameSaveData.IceBlockData(
                        blockPos.x, blockPos.y, block.getType().toString()
                );
                saveData.getIceBlocksData().add(blockData);
            }
        }

        // Guardar fogatas
        for (FireBlock campfire : gameMap.getCampfires()) {
            Campfire cf = (Campfire) campfire;
            Point campfirePos = cf.getGridPosition();
            GameSaveData.CampfireData campfireData = new GameSaveData.CampfireData(
                    campfirePos.x, campfirePos.y,
                    campfire.isLit(),
                    campfire.hasIceAbove()
            );
            saveData.getCampfiresData().add(campfireData);
        }

        // Guardar baldosas calientes
        for (Tile tile : gameMap.getTiles()) {
            if (tile.getType() == Tile.TileType.HOT_TILE) {
                Point tilePos = tile.getGridPosition();
                GameSaveData.HotTileData hotTileData = new GameSaveData.HotTileData(
                        tilePos.x, tilePos.y
                );
                saveData.getHotTilesData().add(hotTileData);
            }
        }

        // Guardar configuracion del nivel
        LevelConfiguration levelConfig = currentLevel.getConfiguration();
        GameSaveData.LevelConfigData configData = new GameSaveData.LevelConfigData(
                levelConfig.getBananaCount(),
                levelConfig.getGrapeCount(),
                levelConfig.getCherryCount(),
                levelConfig.getPineappleCount(),
                levelConfig.getCactusCount(),
                levelConfig.getTrollCount(),
                levelConfig.getSquidCount(),
                levelConfig.getPotCount(),
                levelConfig.getNarvalCount(),
                levelConfig.getIceBlockCount(),
                levelConfig.getFireBlockCount(),
                levelConfig.getHotTileCount()
        );
        saveData.setLevelConfig(configData);

        System.out.println("=== GUARDANDO PARTIDA ===");
        System.out.println("Modo 2 jugadores: " + saveData.isTwoPlayerMode());
        if (saveData.isTwoPlayerMode()) {
            System.out.println("Jugador 2: " + saveData.getPlayer2Name());
        }
        System.out.println("Bloques de hielo: " + saveData.getIceBlocksData().size());
        System.out.println("Fogatas: " + saveData.getCampfiresData().size());
        System.out.println("Baldosas calientes: " + saveData.getHotTilesData().size());
        System.out.println("Enemigos: " + saveData.getEnemiesData().size());
        System.out.println("Frutas: " + saveData.getFruitsData().size());

        return saveData.saveToFile(filename);
    }

    /**
     * Obtiene todos los elementos renderizables del juego
     * Presentation se encarga de dibujarlos
     */
    public RenderData getRenderData() {
        if (!initialized) {
            return new RenderData();
        }

        RenderData data = new RenderData();
        GameMap gameMap = currentLevel.getGameMap();

        data.bottomBlocks = gameMap.getBottomLayerBlocks();
        data.walls = gameMap.getWalls();
        data.topBlocks = gameMap.getTopLayerBlocks();
        data.tiles = gameMap.getTiles();
        data.campfires = gameMap.getCampfires();
        data.fruits = currentLevel.getFruitManager().getFruits();
        data.enemies = currentLevel.getEnemyManager().getEnemies();

        data.players = new java.util.ArrayList<>();
        for (int i = 0; i < playerManager.getPlayerCount(); i++) {
            Player player = playerManager.getPlayer(i);
            if (player != null) {
                data.players.add(player);
            }
        }

        return data;
    }

    /**
     * Clase interna para encapsular todos los datos de renderizado
     */
    public static class RenderData {
        public List<Block> bottomBlocks = new java.util.ArrayList<>();
        public List<Wall> walls = new java.util.ArrayList<>();
        public List<Block> topBlocks = new java.util.ArrayList<>();
        public List<Tile> tiles = new java.util.ArrayList<>();
        public List<FireBlock> campfires = new java.util.ArrayList<>();
        public List<Fruit> fruits = new java.util.ArrayList<>();
        public List<Enemy> enemies = new java.util.ArrayList<>();
        public List<Player> players = new java.util.ArrayList<>();
    }

    /**
     * Capitaliza la primera letra de un string
     * Ejemplo: "vanilla" -> "Vanilla"
     */
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Carga un juego desde un archivo y retorna un DTO con la informaciÃƒÂ³n
     * @param filename nombre del archivo sin extensiÃƒÂ³n
     * @return DTO con informaciÃƒÂ³n del save o null si falla
     */
    /**
     * Carga un juego desde un archivo y retorna la informaciÃƒÂ³n bÃƒÂ¡sica
     * @param filename nombre del archivo sin extensiÃƒÂ³n
     * @return true si se cargÃƒÂ³ correctamente
     */
    public boolean loadGameFromFile(String filename) {
        GameSaveData saveData = GameSaveData.loadFromFile(filename);
        if (saveData == null) {
            return false;
        }

        // Inicializar directamente con los datos cargados
        initializeFromSaveData(saveData);
        return true;
    }

    /**
     * Obtiene informaciÃƒÂ³n del save cargado para mostrar en UI
     * Solo llamar DESPUÃƒâ€°S de loadGameFromFile()
     */
    public String getLoadedPlayerName() {
        if (!initialized || playerManager == null || playerManager.getPlayerCount() == 0) {
            return "";
        }
        return getPlayerName(0);
    }

    public String getLoadedPlayerFlavor() {
        if (!initialized || playerManager == null || playerManager.getPlayerCount() == 0) {
            return "";
        }
        return getPlayerFlavor(0);
    }

    public int getLoadedScore() {
        return getTotalScore();
    }

    public int getLoadedCollectedFruits() {
        return getCollectedFruitsCount();
    }

    public int getLoadedTotalFruits() {
        return getTotalFruitsCount();
    }

    /**
     * Inicializa el juego desde un DTO de guardado
     * @param dto DTO con informaciÃƒÂ³n del save
     */
    public void initializeFromSaveDTO(SaveGameDTO dto) {
        if (dto == null) {
            System.out.println("Error: SaveGameDTO es null");
            return;
        }

        // Convertir DTO a GameSaveData interno
        GameSaveData saveData = new GameSaveData();
        saveData.setPlayerName(dto.getPlayerName());
        saveData.setPlayerFlavor(dto.getPlayerFlavor());
        saveData.setPlayerX(dto.getPlayerGridX());
        saveData.setPlayerY(dto.getPlayerGridY());
        saveData.setScore(dto.getScore());
        saveData.setCollectedFruits(dto.getCollectedFruits());
        saveData.setTotalFruits(dto.getTotalFruits());
        saveData.setRemainingTime(dto.getRemainingTimeMs());

        saveData.setBananasCompleted(dto.isBananasCompleted());
        saveData.setGrapesSpawned(dto.isGrapesSpawned());
        saveData.setGrapesCompleted(dto.isGrapesCompleted());
        saveData.setCherriesSpawned(dto.isCherriesSpawned());
        saveData.setCherriesCompleted(dto.isCherriesCompleted());
        saveData.setPineapplesSpawned(dto.isPineapplesSpawned());
        saveData.setPineapplesCompleted(dto.isPineapplesCompleted());
        saveData.setCactusesSpawned(dto.isCactusesSpawned());
        saveData.setCactusesCompleted(dto.isCactusesCompleted());

        // Convertir LevelConfigDTO a LevelConfigData
        if (dto.getLevelConfig() != null) {
            LevelConfigDTO configDTO = dto.getLevelConfig();
            GameSaveData.LevelConfigData configData = new GameSaveData.LevelConfigData(
                    configDTO.getBananaCount(),
                    configDTO.getGrapeCount(),
                    configDTO.getCherryCount(),
                    configDTO.getPineappleCount(),
                    configDTO.getCactusCount(),
                    configDTO.getTrollCount(),
                    configDTO.getSquidCount(),
                    configDTO.getPotCount(),
                    configDTO.getNarvalCount(),
                    configDTO.getIceBlockCount(),
                    configDTO.getFireBlockCount(),
                    configDTO.getHotTileCount()
            );
            saveData.setLevelConfig(configData);
        }

        // Usar el mÃƒÂ©todo existente
        initializeFromSaveData(saveData);
    }

    /**
     * Crea una configuraciÃƒÂ³n de nivel desde GameSessionData
     * @param bananaCount cantidad de bananas
     * @param grapeCount cantidad de uvas
     * @param cherryCount cantidad de cerezas
     * @param pineappleCount cantidad de pinas
     * @param cactusCount cantidad de cactus
     * @param trollCount cantidad de trolls
     * @param squidCount cantidad de squids
     * @param potCount cantidad de pots
     * @param narvalCount cantidad de narvales
     * @param iceBlockCount cantidad de bloques de hielo
     * @param fireBlockCount cantidad de fogatas
     * @param hotTileCount cantidad de baldosas calientes
     * @return configuraciÃƒÂ³n del nivel
     */
    public LevelConfiguration createLevelConfigFromCounts(
            int bananaCount, int grapeCount, int cherryCount,
            int pineappleCount, int cactusCount, int trollCount,
            int squidCount, int potCount, int narvalCount,
            int iceBlockCount, int fireBlockCount, int hotTileCount) {

        return new LevelConfiguration.Builder()
                .setBananaCount(bananaCount)
                .setGrapeCount(grapeCount)
                .setCherryCount(cherryCount)
                .setPineappleCount(pineappleCount)
                .setCactusCount(cactusCount)
                .setTrollCount(trollCount)
                .setSquidCount(squidCount)
                .setPotCount(potCount)
                .setNarvalCount(narvalCount)
                .setIceBlockCount(iceBlockCount)
                .setFireBlockCount(fireBlockCount)
                .setHotTileCount(hotTileCount)
                .build();
    }

    // ==================== GETTERS PARA PRESENTATION ====================

    public boolean isInitialized() {
        return initialized;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    // Informacion de frutas
    public int getTotalScore() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getFruitManager().getScore();
    }

    public int getPlayerScore(int playerIndex) {
        if (!initialized || currentLevel == null) return 0;
        Player player = playerManager.getPlayer(playerIndex);
        if (player == null) return 0;
        return currentLevel.getFruitManager().getScore(player);
    }

    public int getCollectedFruitsCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getFruitManager().getCollectedCount();
    }

    public int getTotalFruitsCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getFruitManager().getTotalCount();
    }

    // Informacion de enemigos
    public int getActiveEnemyCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getEnemyManager().getActiveEnemyCount();
    }

    public int getActiveTrollCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getEnemyManager().getActiveTrollCount();
    }

    public int getActiveSquidCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getEnemyManager().getActiveSquidCount();
    }

    public int getActivePotCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getEnemyManager().getActivePotCount();
    }

    public int getActiveNarvalCount() {
        if (!initialized || currentLevel == null) return 0;
        return currentLevel.getEnemyManager().getActiveNarvalCount();
    }

    // Informacion de jugadores
    public int getPlayerCount() {
        if (playerManager == null) return 0;
        return playerManager.getPlayerCount();
    }

    public boolean isTwoPlayerMode() {
        if (playerManager == null) return false;
        return playerManager.isTwoPlayerMode();
    }

    public boolean hasAIPlayer() {
        if (playerManager == null) return false;
        return playerManager.hasAIPlayer();
    }

    public boolean isPlayerDead(int playerIndex) {
        if (playerManager == null) return false;
        return playerManager.isPlayerDead(playerIndex);
    }

    public boolean areAllPlayersDead() {
        if (playerManager == null) return false;
        return playerManager.areAllPlayersDead();
    }

    public String getPlayerName(int playerIndex) {
        if (playerManager == null) return "";
        PlayerManager.PlayerInfo info = playerManager.getPlayerInfo(playerIndex);
        return info != null ? info.getName() : "";
    }

    public String getPlayerFlavor(int playerIndex) {
        if (playerManager == null) return "";
        PlayerManager.PlayerInfo info = playerManager.getPlayerInfo(playerIndex);
        return info != null ? info.getFlavor() : "";
    }

    // Informacion del estado del juego
    public boolean isPaused() {
        return pauseButton != null && pauseButton.isPaused();
    }

    public boolean isPlaying() {
        return stateManager != null && stateManager.isPlaying();
    }

    public boolean shouldShowVictoryScreen() {
        return stateManager != null && stateManager.shouldShowVictoryScreen();
    }

    public boolean shouldShowDeathScreen() {
        return stateManager != null && stateManager.shouldShowDeathScreen();
    }

    public boolean isLevelComplete() {
        if (!initialized || currentLevel == null) return false;
        return currentLevel.isLevelComplete();
    }

    public int getRemainingMinutes() {
        if (timer == null) return 0;
        return timer.getRemainingMinutes();
    }

    public int getRemainingSeconds() {
        if (timer == null) return 0;
        return timer.getRemainingSeconds();
    }

    public boolean isTimeUp() {
        return timer != null && timer.isTimeUp();
    }

    // Informacion del boton de pausa
    public java.awt.Image getPauseButtonImage() {
        if (pauseButton == null) return null;
        return pauseButton.getCurrentImage();
    }

    public int getPauseButtonX() {
        if (pauseButton == null) return 0;
        return pauseButton.getX();
    }

    public int getPauseButtonY() {
        if (pauseButton == null) return 0;
        return pauseButton.getY();
    }

    public int getPauseButtonWidth() {
        if (pauseButton == null) return 0;
        return pauseButton.getWidth();
    }

    public int getPauseButtonHeight() {
        if (pauseButton == null) return 0;
        return pauseButton.getHeight();
    }

}