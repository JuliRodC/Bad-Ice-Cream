package Domain;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja el mapa del juego, bloques, muros y fogatas usando un sistema de cuadricula.
 */
public class GameMap {
    private List<Block> blocks;
    private List<Wall> walls;
    private GridSystem grid;
    private List<Tile> tiles;
    private Map<Point, Tile> tileOccupancy;
    private List<FireBlock> campfires;
    private Map<Point, FireBlock> campfireOccupancy;

    private Map<Point, Block> gridOccupancy;

    public GameMap(int mapWidth, int mapHeight, int cellSize) {
        this.grid = new GameGrid(mapWidth, mapHeight, cellSize);
        this.blocks = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.gridOccupancy = new HashMap<>();
        this.tiles = new ArrayList<>();
        this.tileOccupancy = new HashMap<>();
        this.campfires = new ArrayList<>();
        this.campfireOccupancy = new HashMap<>();
    }

    public void initializeBorderWalls() {
        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();

        walls.add(new CornerWall(0, 0, grid, "Images/Resources/MuroEsquina.png"));
        walls.add(new CornerWall(gridWidth - 2, 0, grid, "Images/Resources/MuroEsquina.png"));
        walls.add(new CornerWall(0, gridHeight - 2, grid, "Images/Resources/MuroEsquina.png"));
        walls.add(new CornerWall(gridWidth - 2, gridHeight - 2, grid, "Images/Resources/MuroEsquina.png"));

        for (int x = 2; x < gridWidth - 2; x++) {
            walls.add(new HorizontalWall(x, 0, grid, "Images/Resources/MuroInferior.png"));
        }

        for (int x = 2; x < gridWidth - 2; x++) {
            walls.add(new HorizontalWall(x, gridHeight - 1, grid, "Images/Resources/MuroInferior.png"));
        }

        for (int y = 2; y < gridHeight - 2; y++) {
            walls.add(new VerticalWall(0, y, grid, "Images/Resources/Muro.png"));
        }

        for (int y = 2; y < gridHeight - 2; y++) {
            walls.add(new VerticalWall(gridWidth - 1, y, grid, "Images/Resources/Muro.png"));
        }

        System.out.println("Muros del borde inicializados: " + walls.size() + " muros");
    }

    public void createInitialIceLayout() {
        createSolidIceAt(2, 1);
        createSolidIceAt(3, 1);
        createSolidIceAt(4, 1);
        createSolidIceAt(5, 1);
        createSolidIceAt(6, 1);
        createSolidIceAt(7, 1);
        createSolidIceAt(8, 1);
        createSolidIceAt(9, 1);
        createSolidIceAt(10, 1);
        createSolidIceAt(11, 1);
        createSolidIceAt(12, 1);
        createSolidIceAt(13, 1);
        createSolidIceAt(14, 1);
        createSolidIceAt(15, 1);

        createSolidIceAt(1, 2);
        createSolidIceAt(1, 3);
        createSolidIceAt(1, 4);
        createSolidIceAt(1, 5);
        createSolidIceAt(1, 6);
        createSolidIceAt(1, 7);
        createSolidIceAt(1, 8);
        createSolidIceAt(1, 9);
        createSolidIceAt(1, 10);
        createSolidIceAt(1, 11);
        createSolidIceAt(1, 12);
        createSolidIceAt(1, 13);
        createSolidIceAt(1, 14);
        createSolidIceAt(1, 15);

        createSolidIceAt(16, 2);
        createSolidIceAt(16, 3);
        createSolidIceAt(16, 4);
        createSolidIceAt(16, 5);
        createSolidIceAt(16, 6);
        createSolidIceAt(16, 7);
        createSolidIceAt(16, 8);
        createSolidIceAt(16, 9);
        createSolidIceAt(16, 10);
        createSolidIceAt(16, 11);
        createSolidIceAt(16, 12);
        createSolidIceAt(16, 13);
        createSolidIceAt(16, 14);
        createSolidIceAt(16, 15);

        createSolidIceAt(4, 5);
        createSolidIceAt(5, 5);
        createSolidIceAt(6, 5);

        createSolidIceAt(11, 5);
        createSolidIceAt(12, 5);
        createSolidIceAt(13, 5);

        createSolidIceAt(4, 6);
        createSolidIceAt(13, 6);

        createSolidIceAt(4, 7);
        createSolidIceAt(4, 8);
        createSolidIceAt(4, 9);

        createSolidIceAt(13, 7);
        createSolidIceAt(13, 8);
        createSolidIceAt(13, 9);

        createSolidIceAt(4, 10);
        createSolidIceAt(13, 10);

        createSolidIceAt(4, 11);
        createSolidIceAt(13, 11);

        createSolidIceAt(4, 12);
        createSolidIceAt(5, 12);
        createSolidIceAt(6, 12);
        createSolidIceAt(11, 12);
        createSolidIceAt(12, 12);
        createSolidIceAt(13, 12);

        createSolidIceAt(2, 16);
        createSolidIceAt(3, 16);
        createSolidIceAt(4, 16);
        createSolidIceAt(5, 16);
        createSolidIceAt(6, 16);
        createSolidIceAt(7, 16);
        createSolidIceAt(8, 16);
        createSolidIceAt(9, 16);
        createSolidIceAt(10, 16);
        createSolidIceAt(11, 16);
        createSolidIceAt(12, 16);
        createSolidIceAt(13, 16);
        createSolidIceAt(14, 16);
        createSolidIceAt(15, 16);
        createSolidIceAt(16, 16);

        System.out.println("Hielos iniciales creados: " + blocks.size() + " bloques");
    }

    private void createSolidIceAt(int gridX, int gridY) {
        Point pixelPos = grid.gridToPixel(gridX, gridY);
        Block ice = new SolidIceBlock(pixelPos.x, pixelPos.y);
        blocks.add(ice);
        gridOccupancy.put(new Point(gridX, gridY), ice);
    }

    /**
     * Crea hielo en una direccion, saltandose baldosas calientes y fogatas pero continuando despues
     */
    public void createIceInDirection(int playerPixelX, int playerPixelY, Player.Direction direction) {
        Point playerGrid = grid.pixelToGrid(playerPixelX, playerPixelY);
        Point currentGrid = grid.getNextGridPosition(playerGrid.x, playerGrid.y, direction);

        while (grid.isInBounds(currentGrid.x, currentGrid.y)) {
            // Si hay un bloque solido (muro o hielo), DETENER completamente
            if (hasBlockAtGrid(currentGrid.x, currentGrid.y)) {
                break;
            }

            // Si hay baldosa caliente, NO crear hielo pero CONTINUAR
            if (hasHotTileAt(currentGrid.x, currentGrid.y)) {
                System.out.println("Baldosa caliente en (" + currentGrid.x + ", " +
                        currentGrid.y + ") - saltando pero continuando");
                currentGrid = grid.getNextGridPosition(currentGrid.x, currentGrid.y, direction);
                continue;
            }

            // Si hay fogata, apagar la fogata y crear hielo encima
            if (hasCampfireAt(currentGrid.x, currentGrid.y)) {
                FireBlock campfire = getCampfireAt(currentGrid.x, currentGrid.y);
                if (campfire != null) {
                    campfire.extinguish();
                    campfire.setHasIceAbove(true);
                    System.out.println("Fogata apagada con hielo en (" + currentGrid.x + ", " + currentGrid.y + ")");
                }
            }

            // Crear hielo normal
            Point pixelPos = grid.gridToPixel(currentGrid.x, currentGrid.y);
            Block newBlock = new IceBlock(pixelPos.x, pixelPos.y);
            blocks.add(newBlock);
            gridOccupancy.put(new Point(currentGrid.x, currentGrid.y), newBlock);

            currentGrid = grid.getNextGridPosition(currentGrid.x, currentGrid.y, direction);
        }
    }

    /**
     * Destruye bloques de hielo en una direccion
     * Se detiene cuando encuentra un espacio vacio o un muro
     */
    public boolean destroyIceInDirection(int playerPixelX, int playerPixelY, Player.Direction direction) {
        Point playerGrid = grid.pixelToGrid(playerPixelX, playerPixelY);
        Point currentGrid = grid.getNextGridPosition(playerGrid.x, playerGrid.y, direction);

        boolean foundIce = false;

        while (grid.isInBounds(currentGrid.x, currentGrid.y)) {
            Point gridPoint = new Point(currentGrid.x, currentGrid.y);
            Block block = gridOccupancy.get(gridPoint);

            if (block != null && block.getType() == Block.BlockType.ICE) {
                block.startDestroy();
                foundIce = true;

                // Si hay una fogata en esta posicion, indicar que ya no hay hielo
                if (hasCampfireAt(currentGrid.x, currentGrid.y)) {
                    FireBlock campfire = getCampfireAt(currentGrid.x, currentGrid.y);
                    if (campfire != null) {
                        campfire.setHasIceAbove(false);
                        System.out.println("Hielo removido de fogata en (" + currentGrid.x + ", " + currentGrid.y + ")");
                    }
                }

                System.out.println("Destruyendo hielo en: (" + currentGrid.x + ", " + currentGrid.y + ")");
            } else if (hasBlockAtGrid(currentGrid.x, currentGrid.y)) {
                break;
            } else if (block == null) {
                break;
            }

            currentGrid = grid.getNextGridPosition(currentGrid.x, currentGrid.y, direction);
        }

        return foundIce;
    }

    public boolean hasBlockAtGrid(int gridX, int gridY) {
        for (Wall wall : walls) {
            if (wall.occupiesCell(gridX, gridY)) {
                return true;
            }
        }

        Point gridPoint = new Point(gridX, gridY);
        Block block = gridOccupancy.get(gridPoint);
        return block != null && block.isSolid();
    }

    public boolean hasBlockAtPixel(int pixelX, int pixelY) {
        Point gridPos = grid.pixelToGrid(pixelX, pixelY);
        return hasBlockAtGrid(gridPos.x, gridPos.y);
    }

    public List<Block> getBottomLayerBlocks() {
        List<Block> bottomBlocks = new ArrayList<>();
        for (Block block : blocks) {
            Point gridPos = grid.pixelToGrid(block.getX(), block.getY());
            if (gridPos.y == 16) {
                bottomBlocks.add(block);
            }
        }
        return bottomBlocks;
    }

    public List<Block> getTopLayerBlocks() {
        List<Block> topBlocks = new ArrayList<>();
        for (Block block : blocks) {
            Point gridPos = grid.pixelToGrid(block.getX(), block.getY());
            if (gridPos.y != 16) {
                topBlocks.add(block);
            }
        }
        return topBlocks;
    }

    public void update() {
        // Actualizar tiles
        for (Tile tile : tiles) {
            tile.update();
        }

        // Actualizar fogatas
        for (FireBlock campfire : campfires) {
            campfire.update();
        }

        // Actualizar y remover bloques destruidos
        List<Block> toRemove = new ArrayList<>();
        List<Point> keysToRemove = new ArrayList<>();

        for (Block block : blocks) {
            block.update();

            if (block.isDestroyed()) {
                toRemove.add(block);

                for (Map.Entry<Point, Block> entry : gridOccupancy.entrySet()) {
                    if (entry.getValue() == block) {
                        keysToRemove.add(entry.getKey());
                        break;
                    }
                }
            }
        }

        blocks.removeAll(toRemove);
        for (Point key : keysToRemove) {
            gridOccupancy.remove(key);
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<FireBlock> getCampfires() {
        return campfires;
    }

    public void clear() {
        blocks.clear();
        gridOccupancy.clear();
        walls.clear();
        tiles.clear();
        tileOccupancy.clear();
        campfires.clear();
        campfireOccupancy.clear();
    }

    public GridSystem getGrid() {
        return grid;
    }

    public void createSolidIceAtPosition(int gridX, int gridY) {
        Point pixelPos = grid.gridToPixel(gridX, gridY);
        Block ice = new SolidIceBlock(pixelPos.x, pixelPos.y);
        blocks.add(ice);
        gridOccupancy.put(new Point(gridX, gridY), ice);
    }

    public Point snapToGrid(int pixelX, int pixelY) {
        return grid.snapToGrid(pixelX, pixelY);
    }

    /**
     * Crea baldosas calientes en posiciones aleatorias
     */
    public void createRandomHotTiles(int count) {
        if (count <= 0) {
            System.out.println("No se crearan baldosas calientes (cantidad: 0)");
            return;
        }

        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();
        int created = 0;
        int attempts = 0;
        int maxAttempts = count * 20;

        while (created < count && attempts < maxAttempts) {
            attempts++;

            int randomX = 2 + (int)(Math.random() * (gridWidth - 4));
            int randomY = 2 + (int)(Math.random() * (gridHeight - 4));

            Point gridPoint = new Point(randomX, randomY);

            if (!hasBlockAtGrid(randomX, randomY) &&
                    !tileOccupancy.containsKey(gridPoint) &&
                    !campfireOccupancy.containsKey(gridPoint)) {

                HotTile hotTile = new HotTile(randomX, randomY, grid);
                tiles.add(hotTile);
                tileOccupancy.put(gridPoint, hotTile);
                created++;
            }
        }

        System.out.println("Baldosas calientes creadas: " + created);
    }

    /**
     * Crea fogatas en posiciones aleatorias
     */
    public void createRandomCampfires(int count) {
        if (count <= 0) {
            System.out.println("No se crearan fogatas (cantidad: 0)");
            return;
        }

        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();
        int created = 0;
        int attempts = 0;
        int maxAttempts = count * 20;

        while (created < count && attempts < maxAttempts) {
            attempts++;

            int randomX = 2 + (int)(Math.random() * (gridWidth - 4));
            int randomY = 2 + (int)(Math.random() * (gridHeight - 4));

            Point gridPoint = new Point(randomX, randomY);

            if (!hasBlockAtGrid(randomX, randomY) &&
                    !tileOccupancy.containsKey(gridPoint) &&
                    !campfireOccupancy.containsKey(gridPoint)) {

                Campfire campfire = new Campfire(randomX, randomY, grid);
                campfires.add(campfire);
                campfireOccupancy.put(gridPoint, campfire);
                created++;
            }
        }

        System.out.println("Fogatas creadas: " + created);
    }

    /**
     * Verifica si hay una baldosa caliente en una posicion
     */
    public boolean hasHotTileAt(int gridX, int gridY) {
        Point gridPoint = new Point(gridX, gridY);
        Tile tile = tileOccupancy.get(gridPoint);
        return tile != null && tile.getType() == Tile.TileType.HOT_TILE;
    }

    /**
     * Verifica si hay una fogata en una posicion
     */
    public boolean hasCampfireAt(int gridX, int gridY) {
        Point gridPoint = new Point(gridX, gridY);
        return campfireOccupancy.containsKey(gridPoint);
    }

    /**
     * Obtiene la fogata en una posicion especifica
     */
    public FireBlock getCampfireAt(int gridX, int gridY) {
        Point gridPoint = new Point(gridX, gridY);
        return campfireOccupancy.get(gridPoint);
    }

    /**
     * Verifica si hay una fogata encendida en una posicion
     */
    public boolean hasLitCampfireAt(int gridX, int gridY) {
        FireBlock campfire = getCampfireAt(gridX, gridY);
        return campfire != null && campfire.isLit();
    }

    /**
     * Reconstruye los mapas de ocupacion desde las listas actuales
     * Se usa al cargar una partida guardada
     */
    public void rebuildOccupancyMaps() {
        gridOccupancy.clear();
        tileOccupancy.clear();
        campfireOccupancy.clear();

        for (Block block : blocks) {
            Point gridPos = grid.pixelToGrid(block.getX(), block.getY());
            gridOccupancy.put(gridPos, block);
        }

        for (Tile tile : tiles) {
            Point gridPos = tile.getGridPosition();
            tileOccupancy.put(gridPos, tile);
        }

        for (FireBlock campfire : campfires) {
            if (campfire instanceof Campfire) {
                Campfire cf = (Campfire) campfire;
                Point gridPos = cf.getGridPosition();
                campfireOccupancy.put(gridPos, campfire);
            }
        }

        System.out.println("Mapas de ocupacion reconstruidos:");
        System.out.println("  Bloques: " + gridOccupancy.size());
        System.out.println("  Baldosas: " + tileOccupancy.size());
        System.out.println("  Fogatas: " + campfireOccupancy.size());
    }
}