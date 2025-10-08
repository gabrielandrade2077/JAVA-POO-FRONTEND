package br.com;

import br.com.pdv.view.MainScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Garante que a interface gráfica seja executada na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            MainScreen mainScreen = new MainScreen();
            mainScreen.setVisible(true);
        });
    }
}
