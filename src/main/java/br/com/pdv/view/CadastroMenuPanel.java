package br.com.pdv.view;

import javax.swing.*;
import java.awt.*;

public class CadastroMenuPanel extends JPanel {

    public CadastroMenuPanel(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Espaçamento entre os botões
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        JButton btnPessoas = new JButton("Pessoas");
        btnPessoas.setFont(buttonFont);
        btnPessoas.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 0;
        add(btnPessoas, gbc);

        JButton btnProdutos = new JButton("Produtos");
        btnProdutos.setFont(buttonFont);
        btnProdutos.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 1;
        add(btnProdutos, gbc);

        JButton btnCustos = new JButton("Custos");
        btnCustos.setFont(buttonFont);
        btnCustos.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 2;
        add(btnCustos, gbc);

        JButton btnPrecos = new JButton("Preços");
        btnPrecos.setFont(buttonFont);
        btnPrecos.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 3;
        add(btnPrecos, gbc);

        JButton btnEstoque = new JButton("Estoque");
        btnEstoque.setFont(buttonFont);
        btnEstoque.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = 4;
        add(btnEstoque, gbc);

        // Adicionar Listeners
        btnPessoas.addActionListener(e -> abrirJanela("Cadastro de Pessoas", new PessoaScreen(), parentFrame));
        btnProdutos.addActionListener(e -> abrirJanela("Cadastro de Produtos", new ProdutoScreen(), parentFrame));
        btnCustos.addActionListener(e -> abrirJanela("Gerenciamento de Custos", new CustoScreen(), parentFrame));
        btnPrecos.addActionListener(e -> abrirJanela("Gerenciamento de Preços", new PrecoScreen(), parentFrame));
        btnEstoque.addActionListener(e -> abrirJanela("Gerenciamento de Estoque", new EstoqueScreen(), parentFrame));
    }

    private void abrirJanela(String titulo, JPanel painel, JFrame parentFrame) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(painel);
        frame.pack();
        frame.setLocationRelativeTo(parentFrame); // Centraliza em relação à tela pai
        frame.setVisible(true);
    }
}
