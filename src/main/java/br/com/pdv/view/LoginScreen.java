package br.com.pdv.view;

import br.com.pdv.view.MainScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginScreen extends JFrame {

    // Cores e Fontes (Estilo Moderno)
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Cinza claro
    private static final Color PRIMARY_COLOR = new Color(60, 90, 180); // Azul corporativo
    private static final Color TEXT_COLOR = new Color(30, 30, 30); // Cinza escuro
    private static final Color FIELD_BACKGROUND_COLOR = Color.WHITE;
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login - PDV System");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel principal com fundo e borda
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        add(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("Bem-vindo", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Painel de formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Rótulo e campo de usuário
        JLabel userLabel = new JLabel("Usuário");
        userLabel.setFont(FONT_LABEL);
        userLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(userLabel, gbc);

        userField = new JTextField(20);
        userField.setFont(FONT_FIELD);
        userField.setBackground(FIELD_BACKGROUND_COLOR);
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(userField, gbc);

        // Rótulo e campo de senha
        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(FONT_LABEL);
        passLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        passField = new JPasswordField(20);
        passField.setFont(FONT_FIELD);
        passField.setBackground(FIELD_BACKGROUND_COLOR);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passField, gbc);

        // Espaçador
        gbc.gridy = 4;
        formPanel.add(new Box.Filler(new Dimension(0, 10), new Dimension(0, 10), new Dimension(0, 10)), gbc);

        // Botão de login
        loginButton = new JButton("Entrar");
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Ação do botão de login
        loginButton.addActionListener(this::performLogin);
        passField.addActionListener(this::performLogin); // Permite login com Enter no campo de senha

        // Define o botão de login como o padrão
        getRootPane().setDefaultButton(loginButton);
    }

    private void performLogin(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // Simulação de autenticação
        if (("admin".equals(username) && "admin".equals(password)) || ("teste".equals(username) && "teste".equals(password))) {
            dispose(); // Fecha a tela de login
            SwingUtilities.invokeLater(() -> {
                MainScreen mainScreen = new MainScreen(username);
                mainScreen.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
