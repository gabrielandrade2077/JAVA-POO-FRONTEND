package br.com.pdv.view;

import br.com.pdv.model.Custo;
import br.com.pdv.service.CustoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustoScreen extends JPanel {

    private CustoService custoService;

    // Componentes do formulário
    private JTextField impostoField, custoVariavelField, custoFixoField, margemLucroField;
    private JButton salvarButton, limparButton;

    // Componentes da tabela
    private JTable tabelaCustos;
    private DefaultTableModel tableModel;

    public CustoScreen() {
        this.custoService = new CustoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        adicionarListeners();
        atualizarTabela();
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Novo Custo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Imposto e Custo Fixo
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Imposto (%):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; impostoField = new JTextField(10); panel.add(impostoField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Custo Fixo (R$):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; custoFixoField = new JTextField(10); panel.add(custoFixoField, gbc);

        // Linha 2: Custo Variável e Margem de Lucro
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Custo Variável (%):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; custoVariavelField = new JTextField(10); panel.add(custoVariavelField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Margem de Lucro (%):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; margemLucroField = new JTextField(10); panel.add(margemLucroField, gbc);

        // Linha 3: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar Novo Custo");
        limparButton = new JButton("Limpar");
        painelBotoes.add(salvarButton);
        painelBotoes.add(limparButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(painelBotoes, gbc);

        return panel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Histórico de Custos"));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Data", "Imposto", "C. Fixo", "C. Variável", "M. Lucro"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCustos = new JTable(tableModel);

        panel.add(new JScrollPane(tabelaCustos), BorderLayout.CENTER);
        return panel;
    }

    private void adicionarListeners() {
        salvarButton.addActionListener(e -> salvarCusto());
        limparButton.addActionListener(e -> limparFormulario());
    }

    private void limparFormulario() {
        impostoField.setText("");
        custoFixoField.setText("");
        custoVariavelField.setText("");
        margemLucroField.setText("");
        impostoField.requestFocus();
    }

    private void salvarCusto() {
        try {
            Custo custo = new Custo();
            
            // Helper function to safely parse BigDecimal, defaulting to 0 if empty
            BigDecimal imposto = parseBigDecimalOrDefault(impostoField.getText());
            BigDecimal custoFixo = parseBigDecimalOrDefault(custoFixoField.getText());
            BigDecimal custoVariavel = parseBigDecimalOrDefault(custoVariavelField.getText());
            BigDecimal margemLucro = parseBigDecimalOrDefault(margemLucroField.getText());

            custo.setImposto(imposto);
            custo.setCustoFixo(custoFixo);
            custo.setCustoVariavel(custoVariavel);
            custo.setMargemLucro(margemLucro);
            custo.setDataProcessamento(LocalDate.now()); // Data atual

            Custo custoSalvo = custoService.salvarCusto(custo);
            if (custoSalvo != null) {
                JOptionPane.showMessageDialog(this, "Custo registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao registrar o custo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseBigDecimalOrDefault(String text) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(text);
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        List<Custo> listaCustos = custoService.listarCustos();

        if (listaCustos != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Custo c : listaCustos) {
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getDataProcessamento().format(formatter),
                        c.getImposto(),
                        c.getCustoFixo(),
                        c.getCustoVariavel(),
                        c.getMargemLucro()
                });
            }
        }
    }
}
