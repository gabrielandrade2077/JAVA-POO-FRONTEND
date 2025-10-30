package br.com;

import br.com.pdv.view.MainScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font("Arial", Font.PLAIN, 16);

        JLabel usernameLabel = new JLabel("Usuário:");
        usernameLabel.setFont(font);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(font);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(font);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(font);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(font);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if ("admin".equals(username) && "admin".equals(password)) {
                    JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
                    dispose();
                    new MainScreen(username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Nome de usuário ou senha inválido");
                }
            }
        });
        panel.add(loginButton);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(font);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(cancelButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}
