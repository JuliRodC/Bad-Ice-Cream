package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;

/**
 * Temporizador del juego con animacion de reloj
 * Cuenta regresiva desde 3 minutos (180 segundos)
 * Muestra sprites animados del reloj
 */
public class GameTimer {
    private long startTime;
    private long pausedTime;
    private boolean paused;
    private boolean timeUp;

    private static final long GAME_DURATION_MS = 180000; // 3 minutos en milisegundos

    // Animacion del reloj
    private Image[] clockSprites;
    private int currentFrame;
    private int animationCounter;
    private static final int ANIMATION_SPEED = 4;
    private static final int TOTAL_FRAMES = 16;

    // Posicion en pantalla
    private int x;
    private int y;
    private static final int CLOCK_SIZE = 40;

    public GameTimer(int screenWidth) {
        this.startTime = System.currentTimeMillis();
        this.pausedTime = 0;
        this.paused = false;
        this.timeUp = false;
        this.currentFrame = 0;
        this.animationCounter = 0;

        // Posicionar a la izquierda del boton de pausa
        this.x = 540;
        this.y = 30;

        loadClockSprites();
    }

    private void loadClockSprites() {
        clockSprites = new Image[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            String path = "Images/Resources/Clocks/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    clockSprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de reloj: " + path);
            }
        }

        System.out.println("Sprites de reloj cargados: " + TOTAL_FRAMES + " frames");
    }

    /**
     * Actualiza la animacion del reloj
     */
    public void update() {
        if (paused || timeUp) {
            return;
        }

        // Actualizar animacion
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            currentFrame++;

            if (currentFrame >= TOTAL_FRAMES) {
                currentFrame = 0;
            }
        }

        // Verificar si se acabo el tiempo
        if (getRemainingTimeMs() <= 0) {
            timeUp = true;
            System.out.println("Tiempo agotado!");
        }
    }

    /**
     * Pausa el temporizador
     */
    public void pause() {
        if (!paused) {
            paused = true;
            pausedTime = System.currentTimeMillis();
        }
    }

    /**
     * Reanuda el temporizador
     */
    public void resume() {
        if (paused) {
            long pauseDuration = System.currentTimeMillis() - pausedTime;
            startTime += pauseDuration;
            paused = false;
        }
    }

    /**
     * Obtiene el tiempo restante en milisegundos
     */
    public long getRemainingTimeMs() {
        if (timeUp) {
            return 0;
        }

        long currentTime = paused ? pausedTime : System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        long remaining = GAME_DURATION_MS - elapsed;

        return Math.max(0, remaining);
    }

    /**
     * Establece el tiempo restante en milisegundos (para cargar partidas)
     */
    public void setRemainingTime(long remainingMs) {
        long currentTime = System.currentTimeMillis();
        this.startTime = currentTime - (GAME_DURATION_MS - remainingMs);
        this.timeUp = remainingMs <= 0;

        System.out.println("Tiempo restante establecido: " + remainingMs + "ms");
    }

    /**
     * Obtiene los minutos restantes
     */
    public int getRemainingMinutes() {
        long remainingMs = getRemainingTimeMs();
        return (int) (remainingMs / 60000);
    }

    /**
     * Obtiene los segundos restantes (sin contar minutos)
     */
    public int getRemainingSeconds() {
        long remainingMs = getRemainingTimeMs();
        return (int) ((remainingMs % 60000) / 1000);
    }

    /**
     * Verifica si el tiempo se acabo
     */
    public boolean isTimeUp() {
        return timeUp;
    }

    /**
     * Dibuja el reloj y el tiempo en pantalla
     */
    public void draw(Graphics g) {
        // Dibujar sprite del reloj
        if (clockSprites != null && clockSprites[currentFrame] != null) {
            g.drawImage(clockSprites[currentFrame], x, y, CLOCK_SIZE, CLOCK_SIZE, null);
        }

        // Dibujar tiempo
        int minutes = getRemainingMinutes();
        int seconds = getRemainingSeconds();
        String timeText = String.format("%d:%02d", minutes, seconds);

        // Fuente y color
        Font timeFont = new Font("Arial", Font.BOLD, 24);
        g.setFont(timeFont);

        // Posicion del texto (a la derecha del reloj)
        int textX = x + CLOCK_SIZE + 8;
        int textY = y + 22;

        // Sombra del texto
        g.setColor(Color.BLACK);
        g.drawString(timeText, textX + 1, textY + 1);

        // Texto principal (cambiar a rojo si queda menos de 30 segundos)
        long remainingMs = getRemainingTimeMs();
        if (remainingMs <= 30000) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }
        g.drawString(timeText, textX, textY);
    }

    /**
     * Reinicia el temporizador
     */
    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.pausedTime = 0;
        this.paused = false;
        this.timeUp = false;
        this.currentFrame = 0;
        this.animationCounter = 0;
    }
}