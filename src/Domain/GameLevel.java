package Domain;

/**
 * Interfaz que define el comportamiento de un nivel del juego
 * Cada nivel implementa esta interfaz y define su propia configuracion
 */
public interface GameLevel {
    /**
     * Inicializa el nivel creando el mapa, frutas y enemigos
     */
    void initialize();

    /**
     * Inicializa enemigos que requieren un jugador objetivo
     */
    void initializeSquids(PlayerManager playerManager);

    /**
     * Actualiza el estado del nivel (mapa, frutas, enemigos)
     */
    void update();

    /**
     * Reinicia el nivel a su estado inicial
     */
    void reset();

    /**
     * Limpia todos los recursos del nivel
     */
    void clear();

    /**
     * Obtiene el mapa del nivel
     */
    GameMap getGameMap();

    /**
     * Obtiene el gestor de frutas del nivel
     */
    FruitManager getFruitManager();

    /**
     * Obtiene el gestor de enemigos del nivel
     */
    EnemyManager getEnemyManager();

    /**
     * Obtiene el detector de colisiones del nivel
     */
    CollisionDetector getCollisionDetector();

    /**
     * Obtiene el PathFinder del nivel (para IA)
     */
    PathFinder getPathFinder();

    /**
     * Obtiene el numero de nivel
     */
    int getLevelNumber();

    /**
     * Obtiene la configuracion del nivel
     */
    LevelConfiguration getConfiguration();

    /**
     * Verifica si todas las bananas han sido recolectadas
     */
    boolean areBananasCompleted();

    /**
     * Activa el spawn de uvas (llamar cuando se recolecten todas las bananas)
     */
    void spawnGrapes();

    /**
     * Verifica si todas las frutas han sido recolectadas
     */
    boolean isLevelComplete();
}