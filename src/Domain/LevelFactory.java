package Domain;

/**
 * Fabrica para crear niveles del juego
 * Utiliza polimorfismo para crear diferentes niveles
 */
public class LevelFactory {

    /**
     * Crea un nivel segun el numero especificado
     * @param levelNumber numero del nivel (1, 2, 3, etc)
     * @param config configuracion del nivel
     * @return instancia del nivel creado
     */
    public static GameLevel createLevel(int levelNumber, LevelConfiguration config) {
        switch (levelNumber) {
            case 1:
                return new LevelOne(config);
            //case 2:
                //return new LevelTwo(config);
            //case 3:
            //    return new LevelThree(config);
            //case 4:
            //    return new LevelFour(config);
            default:
                System.out.println("Nivel " + levelNumber + " no existe, creando Nivel 1");
                return new LevelOne(config);
        }
    }

    /**
     * Crea un nivel con configuracion por defecto
     * @param levelNumber numero del nivel
     * @return instancia del nivel con configuracion por defecto
     */
    public static GameLevel createLevelWithDefaults(int levelNumber) {
        LevelConfiguration config = getDefaultConfigForLevel(levelNumber);
        return createLevel(levelNumber, config);
    }

    /**
     * Obtiene la configuracion por defecto para un nivel especifico
     * @param levelNumber numero del nivel
     * @return configuracion por defecto del nivel
     */
    private static LevelConfiguration getDefaultConfigForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1:
                return new LevelConfiguration.Builder()
                        .setBananaCount(20)
                        .setGrapeCount(16)
                        .setTrollCount(2)
                        .setIceBlockCount(80)
                        .build();

            case 2:
                return new LevelConfiguration.Builder()
                        .setBananaCount(25)
                        .setGrapeCount(20)
                        .setTrollCount(3)
                        .setIceBlockCount(90)
                        .build();

            case 3:
                return new LevelConfiguration.Builder()
                        .setBananaCount(30)
                        .setGrapeCount(25)
                        .setTrollCount(4)
                        .setIceBlockCount(100)
                        .build();

            case 4:
                return new LevelConfiguration.Builder()
                        .setBananaCount(35)
                        .setGrapeCount(30)
                        .setTrollCount(5)
                        .setIceBlockCount(110)
                        .build();

            default:
                return LevelConfiguration.createDefaultLevel1();
        }
    }

    /**
     * Crea un nivel desde datos guardados
     * @param saveData datos de la partida guardada
     * @return instancia del nivel reconstruido
     */
    public static GameLevel createLevelFromSaveData(GameSaveData saveData) {
        // Por ahora solo soporta nivel 1 desde save data
        // Cuando agregues mas niveles, necesitaras guardar el numero de nivel en GameSaveData

        LevelConfiguration config = new LevelConfiguration.Builder()
                .setBananaCount(saveData.getFruitsData().size()) // Aproximado
                .setGrapeCount(16) // Por defecto
                .setTrollCount(saveData.getEnemiesData().size())
                .setIceBlockCount(saveData.getIceBlocksData().size())
                .build();

        GameLevel level = new LevelOne(config);

        // El nivel debera ser inicializado y luego reconstruido desde saveData
        // Esto se hara en GameScreen

        return level;
    }

    /**
     * Verifica si un numero de nivel es valido
     * @param levelNumber numero a verificar
     * @return true si el nivel existe
     */
    public static boolean isValidLevel(int levelNumber) {
        return levelNumber >= 1 && levelNumber <= 4;
    }

    /**
     * Obtiene el numero total de niveles disponibles
     * @return cantidad de niveles
     */
    public static int getTotalLevels() {
        return 4;
    }
}