package Presentation;

import javax.swing.*;

// Interfaz que define el comportamiento de todas las pantallas
public interface Screen {
    JPanel getPanel();
    void onShow();
    void onHide();
}