package Presentation;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor centralizado de carga de sprites
 * Cachea imagenes para mejor rendimiento
 */
public class SpriteLoader {
    private static SpriteLoader instance;
    private Map<String, Image> cache;

    private SpriteLoader() {
        this.cache = new HashMap<>();
    }

    public static SpriteLoader getInstance() {
        if (instance == null) {
            instance = new SpriteLoader();
        }
        return instance;
    }

    /**
     * Carga una imagen desde ruta
     * Usa cache para no recargar
     */
    public Image loadSprite(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage();
                cache.put(path, img);
                return img;
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprite: " + path);
        }
        return null;
    }

    /**
     * Carga multiples sprites de una carpeta
     */
    public Image[] loadSpritesFromFolder(String folderPath, int frameCount) {
        Image[] sprites = new Image[frameCount];
        for (int i = 0; i < frameCount; i++) {
            String path = folderPath + "/" + (i + 1) + ".png";
            sprites[i] = loadSprite(path);
        }
        return sprites;
    }

    /**
     * Limpia el cache
     */
    public void clearCache() {
        cache.clear();
    }
}