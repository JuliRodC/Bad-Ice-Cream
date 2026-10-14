package Presentation;

import Domain.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Point;

/**
 * Renderizador del juego
 * Maneja el dibujo de todos los elementos visuales en orden correcto
 * Usa datos proporcionados por GameController sin acceso directo a Domain
 */
public class GameRenderer {
    private Image backgroundImage;
    private boolean showDebugGrid;
    private static final int CELL_SIZE = 42;

    public GameRenderer() {
        this.showDebugGrid = false;
        loadBackground();
    }

    private void loadBackground() {
        try {
            String path = "Images/Nieve.png";
            backgroundImage = new javax.swing.ImageIcon(path).getImage();
            if (backgroundImage.getWidth(null) > 0) {
                System.out.println("Fondo del juego cargado desde: " + path);
            }
        } catch (Exception e) {
            System.out.println("Error cargando fondo del juego: " + e.getMessage());
        }
    }

    /**
     * Renderiza toda la escena del juego usando RenderData
     */
    public void renderFromData(Graphics g, GameController.RenderData data, int width, int height) {
        // 1. Fondo
        drawBackground(g, width, height);

        // 2. Grid de debug (opcional)
        if (showDebugGrid) {
            drawDebugGrid(g, width, height);
        }

        // 3. Bloques de la capa inferior
        for (Block block : data.bottomBlocks) {
            drawBlock(g, block);
        }

        // 4. Muros
        for (Wall wall : data.walls) {
            drawWall(g, wall);
        }

        // 5. Bloques de la capa superior
        for (Block block : data.topBlocks) {
            drawBlock(g, block);
        }

        // 6. Baldosas
        for (Tile tile : data.tiles) {
            drawTile(g, tile);
        }

        // 7. Fogatas
        for (FireBlock campfire : data.campfires) {
            drawCampfire(g, campfire);
        }

        // 8. Frutas
        for (Fruit fruit : data.fruits) {
            if (!fruit.isCollected()) {
                drawFruit(g, fruit);
            }
        }

        // 9. Enemigos
        for (Enemy enemy : data.enemies) {
            if (enemy.isActive()) {
                drawEnemy(g, enemy);
            }
        }

        // 10. Jugadores
        for (Player player : data.players) {
            drawPlayer(g, player);
        }
    }

    private void drawBackground(Graphics g, int width, int height) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }
    }

    private void drawBlock(Graphics g, Block block) {
        Image sprite = block.getCurrentSprite();
        if (sprite != null) {
            g.drawImage(sprite,
                    block.getX(),
                    block.getY(),
                    block.getWidth(),
                    block.getHeight(),
                    null);
        }
    }

    private void drawWall(Graphics g, Wall wall) {
        Image sprite = wall.getSprite();
        if (sprite != null) {
            g.drawImage(sprite,
                    wall.getX(),
                    wall.getY(),
                    wall.getWidth(),
                    wall.getHeight(),
                    null);
        }
    }

    private void drawTile(Graphics g, Tile tile) {
        Image sprite = tile.getSprite();
        if (sprite != null) {
            g.drawImage(sprite,
                    tile.getX(),
                    tile.getY(),
                    tile.getWidth(),
                    tile.getHeight(),
                    null);
        } else {
            g.setColor(new Color(255, 100, 0, 150));
            g.fillRect(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
        }
    }

    private void drawCampfire(Graphics g, FireBlock campfire) {
        // Verificar si es una Campfire para acceder a metodos especificos
        if (campfire instanceof Campfire) {
            Campfire cf = (Campfire) campfire;

            // Dibujar las rocas (base de la fogata)
            Image rocksSprite = cf.getCurrentSprite();
            if (rocksSprite != null) {
                g.drawImage(rocksSprite,
                        cf.getRocksRenderX(),
                        cf.getRocksRenderY(),
                        cf.getRocksSpriteWidth(),
                        cf.getRocksSpriteHeight(),
                        null);
            }

            // Dibujar las llamas si esta encendida
            if (cf.isLit()) {
                Image flameSprite = cf.getFlameSprite();
                if (flameSprite != null) {
                    g.drawImage(flameSprite,
                            cf.getFlameRenderX(),
                            cf.getFlameRenderY(),
                            cf.getFlameSpriteWidth(),
                            cf.getFlameSpriteHeight(),
                            null);
                }
            }
        } else {
            // Fallback para otros tipos de FireBlock
            Image sprite = campfire.getCurrentSprite();
            if (sprite != null) {
                g.drawImage(sprite,
                        campfire.getX(),
                        campfire.getY(),
                        campfire.getWidth(),
                        campfire.getHeight(),
                        null);
            }
        }
    }

    private void drawFruit(Graphics g, Fruit fruit) {
        Image sprite = fruit.getCurrentSprite();
        if (sprite != null) {
            int fruitDrawX = fruit.getX() + (CELL_SIZE - fruit.getWidth()) / 2;
            int fruitDrawY = fruit.getY() + (CELL_SIZE - fruit.getHeight()) / 2;

            g.drawImage(sprite,
                    fruitDrawX,
                    fruitDrawY,
                    fruit.getWidth(),
                    fruit.getHeight(),
                    null);
        }
    }

    private void drawEnemy(Graphics g, Enemy enemy) {
        Image sprite = enemy.getCurrentSprite();
        if (sprite != null) {
            int enemyDrawX = enemy.getX() + (CELL_SIZE - enemy.getWidth()) / 2;
            int enemyDrawY = enemy.getY() + (CELL_SIZE - enemy.getHeight()) / 2;

            g.drawImage(sprite,
                    enemyDrawX,
                    enemyDrawY,
                    enemy.getWidth(),
                    enemy.getHeight(),
                    null);
        }
    }

    private void drawPlayer(Graphics g, Player player) {
        Image sprite = player.getCurrentSprite();
        if (sprite != null) {
            int drawWidth = player.getWidth();
            int drawHeight = player.getHeight();

            int playerDrawX = player.getX() + (CELL_SIZE - drawWidth) / 2;
            int playerDrawY = player.getY() + (CELL_SIZE - drawHeight) / 2;

            if (player.isPerformingPutIceAction()) {
                playerDrawY -= 15;
            }

            g.drawImage(sprite, playerDrawX, playerDrawY,
                    drawWidth, drawHeight, null);
        }
    }

    private void drawDebugGrid(Graphics g, int width, int height) {
        g.setColor(new Color(255, 0, 0, 150));

        int gridWidth = width / CELL_SIZE;
        int gridHeight = height / CELL_SIZE;

        for (int x = 0; x <= gridWidth; x++) {
            int xPos = x * CELL_SIZE;
            g.drawLine(xPos, 0, xPos, height);
        }

        for (int y = 0; y <= gridHeight; y++) {
            int yPos = y * CELL_SIZE;
            g.drawLine(0, yPos, width, yPos);
        }
    }

    public void toggleDebugGrid() {
        showDebugGrid = !showDebugGrid;
        System.out.println("Grid de debug: " + (showDebugGrid ? "ON" : "OFF"));
    }

    public void setDebugGrid(boolean show) {
        showDebugGrid = show;
    }

    public boolean isDebugGridActive() {
        return showDebugGrid;
    }
}