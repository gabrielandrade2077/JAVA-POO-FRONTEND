package br.com.pdv.view;

import br.com.pdv.model.Preco;
import br.com.pdv.model.Produto;
import br.com.pdv.model.VendaItem;
import br.com.pdv.service.PrecoService;
import br.com.pdv.service.ProdutoService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PdvScreen extends JPanel {

    private final ProdutoService produtoService;
    private final PrecoService precoService;

    private final Map<Long, BigDecimal> precosAtuais = new ConcurrentHashMap<>();
    private final List<VendaItem> itensVenda = new ArrayList<>();

    private JComboBox<Produto> produtoComboBox;
    private JTextField quantidadeField, valorField;
    private JButton adicionarButton;
    private JTable tabelaVenda;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JButton finalizarButton, cancelarButton;

    private final AtomicBoolean isUpdating = new AtomicBoolean(false);
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public PdvScreen() {
        this.produtoService = new ProdutoService();
        this.precoService = new PrecoService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadInitialData();
        addListeners();
    }

    private void loadInitialData() {
        List<Preco> precos = precoService.listarPrecos();
        if (precos != null) {
            precos.sort(Comparator.comparing(Preco::getDataAlteracao).reversed());
            for (Preco preco : precos) {
                if (preco.getProduto() != null) {
                    precosAtuais.putIfAbsent(preco.getProduto().getId(), preco.getValor());
                }
            }
        }

        List<Produto> produtos = produtoService.listarProdutos();
        produtoComboBox.removeAllItems();
        if (produtos != null) {
            for (Produto p : produtos) {
                if (precosAtuais.containsKey(p.getId())) {
                    produtoComboBox.addItem(p);
                }
            }
        }
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Adicionar Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        produtoComboBox = new JComboBox<>();
        quantidadeField = new JTextField(10);
        valorField = new JTextField(10);
        adicionarButton = new JButton("Adicionar");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; panel.add(produtoComboBox, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(quantidadeField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; panel.add(valorField, gbc);

        gbc.gridx = 3; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; panel.add(adicionarButton, gbc);

        produtoComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Produto) setText(((Produto) v).getNome());
                return this;
            }
        });

        return panel;
    }

    private JPanel createCenterPanel() {
        tableModel = new DefaultTableModel(new Object[]{"Produto", "Qtd.", "Preço Unit.", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaVenda = new JTable(tableModel);
        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(tabelaVenda), BorderLayout.CENTER); }};
    }

    private JPanel createBottomPanel() {
        totalLabel = new JLabel("Total: R$ 0,00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        finalizarButton = new JButton("Finalizar Venda");
        cancelarButton = new JButton("Cancelar Venda");

        return new JPanel(new BorderLayout()) {{ 
            add(totalLabel, BorderLayout.NORTH); 
            add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{ add(cancelarButton); add(finalizarButton); }}, BorderLayout.SOUTH);
        }};
    }

    private void addListeners() {
        quantidadeField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { calculate(true); }
            public void removeUpdate(DocumentEvent e) { calculate(true); }
            public void insertUpdate(DocumentEvent e) { calculate(true); }
        });
        valorField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { calculate(false); }
            public void removeUpdate(DocumentEvent e) { calculate(false); }
            public void insertUpdate(DocumentEvent e) { calculate(false); }
        });

        adicionarButton.addActionListener(e -> adicionarItem());
        finalizarButton.addActionListener(e -> finalizarVenda());
        cancelarButton.addActionListener(e -> limparVenda());
    }

    private void calculate(boolean isQuantidadeSource) {
        if (isUpdating.get()) return;

        SwingUtilities.invokeLater(() -> {
            Produto produto = (Produto) produtoComboBox.getSelectedItem();
            if (produto == null) return;

            BigDecimal precoUnitario = precosAtuais.get(produto.getId());
            if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) == 0) return;

            isUpdating.set(true);
            try {
                if (isQuantidadeSource) {
                    String qtdText = quantidadeField.getText().replace(',', '.');
                    if (!qtdText.isEmpty()) {
                        BigDecimal quantidade = new BigDecimal(qtdText);
                        BigDecimal valor = quantidade.multiply(precoUnitario);
                        valorField.setText(String.format("%.2f", valor));
                    }
                } else {
                    String valorText = valorField.getText().replace(',', '.');
                    if (!valorText.isEmpty()) {
                        BigDecimal valor = new BigDecimal(valorText);
                        BigDecimal quantidade = valor.divide(precoUnitario, 4, RoundingMode.HALF_UP);
                        quantidadeField.setText(String.format("%.4f", quantidade));
                    }
                }
            } catch (NumberFormatException | ArithmeticException ignored) {
                // Ignora erros de formatação ou divisão por zero enquanto o usuário digita
            } finally {
                isUpdating.set(false);
            }
        });
    }

    private void adicionarItem() {
        Produto produto = (Produto) produtoComboBox.getSelectedItem();
        if (produto == null) return;

        try {
            BigDecimal quantidade = new BigDecimal(quantidadeField.getText().replace(',', '.'));
            BigDecimal precoUnitario = precosAtuais.get(produto.getId());

            VendaItem item = new VendaItem(produto, quantidade, precoUnitario);
            itensVenda.add(item);

            tableModel.addRow(new Object[]{
                item.getProduto().getNome(),
                String.format("%.3f", item.getQuantidade()),
                currencyFormat.format(item.getPrecoUnitario()),
                currencyFormat.format(item.getSubtotal())
            });

            atualizarTotal();
            limparCamposItem();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade ou valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTotal() {
        BigDecimal total = itensVenda.stream()
                                   .map(VendaItem::getSubtotal)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(currencyFormat.format(total));
    }

    private void limparCamposItem() {
        quantidadeField.setText("");
        valorField.setText("");
        produtoComboBox.requestFocus();
    }

    private void limparVenda() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja cancelar a venda atual?", "Cancelar Venda", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            itensVenda.clear();
            tableModel.setRowCount(0);
            atualizarTotal();
            limparCamposItem();
        }
    }

    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione itens para finalizar a venda.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Lógica futura: enviar a venda para a API, gerar nota, etc.
        JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        
        itensVenda.clear();
        tableModel.setRowCount(0);
        atualizarTotal();
        limparCamposItem();
    }
}
