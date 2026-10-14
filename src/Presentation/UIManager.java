package Presentation;

import Domain.*;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIManager {
    private List<PlayerScoreUI> scoreUIs;
    private PauseButton pauseButton;
    private GameTimer timeLimit;
    private int screenWidth;
    private int screenHeight;

    public UIManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scoreUIs = new ArrayList<>();
        this.pauseButton = new PauseButton(screenWidth, screenHeight);
        this.timeLimit = new GameTimer(screenWidth);
    }

    public void registerPlayerUI(String playerName, String playerFlavor, Player player, boolean isPlayer2) {
        PlayerScoreUI scoreUI = new PlayerScoreUI(playerName, playerFlavor, player, isPlayer2);
        scoreUIs.add(scoreUI);
    }

    public void drawUI(Graphics g, FruitManager fruitManager, EnemyManager enemyManager,
                       boolean gameActive) {
        // Dibujar puntajes de jugadores
        int player1X = 30;
        int player2X = 400;
        int playerY = 10;

        for (int i = 0; i < scoreUIs.size(); i++) {
            PlayerScoreUI scoreUI = scoreUIs.get(i);
            int posX = (i == 0) ? player1X : player2X;
            scoreUI.draw(g, fruitManager, posX, playerY);
        }

        // Dibujar contador de enemigos
        drawEnemyCounter(g, enemyManager);

        // Dibujar contador de frutas
        drawFruitCounter(g, fruitManager);

        // Dibujar timer si el juego esta activo
        if (gameActive && timeLimit != null) {
            timeLimit.draw(g);
        }

        // Dibujar boton de pausa
        if (pauseButton != null) {
            drawPauseButton(g);
        }
    }

    private void drawEnemyCounter(Graphics g, EnemyManager enemyManager) {
        if (enemyManager == null) return;

        Map<Enemy.EnemyType, Integer> enemyCounts = enemyManager.getEnemyCountsByType();

        if (enemyCounts.isEmpty()) return;

        int startX = 670;
        int startY = 25;
        int lineHeight = 22;
        int currentY = startY + lineHeight;

        Font countFont = new Font("Arial", Font.PLAIN, 15);
        g.setFont(countFont);

        for (Map.Entry<Enemy.EnemyType, Integer> entry : enemyCounts.entrySet()) {
            String enemyName = getEnemyDisplayName(entry.getKey());
            String text = "  " + enemyName + ": " + entry.getValue();

            g.setColor(Color.BLACK);
            g.drawString(text, startX + 1, currentY + 1);

            g.setColor(Color.WHITE);
            g.drawString(text, startX, currentY);

            currentY += lineHeight;
        }
    }

    private void drawFruitCounter(Graphics g, FruitManager fruitManager) {
        if (fruitManager == null) return;

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String fruitText = "Frutas: " + fruitManager.getCollectedCount() + "/" +
                fruitManager.getTotalCount();

        g.setColor(Color.BLACK);
        g.drawString(fruitText, 21, 735);

        g.setColor(Color.WHITE);
        g.drawString(fruitText, 20, 735);
    }

    private void drawPauseButton(Graphics g) {
        if (pauseButton == null) return;

        java.awt.Image buttonImage = pauseButton.getCurrentImage();
        if (buttonImage != null) {
            g.drawImage(buttonImage,
                    pauseButton.getX(),
                    pauseButton.getY(),
                    pauseButton.getWidth(),
                    pauseButton.getHeight(),
                    null);
        }
    }

    public void updateTimer() {
        if (timeLimit != null) {
            timeLimit.update();
        }
    }

    public void pauseTimer() {
        if (timeLimit != null) {
            timeLimit.pause();
        }
    }

    public void resumeTimer() {
        if (timeLimit != null) {
            timeLimit.resume();
        }
    }

    public boolean isTimeUp() {
        return timeLimit != null && timeLimit.isTimeUp();
    }

    public void resetTimer() {
        if (timeLimit != null) {
            timeLimit.reset();
        }
    }

    public boolean updatePauseButtonHover(java.awt.Point mousePos) {
        return pauseButton != null && pauseButton.updateHover(mousePos);
    }

    public boolean handlePauseButtonClick(java.awt.Point clickPos) {
        return pauseButton != null && pauseButton.handleClick(clickPos);
    }

    public boolean isPaused() {
        return pauseButton != null && pauseButton.isPaused();
    }

    public void pause() {
        if (pauseButton != null) {
            pauseButton.pause();
            pauseTimer();
        }
    }

    public void resume() {
        if (pauseButton != null) {
            pauseButton.resume();
            resumeTimer();
        }
    }

    public void togglePause() {
        if (pauseButton != null) {
            pauseButton.toggle();
            if (pauseButton.isPaused()) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        }
    }

    public void clear() {
        scoreUIs.clear();
        if (timeLimit != null) {
            timeLimit.reset();
        }
    }

    private String getEnemyDisplayName(Enemy.EnemyType type) {
        if (type == Enemy.EnemyType.TROLL) {
            return "Troll";
        }
        else if (type == Enemy.EnemyType.SQUID) {
            return "Calamar";
        }
        else if (type == Enemy.EnemyType.POT) {
            return "Maceta";
        }
        else if (type == Enemy.EnemyType.NARVAL) {
            return "Narval";
        }
        return "";
    }

    public PauseButton getPauseButton() {
        return pauseButton;
    }

    public GameTimer getGameTimer() {
        return timeLimit;
    }
}