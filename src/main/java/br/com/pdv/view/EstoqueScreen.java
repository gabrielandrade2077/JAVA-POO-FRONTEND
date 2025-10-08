package br.com.pdv.view;

import br.com.pdv.model.Estoque;
import br.com.pdv.model.Produto;
import br.com.pdv.service.EstoqueService;
import br.com.pdv.service.ProdutoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EstoqueScreen extends JPanel {

    private EstoqueService estoqueService;
    private ProdutoService produtoService;

    // Componentes do formulário
    private JComboBox<Produto> produtoComboBox;
    private JTextField quantidadeField, localTanqueField, loteField, dataValidadeField;
    private JButton salvarButton;

    // Componentes da tabela
    private JTable tabelaEstoque;
    private DefaultTableModel tableModel;

    public EstoqueScreen() {
        this.estoqueService = new EstoqueService();
        this.produtoService = new ProdutoService();
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
        panel.setBorder(BorderFactory.createTitledBorder("Entrada de Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Produto e Quantidade
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; produtoComboBox = new JComboBox<>(); panel.add(produtoComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; quantidadeField = new JTextField(10); panel.add(quantidadeField, gbc);

        // Linha 2: Lote e Validade
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Lote:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; loteField = new JTextField(15); panel.add(loteField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Validade (AAAA-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; dataValidadeField = new JTextField(10); panel.add(dataValidadeField, gbc);

        // Linha 3: Local/Tanque e Botão
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Local/Tanque:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; localTanqueField = new JTextField(15); panel.add(localTanqueField, gbc);

        salvarButton = new JButton("Adicionar ao Estoque");
        gbc.gridx = 3; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
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
        panel.setBorder(BorderFactory.createTitledBorder("Visão Geral do Estoque"));

        tableModel = new DefaultTableModel(new Object[]{"Produto", "Quantidade", "Local", "Lote", "Validade"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaEstoque = new JTable(tableModel);

        panel.add(new JScrollPane(tabelaEstoque), BorderLayout.CENTER);
        return panel;
    }

    private void adicionarListeners() {
        salvarButton.addActionListener(e -> salvarEstoque());
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

    private void salvarEstoque() {
        Produto produtoSelecionado = (Produto) produtoComboBox.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Estoque estoque = new Estoque();
            estoque.setProduto(produtoSelecionado);
            estoque.setQuantidade(new BigDecimal(quantidadeField.getText()));
            estoque.setLoteFabricacao(loteField.getText());
            estoque.setLocalTanque(localTanqueField.getText());
            if (!dataValidadeField.getText().trim().isEmpty()) {
                estoque.setDataValidade(LocalDate.parse(dataValidadeField.getText()));
            }

            Estoque estoqueSalvo = estoqueService.adicionarItemEstoque(estoque);
            if (estoqueSalvo != null) {
                JOptionPane.showMessageDialog(this, "Item adicionado ao estoque com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar item ao estoque.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "A data de validade deve estar no formato AAAA-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        quantidadeField.setText("");
        loteField.setText("");
        localTanqueField.setText("");
        dataValidadeField.setText("");
        produtoComboBox.setSelectedIndex(0);
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Estoque> listaEstoque = estoqueService.listarEstoque();

        if (listaEstoque != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Estoque e : listaEstoque) {
                String nomeProduto = (e.getProduto() != null) ? e.getProduto().getNome() : "N/A";
                String dataFormatada = (e.getDataValidade() != null) ? e.getDataValidade().format(formatter) : "N/A";
                tableModel.addRow(new Object[]{
                        nomeProduto,
                        e.getQuantidade(),
                        e.getLocalTanque(),
                        e.getLoteFabricacao(),
                        dataFormatada
                });
            }
        }
    }
}
