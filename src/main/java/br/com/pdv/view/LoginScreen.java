package br.com.pdv.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login - Sistema PDV");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela

        // Painel principal com GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Rótulo e campo de usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        userField = new JTextField(15);
        panel.add(userField, gbc);

        // Rótulo e campo de senha
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passField = new JPasswordField(15);
        panel.add(passField, gbc);

        // Botão de login
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Entrar");
        panel.add(loginButton, gbc);

        // Adiciona o painel ao frame
        add(panel);

        // Ação do botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // A lógica de autenticação real será adicionada aqui
                String username = userField.getText();
                String password = new String(passField.getPassword());

                // Simulação de autenticação para demonstração
                if ("admin".equals(username) && "admin".equals(password)) {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                            "Login bem-sucedido!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Abrir a tela principal
                    dispose(); // Fecha a tela de login
                    SwingUtilities.invokeLater(() -> {
                        MainScreen mainScreen = new MainScreen();
                        mainScreen.setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                            "Usuário ou senha inválidos.",
                            "Erro de Login",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
