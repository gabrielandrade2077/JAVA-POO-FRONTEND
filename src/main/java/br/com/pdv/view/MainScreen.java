package br.com.pdv.view;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    public MainScreen(String loggedInUser) {
        setTitle("Sistema PDV - Menu Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel superior para o nome de usuário
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(240, 240, 240));
        JLabel userLabel = new JLabel("Usuário: " + loggedInUser);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(userLabel);
        add(topPanel, BorderLayout.NORTH);


        // Painel principal com GridBagLayout para centralizar os botões
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Um fundo mais suave
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); // Espaçamento vertical entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza na célula
        gbc.gridx = 0; // Todos na mesma coluna

        Font buttonFont = new Font("SansSerif", Font.BOLD, 28);
        Dimension buttonSize = new Dimension(300, 80); // Tamanho fixo para ambos os botões

        // Mensagem de Boas-vindas
        JLabel welcomeLabel = new JLabel("Bem-vindo ao PDV Posto de Combustível!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32)); // Tamanho da fonte aumentado para 32
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; // Primeira linha
        gbc.insets = new Insets(0, 0, 30, 0); // Espaçamento maior abaixo da mensagem
        mainPanel.add(welcomeLabel, gbc);

        // Botão Cadastros
        JButton btnCadastros = new JButton("Cadastros");
        btnCadastros.setFont(buttonFont);
        btnCadastros.setPreferredSize(buttonSize);
        btnCadastros.setBackground(new Color(70, 130, 180)); // SteelBlue
        btnCadastros.setForeground(Color.WHITE);
        btnCadastros.setFocusPainted(false);
        btnCadastros.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        gbc.gridy = 1; // Segunda linha
        gbc.insets = new Insets(15, 0, 15, 0); // Espaçamento vertical entre os botões
        mainPanel.add(btnCadastros, gbc);

        // Botão Vendas
        JButton btnVendas = new JButton("Central de Abastecimento");
        btnVendas.setFont(buttonFont);
        btnVendas.setPreferredSize(buttonSize);
        btnVendas.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        btnVendas.setForeground(Color.WHITE);
        btnVendas.setFocusPainted(false);
        btnVendas.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        gbc.gridy = 2; // Terceira linha
        mainPanel.add(btnVendas, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Ações dos botões
        btnCadastros.addActionListener(e -> abrirJanela("Menu de Cadastros", new CadastroMenuPanel(this), 300, 450));
        btnVendas.addActionListener(e -> abrirJanela("Menu de Vendas", new VendasMenuPanel(this), 300, 200));
    }

    // Método auxiliar para abrir janelas com tamanho customizado
    private void abrirJanela(String titulo, JPanel painel, int width, int height) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(painel);

        // Define um tamanho maior para a tela de PDV para melhor visualização
        if (painel instanceof PdvScreen) {
            frame.setSize(850, 650);
        } else {
            frame.setSize(width, height);
        }

        frame.setLocationRelativeTo(this); // Centraliza em relação à tela principal
        frame.setVisible(true);
    }
}