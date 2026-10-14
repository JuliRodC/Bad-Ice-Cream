package Presentation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Administrador que maneja el cambio entre pantallas usando polimorfismo
 * Ahora limpia correctamente los datos de sesion al volver al menu
 */
public class ScreenManager {
    private JFrame frame;
    Map<String, Screen> screens;
    private Screen currentScreen;
    private GameSessionData sessionData;
    private String loadedSaveFilename;

    public ScreenManager(JFrame frame) {
        this.frame = frame;
        this.screens = new HashMap<>();
        this.sessionData = new GameSessionData();
        initializeScreens();
    }

    public GameSessionData getSessionData() {
        return sessionData;
    }

    private void initializeScreens() {
        screens.put("menu", new MenuScreen(this));
        screens.put("options", new OptionsScreen(this));
        screens.put("game", new GameScreen(this));
        screens.put("gameType", new GameTypeScreen(this));
        screens.put("flavorSelection", new FlavorSelectionScreen(this));
        screens.put("levelSelection", new LevelSelectionScreen(this));
        screens.put("settings", new SettingsScreen(this));
        addScreen("loadGame", new LoadGameScreen(this));

        TwoPlayerModeScreen twoPlayerMode = new TwoPlayerModeScreen(this);
        screens.put("twoPlayerMode", twoPlayerMode);
        screens.put("twoPlayerFlavorSelection", new TwoPlayerFlavorSelectionScreen(this, twoPlayerMode));
    }

    public void showScreen(String screenName) {
        Screen newScreen = screens.get(screenName);

        if (newScreen == null) {
            System.out.println("Error: Pantalla '" + screenName + "' no existe");
            return;
        }

        // LIMPIEZA DE DATOS: Si volvemos al menu, opciones o gameType, resetear sesion
        if ("menu".equals(screenName) || "options".equals(screenName)) {
            System.out.println("=== LIMPIANDO DATOS DE SESION ===");
            resetSessionData();
        }

        // Si vamos a gameType (seleccion de cantidad de jugadores), tambien limpiar
        if ("gameType".equals(screenName)) {
            System.out.println("=== LIMPIANDO DATOS DE SESION (nuevo juego) ===");
            resetSessionData();
        }

        // Ocultar pantalla actual
        if (currentScreen != null) {
            currentScreen.onHide();
            frame.remove(currentScreen.getPanel());
        }

        // Mostrar nueva pantalla
        currentScreen = newScreen;
        currentScreen.onShow();
        frame.add(currentScreen.getPanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Resetea los datos de sesion a valores por defecto
     */
    private void resetSessionData() {
        // Usar el metodo resetToDefaults() en lugar de crear nueva instancia
        this.sessionData.resetToDefaults();

        // Limpiar datos de partida cargada
        clearLoadedSaveData();

        System.out.println("=== Datos de sesion reseteados ===");
        System.out.println("  Modo de juego: " + sessionData.getGameMode());
        System.out.println("  Es modo 2 jugadores: " + sessionData.isTwoPlayerMode());
        System.out.println("  Player1 Name: " + sessionData.getPlayer1Name());
        System.out.println("  Player2 Name: " + sessionData.getPlayer2Name());
    }

    public void setLoadedSaveFile(String filename) {
        this.loadedSaveFilename = filename;
    }

    public String getLoadedSaveFile() {
        return loadedSaveFilename;
    }

    /**
     * Limpia los datos de partida cargada
     */
    public void clearLoadedSaveData() {
        this.loadedSaveFilename = null;
    }

    public void addScreen(String name, Screen screen) {
        screens.put(name, screen);
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }
}