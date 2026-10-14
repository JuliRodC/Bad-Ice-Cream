package Domain;

/**
 * Gestor del estado del juego
 * Controla los diferentes estados y transiciones del juego
 */
public class GameStateManager {
    private GameState currentState;
    private GameState previousState;

    private int winAnimationCounter;
    private static final int WIN_ANIMATION_DURATION = 120;

    private int deathAnimationCounter;
    private static final int DEATH_ANIMATION_DURATION = 60;

    private boolean deathAnimationTriggered;
    private boolean victoryAnimationTriggered;

    public enum GameState {
        PLAYING,
        PAUSED,
        DEATH_ANIMATION,
        DEATH_SCREEN,
        VICTORY_ANIMATION,
        VICTORY_SCREEN,
        TIME_UP
    }

    public GameStateManager() {
        this.currentState = GameState.PLAYING;
        this.previousState = null;
        this.winAnimationCounter = 0;
        this.deathAnimationCounter = 0;
        this.deathAnimationTriggered = false;
        this.victoryAnimationTriggered = false;
    }

    /**
     * Actualiza el estado del juego
     */
    public void update() {
        if (currentState == GameState.VICTORY_ANIMATION) {
            winAnimationCounter++;

            if (winAnimationCounter >= WIN_ANIMATION_DURATION) {
                transitionTo(GameState.VICTORY_SCREEN);
            }
        }

        if (currentState == GameState.DEATH_ANIMATION) {
            deathAnimationCounter++;

            // Transicion automatica a pantalla de muerte no se hace aqui
            // Se hace desde checkGameConditions cuando las animaciones terminen
        }
    }

    /**
     * Transiciona a un nuevo estado
     * @param newState nuevo estado
     */
    public void transitionTo(GameState newState) {
        if (currentState == newState) {
            return;
        }

        System.out.println("Transicion de estado: " + currentState + " -> " + newState);
        previousState = currentState;
        currentState = newState;

        onStateEnter(newState);
    }

    /**
     * Logica al entrar a un nuevo estado
     * @param state estado al que se entra
     */
    private void onStateEnter(GameState state) {
        switch (state) {
            case PLAYING:
                deathAnimationTriggered = false;
                victoryAnimationTriggered = false;
                deathAnimationCounter = 0;
                break;

            case PAUSED:
                System.out.println("Juego pausado");
                break;

            case DEATH_ANIMATION:
                deathAnimationTriggered = true;
                deathAnimationCounter = 0;
                System.out.println("Iniciando animacion de muerte");
                break;

            case DEATH_SCREEN:
                System.out.println("Mostrando pantalla de muerte");
                break;

            case VICTORY_ANIMATION:
                victoryAnimationTriggered = true;
                winAnimationCounter = 0;
                System.out.println("Iniciando animacion de victoria");
                break;

            case VICTORY_SCREEN:
                System.out.println("Mostrando pantalla de victoria");
                break;

            case TIME_UP:
                System.out.println("Tiempo agotado");
                break;
        }
    }

    /**
     * Pausa el juego
     */
    public void pause() {
        if (currentState == GameState.PLAYING) {
            transitionTo(GameState.PAUSED);
        }
    }

    /**
     * Reanuda el juego
     */
    public void resume() {
        if (currentState == GameState.PAUSED) {
            transitionTo(GameState.PLAYING);
        }
    }

    /**
     * Inicia la secuencia de muerte
     */
    public void triggerDeath() {
        if (currentState == GameState.PLAYING) {
            transitionTo(GameState.DEATH_ANIMATION);
        }
    }

    /**
     * Muestra la pantalla de muerte (despues de la animacion)
     */
    public void showDeathScreen() {
        if (currentState == GameState.DEATH_ANIMATION) {
            transitionTo(GameState.DEATH_SCREEN);
        }
    }

    /**
     * Inicia la secuencia de victoria
     */
    public void triggerVictory() {
        if (currentState == GameState.PLAYING) {
            transitionTo(GameState.VICTORY_ANIMATION);
        }
    }

    /**
     * Muestra la pantalla de victoria (despues de la animacion)
     */
    public void showVictoryScreen() {
        if (currentState == GameState.VICTORY_ANIMATION) {
            transitionTo(GameState.VICTORY_SCREEN);
        }
    }

    /**
     * Maneja el tiempo agotado
     */
    public void triggerTimeUp() {
        if (currentState == GameState.PLAYING) {
            transitionTo(GameState.TIME_UP);
            transitionTo(GameState.DEATH_ANIMATION);
        }
    }

    /**
     * Reinicia el estado a PLAYING
     */
    public void reset() {
        currentState = GameState.PLAYING;
        previousState = null;
        winAnimationCounter = 0;
        deathAnimationCounter = 0;
        deathAnimationTriggered = false;
        victoryAnimationTriggered = false;
        System.out.println("Estado del juego reiniciado");
    }

    // Getters
    public GameState getCurrentState() {
        return currentState;
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    public boolean isInDeathSequence() {
        return currentState == GameState.DEATH_ANIMATION ||
                currentState == GameState.DEATH_SCREEN;
    }

    public boolean isInVictorySequence() {
        return currentState == GameState.VICTORY_ANIMATION ||
                currentState == GameState.VICTORY_SCREEN;
    }

    public boolean shouldShowDeathScreen() {
        return currentState == GameState.DEATH_SCREEN;
    }

    public boolean shouldShowVictoryScreen() {
        return currentState == GameState.VICTORY_SCREEN;
    }

    public boolean canUpdateGame() {
        return currentState == GameState.PLAYING ||
                currentState == GameState.DEATH_ANIMATION ||
                currentState == GameState.VICTORY_ANIMATION;
    }

    public boolean isDeathAnimationTriggered() {
        return deathAnimationTriggered;
    }

    public boolean isVictoryAnimationTriggered() {
        return victoryAnimationTriggered;
    }
}