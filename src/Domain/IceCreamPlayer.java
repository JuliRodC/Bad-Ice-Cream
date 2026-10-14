package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Implementacion concreta del helado jugador con movimiento basado en cuadriculas.
 * Incluye animacion de muerte y animacion de victoria
 */
public class IceCreamPlayer implements Player {
    private int x, y;
    private int gridX, gridY;
    private int targetX, targetY;
    private int moveSpeed;


    private Player.Direction currentDirection;
    protected Player.Direction facingDirection;
    private Player.Direction pendingDirection;
    private boolean moving;
    private boolean transitioning;

    private CollisionDetector collisionDetector;
    private GridSystem grid;

    // Cooldown para acciones (crear/destruir hielo)
    private long lastActionTime;
    private static final long ACTION_COOLDOWN = 1000;

    // Animacion de caminar
    private int animationFrame;
    private int animationCounter;
    private final int ANIMATION_SPEED = 11;

    // Animacion de crear hielo
    private boolean performingPutIceAction;
    private int putIceFrame;
    private int putIceCounter;
    private final int PUT_ICE_ANIMATION_SPEED = 5;
    private Player.Direction putIceDirection;
    private boolean shouldCreateIceFlag;
    private static final int PUT_ICE_TRIGGER_FRAME = 6;

    // Animacion de romper hielo
    private boolean performingBreakIceAction;
    private int breakIceFrame;
    private int breakIceCounter;
    private final int BREAK_ICE_ANIMATION_SPEED = 4;
    private boolean shouldDestroyIceFlag;
    private static final int BREAK_ICE_TRIGGER_FRAME = 4;

    // Animacion de muerte
    private boolean performingDeathAnimation;
    private int deathFrame;
    private int deathCounter;
    private final int DEATH_ANIMATION_SPEED = 9;
    private boolean deathAnimationComplete;

    // Animacion de victoria
    private boolean performingWinAnimation;
    private int winFrame;
    private int winCounter;
    private final int WIN_ANIMATION_SPEED = 8;
    private static final int WIN_FRAMES = 6;

    private Map<Player.Direction, Image[]> sprites;
    private Map<Player.Direction, Image[]> putIceSprites;
    private Image[] breakIceSprites;
    private Image[] deathSprites;
    private Image[] winSprites;

    // Dimensiones de sprites
    private static final int WALK_SPRITE_WIDTH = 50;
    private static final int WALK_SPRITE_HEIGHT = 74;
    private static final int PUT_ICE_SPRITE_WIDTH = 68;
    private static final int PUT_ICE_SPRITE_HEIGHT = 92;
    private static final int BREAK_ICE_SPRITE_WIDTH = 50;
    private static final int BREAK_ICE_SPRITE_HEIGHT = 70;
    private static final int DEATH_SPRITE_WIDTH = 60;
    private static final int DEATH_SPRITE_HEIGHT = 80;
    private static final int WIN_SPRITE_WIDTH = 50;
    private static final int WIN_SPRITE_HEIGHT = 74;

    public IceCreamPlayer(int startX, int startY, String spriteBasePath) {
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;
        this.moveSpeed = 4;
        this.currentDirection = Player.Direction.IDLE_DOWN;
        this.facingDirection = Player.Direction.DOWN;
        this.pendingDirection = null;
        this.moving = false;
        this.transitioning = false;
        this.animationFrame = 0;
        this.animationCounter = 0;
        this.lastActionTime = 0;

        this.performingPutIceAction = false;
        this.putIceFrame = 0;
        this.putIceCounter = 0;
        this.shouldCreateIceFlag = false;

        this.performingBreakIceAction = false;
        this.breakIceFrame = 0;
        this.breakIceCounter = 0;
        this.shouldDestroyIceFlag = false;

        this.performingDeathAnimation = false;
        this.deathFrame = 0;
        this.deathCounter = 0;
        this.deathAnimationComplete = false;

        this.performingWinAnimation = false;
        this.winFrame = 0;
        this.winCounter = 0;
        loadAllSprites(spriteBasePath);
    }

    public void setCollisionDetector(CollisionDetector detector, GridSystem gridSystem) {
        this.collisionDetector = detector;
        this.grid = gridSystem;

        Point gridPos = grid.pixelToGrid(x, y);
        this.gridX = gridPos.x;
        this.gridY = gridPos.y;
        Point pixelPos = grid.gridToPixel(gridX, gridY);
        this.x = pixelPos.x;
        this.y = pixelPos.y;
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public boolean canPerformAction() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActionTime) >= ACTION_COOLDOWN;
    }

    public void performAction() {
        lastActionTime = System.currentTimeMillis();
    }

    public boolean isOnCooldown() {
        return !canPerformAction();
    }

    public long getRemainingCooldown() {
        long currentTime = System.currentTimeMillis();
        long timeSinceAction = currentTime - lastActionTime;
        long remaining = ACTION_COOLDOWN - timeSinceAction;
        return remaining > 0 ? remaining : 0;
    }

    /**
     * Inicia la animacion de muerte
     */
    public void startDeathAnimation() {
        if (performingDeathAnimation || performingWinAnimation) {
            return;
        }
        performingDeathAnimation = true;
        deathFrame = 0;
        deathCounter = 0;
        deathAnimationComplete = false;

        // Detener todo movimiento y acciones
        moving = false;
        transitioning = false;
        performingPutIceAction = false;
        performingBreakIceAction = false;

        System.out.println("Iniciando animacion de muerte");
    }

    /**
     * Inicia la animacion de victoria
     */
    public void startWinAnimation() {
        if (performingDeathAnimation || performingWinAnimation) {
            return;
        }
        performingWinAnimation = true;
        winFrame = 0;
        winCounter = 0;

        // Detener todo movimiento y acciones
        moving = false;
        transitioning = false;
        performingPutIceAction = false;
        performingBreakIceAction = false;

        System.out.println("Iniciando animacion de victoria");
    }

    /**
     * Verifica si la animacion de muerte ha terminado
     */
    public boolean isDeathAnimationComplete() {
        return deathAnimationComplete;
    }

    /**
     * Verifica si esta ejecutando la animacion de muerte
     */
    public boolean isPerformingDeathAnimation() {
        return performingDeathAnimation;
    }

    /**
     * Verifica si esta ejecutando la animacion de victoria
     */
    public boolean isPerformingWinAnimation() {
        return performingWinAnimation;
    }


    private void loadAllSprites(String base) {
        sprites = new EnumMap<>(Player.Direction.class);
        putIceSprites = new EnumMap<>(Player.Direction.class);

        // Caminar (UP/DOWN/LEFT/RIGHT) e idle (primer frame de caminar)
        Image[] up    = loadFrames(base + "/Walk/Up");
        Image[] down  = loadFrames(base + "/Walk/Down");
        Image[] left  = loadFrames(base + "/Walk/Left");
        Image[] right = loadFrames(base + "/Walk/Right");

        sprites.put(Player.Direction.UP, up);
        sprites.put(Player.Direction.DOWN, down);
        sprites.put(Player.Direction.LEFT, left);
        sprites.put(Player.Direction.RIGHT, right);
        sprites.put(Player.Direction.IDLE_UP, firstFrame(up));
        sprites.put(Player.Direction.IDLE_DOWN, firstFrame(down));
        sprites.put(Player.Direction.IDLE_LEFT, firstFrame(left));
        sprites.put(Player.Direction.IDLE_RIGHT, firstFrame(right));

        // Poner hielo: Back = arriba, Front = abajo
        putIceSprites.put(Player.Direction.UP, loadFrames(base + "/Put Ice/Back"));
        putIceSprites.put(Player.Direction.DOWN, loadFrames(base + "/Put Ice/Front"));
        putIceSprites.put(Player.Direction.LEFT, loadFrames(base + "/Put Ice/Left"));
        putIceSprites.put(Player.Direction.RIGHT, loadFrames(base + "/Put Ice/Right"));

        Image[] fallback = firstFrame(down);
        breakIceSprites = nonEmptyOr(loadFrames(base + "/Broke Ice"), fallback);
        deathSprites    = nonEmptyOr(loadFrames(base + "/Dead"), fallback);
        winSprites      = nonEmptyOr(loadFrames(base + "/Win"), fallback);
    }

    /** Carga 1.png, 2.png, ... hasta que no exista el siguiente. */
    private static Image[] loadFrames(String folder) {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; ; i++) {
            File f = new File(folder + "/" + i + ".png");
            if (!f.exists()) break;
            frames.add(new ImageIcon(f.getPath()).getImage());
        }
        if (frames.isEmpty()) {
            System.out.println("Advertencia: sin sprites en " + folder);
        }
        return frames.toArray(new Image[0]);
    }

    private static Image[] firstFrame(Image[] arr) {
        return arr.length > 0 ? new Image[]{arr[0]} : new Image[0];
    }

    private static Image[] nonEmptyOr(Image[] arr, Image[] fallback) {
        return arr.length > 0 ? arr : fallback;
    }

    @Override
    public boolean startPutIceAnimation() {
        if (performingPutIceAction || performingBreakIceAction || transitioning || performingDeathAnimation || performingWinAnimation) {
            return false;
        }
        performingPutIceAction = true;
        putIceFrame = 0;
        putIceCounter = 0;
        putIceDirection = facingDirection;
        shouldCreateIceFlag = false;
        System.out.println("Iniciando animacion de crear hielo hacia: " + putIceDirection);
        return true;
    }

    @Override
    public boolean startBreakIceAnimation() {
        if (performingPutIceAction || performingBreakIceAction || transitioning || performingDeathAnimation || performingWinAnimation) {
            return false;
        }
        performingBreakIceAction = true;
        breakIceFrame = 0;
        breakIceCounter = 0;
        shouldDestroyIceFlag = false;
        System.out.println("Iniciando animacion de romper hielo");
        return true;
    }

    @Override
    public boolean isPerformingAction() {
        return performingPutIceAction || performingBreakIceAction || performingDeathAnimation || performingWinAnimation;
    }

    @Override
    public boolean isPerformingPutIceAction() {
        return performingPutIceAction;
    }

    @Override
    public boolean isPerformingBreakIceAction() {
        return performingBreakIceAction;
    }

    @Override
    public boolean shouldCreateIce() {
        return shouldCreateIceFlag;
    }

    @Override
    public boolean shouldDestroyIce() {
        return shouldDestroyIceFlag;
    }

    @Override
    public void resetIceCreation() {
        shouldCreateIceFlag = false;
    }

    @Override
    public void resetIceDestruction() {
        shouldDestroyIceFlag = false;
    }

    @Override
    public void moveUp() {
        if (performingPutIceAction || performingBreakIceAction || performingDeathAnimation || performingWinAnimation) return;

        facingDirection = Player.Direction.UP;
        moving = true;

        if (transitioning) {
            pendingDirection = Player.Direction.UP;
        } else {
            startMovement(Player.Direction.UP);
        }
    }

    @Override
    public void moveDown() {
        if (performingPutIceAction || performingBreakIceAction || performingDeathAnimation || performingWinAnimation) return;

        facingDirection = Player.Direction.DOWN;
        moving = true;

        if (transitioning) {
            pendingDirection = Player.Direction.DOWN;
        } else {
            startMovement(Player.Direction.DOWN);
        }
    }

    @Override
    public void moveLeft() {
        if (performingPutIceAction || performingBreakIceAction || performingDeathAnimation || performingWinAnimation) return;

        facingDirection = Player.Direction.LEFT;
        moving = true;

        if (transitioning) {
            pendingDirection = Player.Direction.LEFT;
        } else {
            startMovement(Player.Direction.LEFT);
        }
    }

    @Override
    public void moveRight() {
        if (performingPutIceAction || performingBreakIceAction || performingDeathAnimation || performingWinAnimation) return;

        facingDirection = Player.Direction.RIGHT;
        moving = true;

        if (transitioning) {
            pendingDirection = Player.Direction.RIGHT;
        } else {
            startMovement(Player.Direction.RIGHT);
        }
    }

    @Override
    public void stopMoving() {
        moving = false;
        pendingDirection = null;
        if (!transitioning) {
            animationFrame = 0;
        }
    }

    private void startMovement(Player.Direction direction) {
        if (collisionDetector == null || grid == null) return;
        if (!collisionDetector.canMove(gridX, gridY, direction)) {
            moving = false;
            pendingDirection = null;
            return;
        }
        currentDirection = direction;
        moving = true;
        transitioning = true;
        Point nextGrid = grid.getNextGridPosition(gridX, gridY, direction);
        Point nextPixel = grid.gridToPixel(nextGrid.x, nextGrid.y);
        targetX = nextPixel.x;
        targetY = nextPixel.y;
    }

    @Override
    public void startGridMovement(Player.Direction direction, GridSystem grid) {
    }

    @Override
    public void snapToNearestGrid(GridSystem grid) {
    }

    @Override
    public void update() {
        if (performingDeathAnimation) {
            updateDeathAnimation();
        } else if (performingWinAnimation) {
            updateWinAnimation();
        } else if (performingPutIceAction) {
            updatePutIceAnimation();
        } else if (performingBreakIceAction) {
            updateBreakIceAnimation();
        } else if (transitioning) {
            performGridMovement();
        }
    }

    private void updateDeathAnimation() {
        deathCounter++;
        if (deathCounter >= DEATH_ANIMATION_SPEED) {
            deathCounter = 0;
            deathFrame++;

            System.out.println("Death animation frame: " + deathFrame + "/" + (deathSprites.length - 1));

            if (deathFrame >= deathSprites.length) {
                deathFrame = deathSprites.length - 1;
                deathAnimationComplete = true;
                System.out.println("ANIMACION DE MUERTE COMPLETADA para jugador");
            }
        }
    }

    private void updateWinAnimation() {
        winCounter++;
        if (winCounter >= WIN_ANIMATION_SPEED) {
            winCounter = 0;
            winFrame++;

            // Loop infinito de la animacion de victoria
            if (winFrame >= WIN_FRAMES) {
                winFrame = 0;
            }
        }
    }

    private void updatePutIceAnimation() {
        putIceCounter++;
        if (putIceCounter >= PUT_ICE_ANIMATION_SPEED) {
            putIceCounter = 0;
            putIceFrame++;
            if (putIceFrame == PUT_ICE_TRIGGER_FRAME) {
                shouldCreateIceFlag = true;
                System.out.println("Momento de crear hielo!");
            }
            Image[] currentPutIceSprites = putIceSprites.get(putIceDirection);
            if (putIceFrame >= currentPutIceSprites.length) {
                performingPutIceAction = false;
                putIceFrame = 0;
                System.out.println("Animacion de crear hielo terminada");
            }
        }
    }

    private void updateBreakIceAnimation() {
        breakIceCounter++;
        if (breakIceCounter >= BREAK_ICE_ANIMATION_SPEED) {
            breakIceCounter = 0;
            breakIceFrame++;
            if (breakIceFrame == BREAK_ICE_TRIGGER_FRAME) {
                shouldDestroyIceFlag = true;
                System.out.println("Momento de destruir hielo!");
            }
            if (breakIceFrame >= breakIceSprites.length) {
                performingBreakIceAction = false;
                breakIceFrame = 0;
                System.out.println("Animacion de romper hielo terminada");
            }
        }
    }

    private void performGridMovement() {
        boolean reachedTarget = true;
        if (x < targetX) {
            x += moveSpeed;
            if (x >= targetX) x = targetX;
            else reachedTarget = false;
        } else if (x > targetX) {
            x -= moveSpeed;
            if (x <= targetX) x = targetX;
            else reachedTarget = false;
        }
        if (y < targetY) {
            y += moveSpeed;
            if (y >= targetY) y = targetY;
            else reachedTarget = false;
        } else if (y > targetY) {
            y -= moveSpeed;
            if (y <= targetY) y = targetY;
            else reachedTarget = false;
        }
        updateAnimation();
        if (reachedTarget) {
            Point gridPos = grid.pixelToGrid(x, y);
            gridX = gridPos.x;
            gridY = gridPos.y;
            transitioning = false;
            if (pendingDirection != null) {
                Player.Direction nextDir = pendingDirection;
                pendingDirection = null;
                startMovement(nextDir);
            } else if (moving) {
                startMovement(currentDirection);
            }
        }
    }

    private void updateAnimation() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame++;
            Image[] currentSprites = sprites.get(currentDirection);
            if (animationFrame >= currentSprites.length) {
                animationFrame = 0;
            }
        }
    }

    @Override
    public Image getCurrentSprite() {
        if (performingDeathAnimation) {
            if (deathFrame < deathSprites.length) {
                return deathSprites[deathFrame];
            }
        }

        if (performingWinAnimation) {
            if (winFrame < winSprites.length) {
                return winSprites[winFrame];
            }
        }

        if (performingBreakIceAction) {
            if (breakIceFrame < breakIceSprites.length) {
                return breakIceSprites[breakIceFrame];
            }
        }

        if (performingPutIceAction) {
            Image[] currentPutIceSprites = putIceSprites.get(putIceDirection);
            if (currentPutIceSprites != null && putIceFrame < currentPutIceSprites.length) {
                return currentPutIceSprites[putIceFrame];
            }
        }

        if (transitioning) {
            Image[] currentSprites = sprites.get(currentDirection);
            if (currentSprites != null && currentSprites.length > 0) {
                return currentSprites[animationFrame % currentSprites.length];
            }
        }

        Player.Direction idleDirection = getIdleDirection(facingDirection);
        Image[] idleSprites = sprites.get(idleDirection);
        if (idleSprites != null && idleSprites.length > 0) {
            return idleSprites[0];
        }

        return null;
    }

    private Player.Direction getIdleDirection(Player.Direction direction) {
        switch (direction) {
            case UP:
                return Player.Direction.IDLE_UP;
            case DOWN:
                return Player.Direction.IDLE_DOWN;
            case LEFT:
                return Player.Direction.IDLE_LEFT;
            case RIGHT:
                return Player.Direction.IDLE_RIGHT;
            default:
                return Player.Direction.IDLE_DOWN;
        }
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public int getWidth() {
        if (performingDeathAnimation) return DEATH_SPRITE_WIDTH;
        if (performingWinAnimation) return WIN_SPRITE_WIDTH;
        if (performingBreakIceAction) return BREAK_ICE_SPRITE_WIDTH;
        if (performingPutIceAction) return PUT_ICE_SPRITE_WIDTH;
        return WALK_SPRITE_WIDTH;
    }

    @Override
    public int getHeight() {
        if (performingDeathAnimation) return DEATH_SPRITE_HEIGHT;
        if (performingWinAnimation) return WIN_SPRITE_HEIGHT;
        if (performingBreakIceAction) return BREAK_ICE_SPRITE_HEIGHT;
        if (performingPutIceAction) return PUT_ICE_SPRITE_HEIGHT;
        return WALK_SPRITE_HEIGHT;
    }

    @Override
    public Player.Direction getDirection() {
        return facingDirection;
    }

    @Override
    public boolean isMoving() { return moving || transitioning; }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        if (grid != null) {
            Point gridPos = grid.pixelToGrid(x, y);
            this.gridX = gridPos.x;
            this.gridY = gridPos.y;
        }
        this.transitioning = false;
        this.moving = false;
    }

    @Override
    public void startMoveInDirection(Direction direction, GridSystem grid) {
        if (performingPutIceAction || performingBreakIceAction ||
                performingDeathAnimation || performingWinAnimation) {
            return;
        }

        // Establecer la direccion que mira el jugador
        facingDirection = direction;
        moving = true;

        // Si no esta en transicion, iniciar el movimiento
        if (!transitioning) {
            startMovement(direction);
        } else {
            // Si esta en transicion, guardar como direccion pendiente
            pendingDirection = direction;
        }
    }

    public void setSpeed(int speed) {
        this.moveSpeed = speed;
    }
}