package Domain;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Implementacion de la fruta Banana (estatica con animacion)
 * No se mueve, pero tiene sprites animados
 * Otorga 100 puntos al ser recolectada
 */
public class BananaFruit extends Fruit {
    private Image[] sprites;
    private static final int ANIMATION_SPEED = 7;
    private static final int TOTAL_FRAMES = 5;
    private static final int POINTS = 100;
    private static final int FRUIT_SIZE = 56;

    public BananaFruit(int gridX, int gridY, GridSystem grid) {
        super(gridX, gridY, grid);
        loadSprites();
    }

    private void loadSprites() {
        sprites = new Image[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            String path = "Images/Fruits/Banana/" + (i + 1) + ".png";
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) {
                    sprites[i] = icon.getImage();
                } else {
                    System.out.println("Advertencia: No se pudo cargar " + path);
                }
            } catch (Exception e) {
                System.out.println("Error cargando sprite de banana: " + path);
            }
        }

        System.out.println("Sprites de banana cargados: " + TOTAL_FRAMES + " frames");
    }

    @Override
    public void update() {
        if (collected) {
            return;
        }
        updateAnimation(ANIMATION_SPEED, TOTAL_FRAMES);
    }

    @Override
    public Image getCurrentSprite() {
        if (sprites != null && sprites.length > 0 && sprites[animationFrame] != null) {
            return sprites[animationFrame];
        }
        return null;
    }

    @Override
    public FruitType getType() {
        return FruitType.BANANA;
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public int getWidth() {
        return FRUIT_SIZE;
    }

    @Override
    public int getHeight() {
        return FRUIT_SIZE;
    }
}