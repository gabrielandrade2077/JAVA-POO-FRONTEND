package br.com.pdv.view;

import javax.swing.*;
import java.awt.*;

public class VendasMenuPanel extends JPanel {

    public VendasMenuPanel(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Espaçamento entre os botões
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        JButton btnPdv = new JButton("Abastecimento");
        btnPdv.setFont(buttonFont);
        btnPdv.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 0;
        add(btnPdv, gbc);

        // Adicionar Listener
        btnPdv.addActionListener(e -> abrirJanela("Ponto de Venda", new PdvScreen(), parentFrame));
    }

    private void abrirJanela(String titulo, JPanel painel, JFrame parentFrame) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(painel);

        // Define um tamanho maior para a tela de PDV para melhor visualização
        if (painel instanceof PdvScreen) {
            frame.setSize(850, 650);
        } else {
            frame.pack();
        }

        frame.setLocationRelativeTo(parentFrame); // Centraliza em relação à tela pai
        frame.setVisible(true);
    }
}
