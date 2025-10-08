package br.com.pdv.view;

import br.com.pdv.model.Produto;
import br.com.pdv.service.ProdutoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoScreen extends JPanel {

    private ProdutoService produtoService;

    // Componentes do formulário
    private JTextField idField, nomeField, referenciaField, fornecedorField, categoriaField, marcaField;
    private JButton salvarButton, limparButton, deletarButton;

    // Componentes da tabela
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private List<Produto> listaProdutos = new ArrayList<>();

    public ProdutoScreen() {
        this.produtoService = new ProdutoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        adicionarListeners();
        atualizarTabela();
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField();
        idField.setVisible(false);

        // Linha 1: Nome e Referência
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; nomeField = new JTextField(20); panel.add(nomeField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Referência:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; referenciaField = new JTextField(15); panel.add(referenciaField, gbc);

        // Linha 2: Fornecedor e Categoria
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Fornecedor:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; fornecedorField = new JTextField(20); panel.add(fornecedorField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; categoriaField = new JTextField(15); panel.add(categoriaField, gbc);

        // Linha 3: Marca
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; marcaField = new JTextField(20); panel.add(marcaField, gbc);

        // Linha 4: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        limparButton = new JButton("Limpar");
        deletarButton = new JButton("Deletar");
        painelBotoes.add(salvarButton);
        painelBotoes.add(limparButton);
        painelBotoes.add(deletarButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(painelBotoes, gbc);

        return panel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Produtos Cadastrados"));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Referência", "Marca"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(tableModel);

        panel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);
        return panel;
    }

    private void adicionarListeners() {
        salvarButton.addActionListener(e -> salvarProduto());
        limparButton.addActionListener(e -> limparFormulario());
        deletarButton.addActionListener(e -> deletarProduto());

        tabelaProdutos.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tabelaProdutos.getSelectedRow() != -1) {
                int selectedRow = tabelaProdutos.getSelectedRow();
                Produto produtoSelecionado = listaProdutos.get(selectedRow);
                preencherFormulario(produtoSelecionado);
            }
        });
    }

    private void limparFormulario() {
        idField.setText("");
        nomeField.setText("");
        referenciaField.setText("");
        fornecedorField.setText("");
        categoriaField.setText("");
        marcaField.setText("");
        tabelaProdutos.clearSelection();
        nomeField.requestFocus();
    }

    private void preencherFormulario(Produto produto) {
        idField.setText(String.valueOf(produto.getId()));
        nomeField.setText(produto.getNome());
        referenciaField.setText(produto.getReferencia());
        fornecedorField.setText(produto.getFornecedor());
        categoriaField.setText(produto.getCategoria());
        marcaField.setText(produto.getMarca());
    }

    private void salvarProduto() {
        if (nomeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do produto é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Produto produto = new Produto();
        produto.setNome(nomeField.getText());
        produto.setReferencia(referenciaField.getText());
        produto.setFornecedor(fornecedorField.getText());
        produto.setCategoria(categoriaField.getText());
        produto.setMarca(marcaField.getText());

        if (!idField.getText().isEmpty()) {
            produto.setId(Long.parseLong(idField.getText()));
        }

        Produto produtoSalvo = produtoService.salvarProduto(produto);
        if (produtoSalvo != null) {
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o produto.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        limparFormulario();
        atualizarTabela();
    }

    private void deletarProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o produto selecionado?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Produto produtoSelecionado = listaProdutos.get(selectedRow);
            produtoService.deletarProduto(produtoSelecionado.getId());
            JOptionPane.showMessageDialog(this, "Produto deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
            limparFormulario();
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        listaProdutos = produtoService.listarProdutos();

        if (listaProdutos != null) {
            for (Produto p : listaProdutos) {
                tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getReferencia(), p.getMarca()});
            }
        }
    }
}
