package Domain;

import java.awt.event.KeyEvent;

/**
 * Manejador de entradas de teclado para los jugadores
 * Controla el movimiento y acciones de ambos jugadores
 */
public class InputHandler {
    private PlayerManager playerManager;
    private GameMap gameMap;

    // Estados de teclas para Jugador 1 (flechas)
    private boolean p1UpPressed;
    private boolean p1DownPressed;
    private boolean p1LeftPressed;
    private boolean p1RightPressed;

    // Estados de teclas para Jugador 2 (WASD)
    private boolean p2UpPressed;
    private boolean p2DownPressed;
    private boolean p2LeftPressed;
    private boolean p2RightPressed;

    public InputHandler(PlayerManager playerManager, GameMap gameMap) {
        this.playerManager = playerManager;
        this.gameMap = gameMap;
        resetAllKeys();
    }

    /**
     * Maneja la presion o liberacion de una tecla
     * @param e evento de teclado
     * @param pressed true si se presiono, false si se libero
     */
    public void handleKeyPress(KeyEvent e, boolean pressed) {
        int key = e.getKeyCode();

        Player player1 = playerManager.getPlayer(0);
        Player player2 = playerManager.getPlayer(1);

        // Controles del Jugador 1 (flechas y SPACE)
        if (player1 != null && !playerManager.isPlayerDead(0)) {
            handlePlayer1Input(key, pressed, player1);
        }

        // Controles del Jugador 2 (WASD y E) - solo si no es IA
        if (player2 != null && !playerManager.isPlayerDead(1)) {
            PlayerManager.PlayerInfo info = playerManager.getPlayerInfo(1);
            if (info != null && !info.isAI()) {
                handlePlayer2Input(key, pressed, player2);
            }
        }
    }

    /**
     * Maneja el input del Jugador 1
     */
    private void handlePlayer1Input(int key, boolean pressed, Player player) {
        // Movimiento
        if (key == KeyEvent.VK_UP) {
            p1UpPressed = pressed;
        } else if (key == KeyEvent.VK_DOWN) {
            p1DownPressed = pressed;
        } else if (key == KeyEvent.VK_LEFT) {
            p1LeftPressed = pressed;
        } else if (key == KeyEvent.VK_RIGHT) {
            p1RightPressed = pressed;
        }

        // Accion de hielo
        if (key == KeyEvent.VK_SPACE && pressed && gameMap != null) {
            handleIceAction(player);
        }
    }

    /**
     * Maneja el input del Jugador 2
     */
    private void handlePlayer2Input(int key, boolean pressed, Player player) {
        // Movimiento
        if (key == KeyEvent.VK_W) {
            p2UpPressed = pressed;
        } else if (key == KeyEvent.VK_S) {
            p2DownPressed = pressed;
        } else if (key == KeyEvent.VK_A) {
            p2LeftPressed = pressed;
        } else if (key == KeyEvent.VK_D) {
            p2RightPressed = pressed;
        }

        // Accion de hielo
        if (key == KeyEvent.VK_F && pressed && gameMap != null) {
            handleIceAction(player);
        }
    }

    /**
     * Actualiza el movimiento de los jugadores segun las teclas presionadas
     */
    public void updatePlayerMovement() {
        Player player1 = playerManager.getPlayer(0);
        Player player2 = playerManager.getPlayer(1);

        // Actualizar Jugador 1
        if (player1 != null && !playerManager.isPlayerDead(0)) {
            updatePlayer1Movement(player1);
        }

        // Actualizar Jugador 2 (solo si no es IA)
        if (player2 != null && !playerManager.isPlayerDead(1)) {
            PlayerManager.PlayerInfo info = playerManager.getPlayerInfo(1);
            if (info != null && !info.isAI()) {
                updatePlayer2Movement(player2);
            }
        }
    }

    /**
     * Actualiza el movimiento del Jugador 1
     */
    private void updatePlayer1Movement(Player player) {
        if (player.isPerformingAction()) {
            return;
        }

        if (player.isOnCooldown()) {
            player.stopMoving();
            return;
        }

        if (p1UpPressed) {
            player.moveUp();
        } else if (p1DownPressed) {
            player.moveDown();
        } else if (p1LeftPressed) {
            player.moveLeft();
        } else if (p1RightPressed) {
            player.moveRight();
        } else {
            player.stopMoving();
        }
    }

    /**
     * Actualiza el movimiento del Jugador 2
     */
    private void updatePlayer2Movement(Player player) {
        if (player.isPerformingAction()) {
            return;
        }

        if (player.isOnCooldown()) {
            player.stopMoving();
            return;
        }

        if (p2UpPressed) {
            player.moveUp();
        } else if (p2DownPressed) {
            player.moveDown();
        } else if (p2LeftPressed) {
            player.moveLeft();
        } else if (p2RightPressed) {
            player.moveRight();
        } else {
            player.stopMoving();
        }
    }

    /**
     * Maneja la accion de crear/destruir hielo
     */
    private void handleIceAction(Player player) {
        if (!player.canPerformAction() || player.isPerformingAction()) {
            return;
        }

        Player.Direction dir = player.getDirection();
        java.awt.Point playerGrid = gameMap.getGrid().pixelToGrid(player.getX(), player.getY());
        java.awt.Point nextGrid = gameMap.getGrid().getNextGridPosition(playerGrid.x, playerGrid.y, dir);

        // Verificar si hay hielo en la direccion
        boolean hasIceInDirection = false;
        if (gameMap.getGrid().isInBounds(nextGrid.x, nextGrid.y)) {
            for (Block block : gameMap.getBlocks()) {
                java.awt.Point blockGrid = gameMap.getGrid().pixelToGrid(block.getX(), block.getY());
                if (blockGrid.x == nextGrid.x && blockGrid.y == nextGrid.y &&
                        block.getType() == Block.BlockType.ICE) {
                    hasIceInDirection = true;
                    break;
                }
            }
        }

        boolean animationStarted = false;

        if (hasIceInDirection) {
            animationStarted = player.startBreakIceAnimation();
        } else {
            animationStarted = player.startPutIceAnimation();
        }

        if (animationStarted) {
            player.performAction();
        }
    }

    /**
     * Reinicia todos los estados de teclas
     */
    public void resetAllKeys() {
        p1UpPressed = false;
        p1DownPressed = false;
        p1LeftPressed = false;
        p1RightPressed = false;

        p2UpPressed = false;
        p2DownPressed = false;
        p2LeftPressed = false;
        p2RightPressed = false;
    }

    /**
     * Actualiza las referencias del gestor
     */
    public void updateReferences(PlayerManager playerManager, GameMap gameMap) {
        this.playerManager = playerManager;
        this.gameMap = gameMap;
    }
}