package br.com.pdv.view;

import br.com.pdv.model.Preco;
import br.com.pdv.model.Produto;
import br.com.pdv.service.PrecoService;
import br.com.pdv.service.ProdutoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrecoScreen extends JPanel {

    private PrecoService precoService;
    private ProdutoService produtoService;

    // Componentes do formulário
    private JComboBox<Produto> produtoComboBox;
    private JTextField valorField;
    private JButton salvarButton;

    // Componentes da tabela
    private JTable tabelaPrecos;
    private DefaultTableModel tableModel;

    public PrecoScreen() {
        this.precoService = new PrecoService();
        this.produtoService = new ProdutoService(); // Para carregar os produtos
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        adicionarListeners();
        carregarProdutos();
        atualizarTabela();
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Definir Novo Preço"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Produto e Valor
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; produtoComboBox = new JComboBox<>(); panel.add(produtoComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Novo Preço (R$):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; valorField = new JTextField(10); panel.add(valorField, gbc);

        // Linha 2: Botão Salvar
        salvarButton = new JButton("Salvar Preço");
        gbc.gridx = 3; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(salvarButton, gbc);

        // Custom renderer para o ComboBox
        produtoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produto) {
                    setText(((Produto) value).getNome());
                }
                return this;
            }
        });

        return panel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Histórico de Preços"));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Data", "Produto", "Valor"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPrecos = new JTable(tableModel);

        panel.add(new JScrollPane(tabelaPrecos), BorderLayout.CENTER);
        return panel;
    }

    private void adicionarListeners() {
        salvarButton.addActionListener(e -> salvarPreco());
    }

    private void carregarProdutos() {
        List<Produto> produtos = produtoService.listarProdutos();
        produtoComboBox.removeAllItems();
        if (produtos != null) {
            for (Produto p : produtos) {
                produtoComboBox.addItem(p);
            }
        }
    }

    private void salvarPreco() {
        Produto produtoSelecionado = (Produto) produtoComboBox.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Preco preco = new Preco();
            preco.setProduto(produtoSelecionado);
            preco.setValor(new BigDecimal(valorField.getText()));
            preco.setDataAlteracao(LocalDate.now());

            Preco precoSalvo = precoService.salvarPreco(preco);
            if (precoSalvo != null) {
                JOptionPane.showMessageDialog(this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                valorField.setText("");
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o preço.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Preco> listaPrecos = precoService.listarPrecos();

        if (listaPrecos != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Preco p : listaPrecos) {
                String nomeProduto = (p.getProduto() != null) ? p.getProduto().getNome() : "N/A";
                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getDataAlteracao().format(formatter),
                        nomeProduto,
                        p.getValor()
                });
            }
        }
    }
}
