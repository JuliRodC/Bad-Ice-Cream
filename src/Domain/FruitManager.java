package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de frutas del juego con soporte para multiples jugadores
 * Sistema de oleadas: Banana -> Uva -> Cereza -> Pina -> Cactus
 */
public class FruitManager {
    private List<Fruit> fruits;
    private GridSystem grid;
    private GameMap gameMap;
    private int totalFruits;

    private Map<Player, Integer> playerScores;
    private Map<Player, Integer> playerCollectedFruits;

    private int totalBananas;
    private int totalGrapes;
    private int totalCherries;
    private int totalPineapples;
    private int totalCactuses;

    private boolean bananasCompleted;
    private boolean grapesSpawned;
    private boolean grapesCompleted;
    private boolean cherriesSpawned;
    private boolean cherriesCompleted;
    private boolean pineapplesSpawned;
    private boolean pineapplesCompleted;
    private boolean cactusesSpawned;
    private boolean cactusesCompleted;

    public FruitManager(GridSystem grid) {
        this.grid = grid;
        this.fruits = new ArrayList<Fruit>();
        this.playerScores = new HashMap<Player, Integer>();
        this.playerCollectedFruits = new HashMap<Player, Integer>();
        this.totalFruits = 0;

        this.totalBananas = 0;
        this.totalGrapes = 0;
        this.totalCherries = 0;
        this.totalPineapples = 0;
        this.totalCactuses = 0;

        this.bananasCompleted = false;
        this.grapesSpawned = false;
        this.grapesCompleted = false;
        this.cherriesSpawned = false;
        this.cherriesCompleted = false;
        this.pineapplesSpawned = false;
        this.pineapplesCompleted = false;
        this.cactusesSpawned = false;
        this.cactusesCompleted = false;
    }

    public void configureWaveCounts(int bananas, int grapes, int cherries, int pineapples, int cactuses) {
        this.totalBananas = bananas;
        this.totalGrapes = grapes;
        this.totalCherries = cherries;
        this.totalPineapples = pineapples;
        this.totalCactuses = cactuses;

        System.out.println("=== OLEADAS CONFIGURADAS ===");
        System.out.println("  Bananas: " + totalBananas);
        System.out.println("  Uvas: " + totalGrapes);
        System.out.println("  Cerezas: " + totalCherries);
        System.out.println("  Pinas: " + totalPineapples);
        System.out.println("  Cactus: " + totalCactuses);
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.CHERRY) {
                CherryFruit cherry = (CherryFruit) fruit;
                cherry.setGameMap(gameMap);
            }
        }
    }

    public void registerPlayer(Player player) {
        if (!playerScores.containsKey(player)) {
            playerScores.put(player, 0);
            playerCollectedFruits.put(player, 0);
            System.out.println("Jugador registrado en FruitManager");
        }
    }

    public void addFruit(Fruit fruit) {
        fruits.add(fruit);
        totalFruits++;

        Fruit.FruitType type = fruit.getType();
        if (type == Fruit.FruitType.BANANA) {
            totalBananas++;
        } else if (type == Fruit.FruitType.GRAPE) {
            totalGrapes++;
        } else if (type == Fruit.FruitType.CHERRY) {
            totalCherries++;
        }
    }

    public void createLevelBananas(int bananaCount) {
        if (bananaCount <= 0) {
            System.out.println("No se crearan bananas (cantidad: 0)");
            bananasCompleted = true;
            totalBananas = 0;
            return;
        }

        int createdBananas = 0;

        int[][] predefinedPositions = {
                {3, 4}, {4, 4}, {3, 5},
                {3, 12}, {3, 13}, {4, 13},
                {14, 4}, {14, 5}, {13, 4},
                {14, 12}, {14, 13}, {13, 13},
                {7, 6}, {10, 6},
                {7, 11}, {10, 11},
                {6, 8}, {6, 9},
                {11, 8}, {11, 9}
        };

        for (int i = 0; i < predefinedPositions.length && createdBananas < bananaCount; i++) {
            addFruit(new BananaFruit(predefinedPositions[i][0], predefinedPositions[i][1], grid));
            createdBananas++;
        }

        if (createdBananas < bananaCount) {
            int currentX = 2;
            int currentY = 2;

            while (createdBananas < bananaCount) {
                boolean positionOccupied = false;
                for (Fruit fruit : fruits) {
                    if (fruit.getGridPosition().x == currentX && fruit.getGridPosition().y == currentY) {
                        positionOccupied = true;
                        break;
                    }
                }

                if (!positionOccupied && currentX >= 2 && currentX <= 15 && currentY >= 2 && currentY <= 15) {
                    addFruit(new BananaFruit(currentX, currentY, grid));
                    createdBananas++;
                }

                currentX++;
                if (currentX > 15) {
                    currentX = 2;
                    currentY++;
                }

                if (currentY > 15) {
                    currentY = 2;
                }
            }
        }

        totalBananas = createdBananas;
        System.out.println("Bananas creadas en el nivel: " + createdBananas);
    }

    public void spawnGrapes(int grapeCount) {
        if (grapesSpawned) {
            return;
        }

        if (grapeCount <= 0) {
            System.out.println("No se crearan uvas (cantidad: 0)");
            grapesSpawned = true;
            grapesCompleted = true;
            return;
        }

        int createdGrapes = 0;

        int[][] predefinedPositions = {
                {2, 2}, {3, 2}, {2, 3},
                {14, 2}, {15, 2}, {15, 3},
                {2, 14}, {2, 15}, {3, 15},
                {15, 14}, {15, 15}, {14, 15},
                {5, 6}, {5, 11}, {12, 6}, {12, 11}
        };

        for (int i = 0; i < predefinedPositions.length && createdGrapes < grapeCount; i++) {
            fruits.add(new GrapeFruit(predefinedPositions[i][0], predefinedPositions[i][1], grid));
            createdGrapes++;
        }

        if (createdGrapes < grapeCount) {
            int currentX = 2;
            int currentY = 2;

            while (createdGrapes < grapeCount) {
                boolean hasConflict = false;
                for (Fruit fruit : fruits) {
                    if (fruit.getGridPosition().x == currentX && fruit.getGridPosition().y == currentY) {
                        hasConflict = true;
                        break;
                    }
                }

                if (!hasConflict && currentX >= 2 && currentX <= 15 && currentY >= 2 && currentY <= 15) {
                    fruits.add(new GrapeFruit(currentX, currentY, grid));
                    createdGrapes++;
                }

                currentX++;
                if (currentX > 15) {
                    currentX = 2;
                    currentY++;
                }

                if (currentY > 15) {
                    currentY = 2;
                }
            }
        }

        totalGrapes = createdGrapes;
        totalFruits += createdGrapes;
        grapesSpawned = true;

        System.out.println("Uvas aparecieron! Total de uvas: " + createdGrapes);
        System.out.println("Total de frutas actualizado: " + totalFruits);
    }

    public void spawnCherries(int cherryCount) {
        if (cherriesSpawned) {
            return;
        }

        if (cherryCount <= 0) {
            System.out.println("No se crearan cerezas (cantidad: 0)");
            cherriesSpawned = true;
            cherriesCompleted = true;
            return;
        }

        if (gameMap == null) {
            System.out.println("Error: GameMap no configurado para spawn de cerezas");
            return;
        }

        int createdCherries = 0;
        int attempts = 0;
        int maxAttempts = cherryCount * 50;

        while (createdCherries < cherryCount && attempts < maxAttempts) {
            attempts++;

            int randomX = 2 + (int)(Math.random() * 14);
            int randomY = 2 + (int)(Math.random() * 14);

            boolean hasConflict = false;
            for (Fruit fruit : fruits) {
                if (fruit.getGridPosition().x == randomX && fruit.getGridPosition().y == randomY) {
                    hasConflict = true;
                    break;
                }
            }

            if (!hasConflict && !gameMap.hasBlockAtGrid(randomX, randomY) &&
                    !gameMap.hasHotTileAt(randomX, randomY) &&
                    !gameMap.hasCampfireAt(randomX, randomY)) {

                CherryFruit cherry = new CherryFruit(randomX, randomY, grid, gameMap);
                fruits.add(cherry);
                createdCherries++;
            }
        }

        totalCherries = createdCherries;
        totalFruits += createdCherries;
        cherriesSpawned = true;

        System.out.println("Cerezas aparecieron! Total de cerezas: " + createdCherries);
        System.out.println("Total de frutas actualizado: " + totalFruits);
    }

    public void spawnPineapples(int pineappleCount) {
        if (pineapplesSpawned) {
            return;
        }

        if (pineappleCount <= 0) {
            System.out.println("No se crearan pinas (cantidad: 0)");
            pineapplesSpawned = true;
            pineapplesCompleted = true;
            return;
        }

        if (gameMap == null) {
            System.out.println("Error: GameMap no configurado para spawn de pinas");
            return;
        }

        int createdPineapples = 0;
        int attempts = 0;
        int maxAttempts = pineappleCount * 50;

        CollisionDetector detector = gameMap.getGrid() != null ?
                new GameCollisionDetector(gameMap, gameMap.getGrid()) : null;

        if (detector == null) {
            System.out.println("Error: No se pudo crear CollisionDetector para pinas");
            return;
        }

        while (createdPineapples < pineappleCount && attempts < maxAttempts) {
            attempts++;

            int randomX = 2 + (int)(Math.random() * 14);
            int randomY = 2 + (int)(Math.random() * 14);

            boolean hasConflict = false;
            for (Fruit fruit : fruits) {
                if (fruit.getGridPosition().x == randomX && fruit.getGridPosition().y == randomY) {
                    hasConflict = true;
                    break;
                }
            }

            if (!hasConflict && !gameMap.hasBlockAtGrid(randomX, randomY) &&
                    !gameMap.hasHotTileAt(randomX, randomY) &&
                    !gameMap.hasCampfireAt(randomX, randomY)) {

                PineappleFruit pineapple = new PineappleFruit(randomX, randomY, grid, detector);
                fruits.add(pineapple);
                createdPineapples++;
            }
        }

        totalPineapples = createdPineapples;
        totalFruits += createdPineapples;
        pineapplesSpawned = true;

        System.out.println("Pinas aparecieron! Total de pinas: " + createdPineapples);
        System.out.println("Total de frutas actualizado: " + totalFruits);
    }

    public void spawnCactuses(int cactusCount) {
        if (cactusesSpawned) {
            return;
        }

        if (cactusCount <= 0) {
            System.out.println("No se crearan cactus (cantidad: 0)");
            cactusesSpawned = true;
            cactusesCompleted = true;
            return;
        }

        if (gameMap == null) {
            System.out.println("Error: GameMap no configurado para spawn de cactus");
            return;
        }

        int createdCactuses = 0;
        int attempts = 0;
        int maxAttempts = cactusCount * 50;

        while (createdCactuses < cactusCount && attempts < maxAttempts) {
            attempts++;

            int randomX = 2 + (int)(Math.random() * 14);
            int randomY = 2 + (int)(Math.random() * 14);

            boolean hasConflict = false;
            for (Fruit fruit : fruits) {
                if (fruit.getGridPosition().x == randomX && fruit.getGridPosition().y == randomY) {
                    hasConflict = true;
                    break;
                }
            }

            if (!hasConflict && !gameMap.hasBlockAtGrid(randomX, randomY) &&
                    !gameMap.hasHotTileAt(randomX, randomY) &&
                    !gameMap.hasCampfireAt(randomX, randomY)) {

                CactusFruit cactus = new CactusFruit(randomX, randomY, grid);
                fruits.add(cactus);
                createdCactuses++;
            }
        }

        totalCactuses = createdCactuses;
        totalFruits += createdCactuses;
        cactusesSpawned = true;

        System.out.println("Cactus aparecieron! Total de cactus: " + createdCactuses);
        System.out.println("Total de frutas actualizado: " + totalFruits);
    }

    private boolean allBananasCollected() {
        if (totalBananas == 0) {
            return true;
        }

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.BANANA && !fruit.isCollected()) {
                return false;
            }
        }
        return true;
    }

    private boolean allGrapesCollected() {
        if (totalGrapes == 0) {
            return true;
        }

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.GRAPE && !fruit.isCollected()) {
                return false;
            }
        }
        return true;
    }

    private boolean allCherriesCollected() {
        if (totalCherries == 0) {
            return true;
        }

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.CHERRY && !fruit.isCollected()) {
                return false;
            }
        }
        return true;
    }

    private boolean allPineapplesCollected() {
        if (totalPineapples == 0) {
            return true;
        }

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.PINEAPPLE && !fruit.isCollected()) {
                return false;
            }
        }
        return true;
    }

    private boolean allCactusesCollected() {
        if (totalCactuses == 0) {
            return true;
        }

        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.CACTUS && !fruit.isCollected()) {
                return false;
            }
        }
        return true;
    }

    public void update() {
        for (Fruit fruit : fruits) {
            fruit.update();
        }

        if (!bananasCompleted && allBananasCollected()) {
            bananasCompleted = true;
            System.out.println("Todas las bananas recolectadas!");
        }

        if (!grapesCompleted && grapesSpawned && allGrapesCollected()) {
            grapesCompleted = true;
            System.out.println("Todas las uvas recolectadas!");
        }

        if (!cherriesCompleted && cherriesSpawned && allCherriesCollected()) {
            cherriesCompleted = true;
            System.out.println("Todas las cerezas recolectadas!");
        }

        if (!pineapplesCompleted && pineapplesSpawned && allPineapplesCollected()) {
            pineapplesCompleted = true;
            System.out.println("Todas las pinas recolectadas!");
        }

        if (!cactusesCompleted && cactusesSpawned && allCactusesCollected()) {
            cactusesCompleted = true;
            System.out.println("Todos los cactus recolectados!");
        }
    }

    public void notifyPlayerMoved() {
        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.PINEAPPLE && !fruit.isCollected()) {
                PineappleFruit pineapple = (PineappleFruit) fruit;
                pineapple.moveRandomly();
            }
        }
    }

    public void checkCollisions(Player player) {
        if (!playerScores.containsKey(player)) {
            registerPlayer(player);
        }

        for (Fruit fruit : fruits) {
            if (!fruit.isCollected() &&
                    fruit.collidesWith(player.getX(), player.getY(),
                            player.getWidth(), player.getHeight())) {
                fruit.collect();

                int currentScore = playerScores.get(player);
                playerScores.put(player, currentScore + fruit.getPoints());

                int currentCollected = playerCollectedFruits.get(player);
                playerCollectedFruits.put(player, currentCollected + 1);

                System.out.println("Fruta recolectada por jugador | Puntos: " + playerScores.get(player) +
                        " | Frutas: " + playerCollectedFruits.get(player));
            }
        }
    }

    /**
     * Verifica si el jugador debe morir por colision con cactus peligroso
     */
    public boolean checkDangerousCactusCollision(Player player) {
        for (Fruit fruit : fruits) {
            if (fruit.getType() == Fruit.FruitType.CACTUS && !fruit.isCollected()) {
                CactusFruit cactus = (CactusFruit) fruit;
                if (cactus.shouldKillPlayer(player.getX(), player.getY())) {
                    System.out.println("Jugador murio por cactus peligroso en: " + cactus.getGridPosition());
                    return true;
                }
            }
        }
        return false;
    }

    public List<Fruit> getFruits() {
        return fruits;
    }

    public boolean allFruitsCollected() {
        if (totalBananas > 0 && !bananasCompleted) {
            return false;
        }

        if (totalGrapes > 0) {
            if (!grapesSpawned || !grapesCompleted) {
                return false;
            }
        }

        if (totalCherries > 0) {
            if (!cherriesSpawned || !cherriesCompleted) {
                return false;
            }
        }

        if (totalPineapples > 0) {
            if (!pineapplesSpawned || !pineapplesCompleted) {
                return false;
            }
        }

        if (totalCactuses > 0) {
            if (!cactusesSpawned || !cactusesCompleted) {
                return false;
            }
        }

        int totalCollected = 0;
        for (int collected : playerCollectedFruits.values()) {
            totalCollected += collected;
        }

        return totalCollected >= totalFruits;
    }

    public boolean shouldSpawnNextWave() {
        if (!bananasCompleted) {
            return false;
        }

        if (totalGrapes > 0 && !grapesCompleted) {
            return false;
        }

        if (totalCherries > 0 && !cherriesCompleted) {
            return false;
        }

        if (totalPineapples > 0 && !pineapplesCompleted) {
            return false;
        }

        if (totalCactuses > 0 && !cactusesCompleted) {
            return false;
        }

        return true;
    }

    public int getScore(Player player) {
        return playerScores.getOrDefault(player, 0);
    }

    public int getCollectedCount(Player player) {
        return playerCollectedFruits.getOrDefault(player, 0);
    }

    public int getScore() {
        int total = 0;
        for (int score : playerScores.values()) {
            total += score;
        }
        return total;
    }

    public int getCollectedCount() {
        int total = 0;
        for (int collected : playerCollectedFruits.values()) {
            total += collected;
        }
        return total;
    }

    public int getTotalCount() {
        return totalFruits;
    }

    public boolean areGrapesSpawned() {
        return grapesSpawned;
    }

    public boolean areBananasCompleted() {
        return bananasCompleted;
    }

    public boolean areGrapesCompleted() {
        return grapesCompleted;
    }

    public boolean areCherriesSpawned() {
        return cherriesSpawned;
    }

    public boolean areCherriesCompleted() {
        return cherriesCompleted;
    }

    public boolean arePineapplesSpawned() {
        return pineapplesSpawned;
    }

    public boolean arePineapplesCompleted() {
        return pineapplesCompleted;
    }

    public boolean areCactusesSpawned() {
        return cactusesSpawned;
    }

    public boolean areCactusesCompleted() {
        return cactusesCompleted;
    }

    public void clear() {
        fruits.clear();
        playerScores.clear();
        playerCollectedFruits.clear();
        totalFruits = 0;

        totalBananas = 0;
        totalGrapes = 0;
        totalCherries = 0;
        totalPineapples = 0;
        totalCactuses = 0;

        bananasCompleted = false;
        grapesSpawned = false;
        grapesCompleted = false;
        cherriesSpawned = false;
        cherriesCompleted = false;
        pineapplesSpawned = false;
        pineapplesCompleted = false;
        cactusesSpawned = false;
        cactusesCompleted = false;
    }


    /**
     * Establece el puntaje de un jugador especifico
     */
    public void setPlayerScore(Player player, int score) {
        if (playerScores.containsKey(player)) {
            playerScores.put(player, score);
        }
    }

    /**
     * Establece las frutas recolectadas de un jugador especifico
     */
    public void setPlayerCollectedFruits(Player player, int collected) {
        if (playerCollectedFruits.containsKey(player)) {
            playerCollectedFruits.put(player, collected);
        }
    }

    public void setScore(int score) {
        if (!playerScores.isEmpty()) {
            Player firstPlayer = playerScores.keySet().iterator().next();
            playerScores.put(firstPlayer, score);
        }
    }

    public void setCollectedFruits(int collected) {
        if (!playerCollectedFruits.isEmpty()) {
            Player firstPlayer = playerCollectedFruits.keySet().iterator().next();
            playerCollectedFruits.put(firstPlayer, collected);
        }
    }

    public void setBananasCompleted(boolean completed) {
        this.bananasCompleted = completed;
    }

    public void setGrapesSpawned(boolean spawned) {
        this.grapesSpawned = spawned;
    }

    public void setCherriesSpawned(boolean spawned) {
        this.cherriesSpawned = spawned;
    }

    public void setPineapplesSpawned(boolean spawned) {
        this.pineapplesSpawned = spawned;
    }

    public void setCactusesSpawned(boolean spawned) {
        this.cactusesSpawned = spawned;
    }


    public void setGrapesCompleted(boolean completed) {
        this.grapesCompleted = completed;
    }

    public void setCherriesCompleted(boolean completed) {
        this.cherriesCompleted = completed;
    }

    public void setPineapplesCompleted(boolean completed) {
        this.pineapplesCompleted = completed;
    }

    public void setCactusesCompleted(boolean completed) {
        this.cactusesCompleted = completed;
    }


    public void setTotalFruits(int total) {
        this.totalFruits = total;
    }
}