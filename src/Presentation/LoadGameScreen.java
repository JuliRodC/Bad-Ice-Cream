package Presentation;

import Domain.GameController;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Pantalla para cargar partidas guardadas
 * Permite al usuario seleccionar un archivo .sav y cargar el estado del juego
 */
public class LoadGameScreen implements Screen {
    private JPanel panel;
    private ScreenManager screenManager;

    public LoadGameScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        initializePanel();
    }

    private void initializePanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(770, 790));
        panel.setBackground(Color.BLACK);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onShow() {
        System.out.println("Mostrando dialogo de carga");
        openLoadDialog();
    }

    @Override
    public void onHide() {
        System.out.println("Ocultando pantalla de carga");
    }

    /**
     * Abre el dialogo de seleccion de archivo para cargar una partida
     */
    private void openLoadDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar partida guardada");

        // Establecer directorio inicial en la carpeta de guardados
        File saveDir = new File("saves");
        if (saveDir.exists() && saveDir.isDirectory()) {
            fileChooser.setCurrentDirectory(saveDir);
        }

        // Filtro para solo mostrar archivos .sav
        javax.swing.filechooser.FileNameExtensionFilter filter =
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Archivos de guardado (*.sav)", "sav");
        fileChooser.setFileFilter(filter);

        // Mostrar el dialogo
        int userSelection = fileChooser.showOpenDialog(panel);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String filepath = fileToLoad.getAbsolutePath();

            System.out.println("Intentando cargar partida desde: " + filepath);
            loadGameFromFile(filepath);
        } else {
            System.out.println("Carga cancelada por el usuario");
            screenManager.showScreen("menu");
        }
    }

    /**
     * Carga el estado del juego desde un archivo
     * @param filepath ruta completa del archivo a cargar
     */
    private void loadGameFromFile(String filepath) {
        // Extraer solo el nombre del archivo sin la extension
        File file = new File(filepath);
        String filename = file.getName();
        if (filename.endsWith(".sav")) {
            filename = filename.substring(0, filename.length() - 4);
        }

        // Marcar que vamos a cargar un save
        screenManager.setLoadedSaveFile(filename);

        JOptionPane.showMessageDialog(panel,
                "Partida cargada exitosamente!",
                "Carga exitosa",
                JOptionPane.INFORMATION_MESSAGE);

        // Ir directamente al juego
        screenManager.showScreen("game");
    }
}