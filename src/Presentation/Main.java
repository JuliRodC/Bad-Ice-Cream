package Presentation;

import javax.swing.*;

// Clase principal que crea la ventana y administra las pantallasg
public class Main {


    public Main() {
        JFrame frame = new JFrame("Bad Ice Cream");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(770, 790);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Crear administrador de pantallas
        ScreenManager screenManager = new ScreenManager(frame);

        // Mostrar pantalla inicial (menu)
        screenManager.showScreen("menu");

        frame.setVisible(true);
    }

    // Metodo main tradicion
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}



