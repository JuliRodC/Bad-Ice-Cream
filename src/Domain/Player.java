package Domain;

import java.awt.Image;

/**
 * Interfaz que define el comportamiento de un jugador (polimorfismo)
 */
public interface Player {
    // Movimiento
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();
    void stopMoving();

    // Actualizacion del estado (se llama cada frame)
    void update();

    // Getters de posicion
    int getX();
    int getY();
    int getWidth();
    int getHeight();

    // Setter de posicion (para alinear a grid)
    void setPosition(int x, int y);

    // Movimiento por casillas (requiere GridSystem)
    void startMoveInDirection(Direction direction, GridSystem grid);

    // Direccion y animacion
    Direction getDirection();
    Image getCurrentSprite();

    // Estado del jugador
    boolean isMoving();
    void snapToNearestGrid(GridSystem grid);
    void startGridMovement(Direction direction, GridSystem grid);

    // Accion de crear hielo
    boolean startPutIceAnimation();
    boolean startBreakIceAnimation();
    boolean isPerformingAction();
    boolean isPerformingPutIceAction();
    boolean isPerformingBreakIceAction();
    boolean shouldCreateIce();
    boolean shouldDestroyIce();
    void resetIceCreation();
    void resetIceDestruction();

    // Animaciones de muerte y victoria
    void startDeathAnimation();
    boolean isDeathAnimationComplete();
    boolean isPerformingDeathAnimation();
    void startWinAnimation();
    boolean isPerformingWinAnimation();

    // Cooldown de acciones
    boolean canPerformAction();
    void performAction();
    boolean isOnCooldown();
    long getRemainingCooldown();

    void setCollisionDetector(CollisionDetector collisionDetector, GridSystem grid);

    // Enum para las direcciones
    enum Direction {
        UP, DOWN, LEFT, RIGHT,
        IDLE_DOWN, IDLE_UP, IDLE_LEFT, IDLE_RIGHT
    }
}