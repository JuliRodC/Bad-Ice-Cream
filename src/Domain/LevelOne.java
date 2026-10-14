package Domain;

/**
 * Implementacion del primer nivel del juego
 * Sistema de oleadas: Banana -> Uva -> Cereza -> Pina -> Cactus
 */
public class LevelOne implements GameLevel {
    private final LevelConfiguration config;
    private final int levelNumber = 1;

    private GameMap gameMap;
    private FruitManager fruitManager;
    private EnemyManager enemyManager;
    private CollisionDetector collisionDetector;
    private PathFinder pathFinder;

    private boolean initialized;

    private TargetSelector targetSelector;

    public LevelOne(LevelConfiguration config) {
        this.config = config;
        this.initialized = false;
    }

    public void initialize() {
        if (initialized) {
            System.out.println("Nivel 1 ya esta inicializado");
            return;
        }

        System.out.println("Inicializando Nivel 1...");

        gameMap = new GameMap(
                config.getMapWidth(),
                config.getMapHeight(),
                config.getCellSize()
        );
        gameMap.initializeBorderWalls();
        gameMap.createInitialIceLayout();

        if (config.getHotTileCount() > 0) {
            gameMap.createRandomHotTiles(config.getHotTileCount());
        }

        if (config.getFireBlockCount() > 0) {
            gameMap.createRandomCampfires(config.getFireBlockCount());
        }

        collisionDetector = new GameCollisionDetector(gameMap, gameMap.getGrid());
        pathFinder = new PathFinder(gameMap.getGrid(), collisionDetector, gameMap);

        fruitManager = new FruitManager(gameMap.getGrid());
        fruitManager.setGameMap(gameMap);

        fruitManager.configureWaveCounts(
                config.getBananaCount(),
                config.getGrapeCount(),
                config.getCherryCount(),
                config.getPineappleCount(),
                config.getCactusCount()
        );

        fruitManager.createLevelBananas(config.getBananaCount());

        enemyManager = new EnemyManager(gameMap.getGrid(), collisionDetector);
        enemyManager.setPathFinder(pathFinder);
        enemyManager.setGameMap(gameMap);
        enemyManager.createTrollsInCorners(config.getTrollCount());

        initialized = true;

        System.out.println("Nivel 1 inicializado correctamente");
        System.out.println("  Bananas: " + config.getBananaCount());
        System.out.println("  Uvas (spawn posterior): " + config.getGrapeCount());
        System.out.println("  Cerezas (spawn posterior): " + config.getCherryCount());
        System.out.println("  Pinas (spawn posterior): " + config.getPineappleCount());
        System.out.println("  Cactus (spawn posterior): " + config.getCactusCount());
        System.out.println("  Trolls: " + config.getTrollCount());
        System.out.println("  Squids: " + config.getSquidCount());
        System.out.println("  Narvales: " + config.getNarvalCount());
        System.out.println("  Bloques de hielo: " + config.getIceBlockCount());
        System.out.println("  Baldosas calientes: " + config.getHotTileCount());
        System.out.println("  Fogatas: " + config.getFireBlockCount());
        System.out.println("  Pots: " + config.getPotCount());
    }


    public void update() {
        if (!initialized) {
            System.out.println("Advertencia: Intentando actualizar nivel no inicializado");
            return;
        }

        gameMap.update();
        fruitManager.update();
        enemyManager.update();

        if (fruitManager.areBananasCompleted() && !fruitManager.areGrapesSpawned()) {
            spawnGrapes();
        }

        if (fruitManager.areGrapesCompleted() && !fruitManager.areCherriesSpawned()) {
            spawnCherries();
        }

        if (fruitManager.areCherriesCompleted() && !fruitManager.arePineapplesSpawned()) {
            spawnPineapples();
        }

        if (fruitManager.arePineapplesCompleted() && !fruitManager.areCactusesSpawned()) {
            spawnCactuses();
        }
    }

    public void initializeSquids(PlayerManager playerManager) {
        // Crear el selector de objetivos
        targetSelector = new TargetSelector(playerManager, gameMap.getGrid());
        enemyManager.setTargetSelector(targetSelector);

        if (enemyManager != null && config.getSquidCount() > 0) {
            enemyManager.createSquidsInMap(config.getSquidCount());
        }
        if (enemyManager != null && config.getPotCount() > 0) {
            enemyManager.createPotsInMap(config.getPotCount());
        }
        if (enemyManager != null && config.getNarvalCount() > 0) {
            enemyManager.createNarvalsInMap(config.getNarvalCount());
        }
    }



    public void reset() {
        System.out.println("Reiniciando Nivel 1 con configuracion original...");

        // Limpiar todo
        clear();

        // Re-inicializar con la MISMA configuracion (config)
        gameMap = new GameMap(
                config.getMapWidth(),
                config.getMapHeight(),
                config.getCellSize()
        );
        gameMap.initializeBorderWalls();
        gameMap.createInitialIceLayout();

        if (config.getHotTileCount() > 0) {
            gameMap.createRandomHotTiles(config.getHotTileCount());
        }

        if (config.getFireBlockCount() > 0) {
            gameMap.createRandomCampfires(config.getFireBlockCount());
        }

        collisionDetector = new GameCollisionDetector(gameMap, gameMap.getGrid());
        pathFinder = new PathFinder(gameMap.getGrid(), collisionDetector, gameMap);

        fruitManager = new FruitManager(gameMap.getGrid());
        fruitManager.setGameMap(gameMap);

        // CRITICO: Configurar contadores de oleadas ANTES de crear frutas
        fruitManager.configureWaveCounts(
                config.getBananaCount(),
                config.getGrapeCount(),
                config.getCherryCount(),
                config.getPineappleCount(),
                config.getCactusCount()
        );

        // Crear solo las bananas iniciales
        fruitManager.createLevelBananas(config.getBananaCount());

        enemyManager = new EnemyManager(gameMap.getGrid(), collisionDetector);
        enemyManager.setPathFinder(pathFinder);
        enemyManager.setGameMap(gameMap);
        enemyManager.createTrollsInCorners(config.getTrollCount());

        initialized = true;

        System.out.println("Nivel 1 reiniciado correctamente con configuracion original");
        System.out.println("  Bananas totales configuradas: " + config.getBananaCount());
        System.out.println("  Uvas totales configuradas: " + config.getGrapeCount());
        System.out.println("  Cerezas totales configuradas: " + config.getCherryCount());
        System.out.println("  Pinas totales configuradas: " + config.getPineappleCount());
        System.out.println("  Cactus totales configurados: " + config.getCactusCount());
        System.out.println("  Trolls: " + config.getTrollCount());
        System.out.println("  Squids: " + config.getSquidCount());
        System.out.println("  Pots: " + config.getPotCount());
        System.out.println("  Narvales: " + config.getNarvalCount());
        System.out.println("  Bloques de hielo: " + config.getIceBlockCount());
        System.out.println("  Baldosas calientes: " + config.getHotTileCount());
        System.out.println("  Fogatas: " + config.getFireBlockCount());
    }

    public void clear() {
        if (gameMap != null) {
            gameMap.clear();
        }
        if (fruitManager != null) {
            fruitManager.clear();
        }
        if (enemyManager != null) {
            enemyManager.clear();
        }
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public FruitManager getFruitManager() {
        return fruitManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    public PathFinder getPathFinder() {
        return pathFinder;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public LevelConfiguration getConfiguration() {
        return config;
    }

    public boolean areBananasCompleted() {
        return fruitManager != null && fruitManager.areBananasCompleted();
    }

    public void spawnGrapes() {
        if (config.getGrapeCount() > 0) {
            System.out.println("Spawneando uvas del Nivel 1...");
            fruitManager.spawnGrapes(config.getGrapeCount());
        }
    }

    public void spawnCherries() {
        if (config.getCherryCount() > 0) {
            System.out.println("Spawneando cerezas del Nivel 1...");
            fruitManager.spawnCherries(config.getCherryCount());
        }
    }

    public void spawnPineapples() {
        if (config.getPineappleCount() > 0) {
            System.out.println("Spawneando pinas del Nivel 1...");
            fruitManager.spawnPineapples(config.getPineappleCount());
        }
    }

    public void spawnCactuses() {
        if (config.getCactusCount() > 0) {
            System.out.println("Spawneando cactus del Nivel 1...");
            fruitManager.spawnCactuses(config.getCactusCount());
        }
    }

    public boolean isLevelComplete() {
        return fruitManager != null && fruitManager.allFruitsCollected();
    }
}