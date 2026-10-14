package Domain;

/**
 * Fabrica para crear diferentes tipos de jugadores IA
 * Facilita agregar nuevos tipos de IA en el futuro
 */
public class AIPlayerFactory {

    public static AIPlayer createAI(AIPlayer.AIType aiType,
                                    int startX, int startY,
                                    String spriteBasePath,
                                    GridSystem grid,
                                    PathFinder pathFinder) {
        switch (aiType) {
            case HUNGRY:
                return new HungryAIPlayer(startX, startY, spriteBasePath, grid, pathFinder);

            case FEARFUL:
                return new FearfulAIPlayer(startX, startY, spriteBasePath, grid, pathFinder);

            case EXPERT:
                return new ExpertAIPlayer(startX, startY, spriteBasePath, grid, pathFinder);

            default:
                return new HungryAIPlayer(startX, startY, spriteBasePath, grid, pathFinder);
        }
    }

    public static AIPlayer createAIFromDifficulty(String difficulty,
                                                  int startX, int startY,
                                                  String spriteBasePath,
                                                  GridSystem grid,
                                                  PathFinder pathFinder) {
        if (difficulty == null) {
            return createAI(AIPlayer.AIType.HUNGRY, startX, startY, spriteBasePath, grid, pathFinder);
        }

        switch (difficulty.toLowerCase()) {
            case "hungry":
                return createAI(AIPlayer.AIType.HUNGRY, startX, startY, spriteBasePath, grid, pathFinder);
            case "fearful":
                return createAI(AIPlayer.AIType.FEARFUL, startX, startY, spriteBasePath, grid, pathFinder);
            case "expert":
                return createAI(AIPlayer.AIType.EXPERT, startX, startY, spriteBasePath, grid, pathFinder);
            default:
                return createAI(AIPlayer.AIType.HUNGRY, startX, startY, spriteBasePath, grid, pathFinder);
        }
    }
}