package br.com.pdv.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class PdvScreen extends JPanel {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public PdvScreen() {
        setLayout(new GridLayout(1, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createPumpPanel("Bomba 1", "Etanol", 3.50));
        add(createPumpPanel("Bomba 2", "Gasolina", 5.50));
        add(createPumpPanel("Bomba 3", "Diesel", 6.00));
    }

    private JPanel createPumpPanel(String pumpTitle, String fuelType, double basePrice) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(pumpTitle + " - " + fuelType));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Valor por Litro
        Random random = new Random();
        double priceVariation = (random.nextDouble() - 0.5) * 0.2; // +/- 10 centavos
        BigDecimal valorPorLitro = BigDecimal.valueOf(basePrice + priceVariation).setScale(2, RoundingMode.HALF_UP);
        JTextField valorPorLitroField = new JTextField(valorPorLitro.toPlainString(), 10);
        valorPorLitroField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Valor/Litro:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(valorPorLitroField, gbc);

        // Litros
        JTextField litrosField = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Litros:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(litrosField, gbc);

        // Valor Total
        JTextField valorTotalField = new JTextField(10);
        valorTotalField.setEditable(false);
        valorTotalField.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(valorTotalField, gbc);

        // Forma de Pagamento
        JComboBox<String> pagamentoComboBox = new JComboBox<>(new String[]{"Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito"});
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Pagamento:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(pagamentoComboBox, gbc);

        // Botão Abastecer
        JButton abastecerButton = new JButton("Abastecer");
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; inputPanel.add(abastecerButton, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Tabela de Histórico ---
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Litros", "Valor Total", "Pagamento"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        DocumentListener calculationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateTotal();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateTotal();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateTotal();
            }

            private void calculateTotal() {
                try {
                    String litrosText = litrosField.getText().replace(',', '.');
                    if (litrosText.isEmpty()) {
                        valorTotalField.setText("");
                        return;
                    }
                    BigDecimal litros = new BigDecimal(litrosText);
                    BigDecimal total = litros.multiply(valorPorLitro);
                    valorTotalField.setText(currencyFormat.format(total));
                } catch (NumberFormatException ex) {
                    valorTotalField.setText("Inválido");
                }
            }
        };

        litrosField.getDocument().addDocumentListener(calculationListener);

        abastecerButton.addActionListener(e -> {
            try {
                String litrosText = litrosField.getText().replace(',', '.');
                BigDecimal litros = new BigDecimal(litrosText);
                BigDecimal total = litros.multiply(valorPorLitro);
                String pagamento = (String) pagamentoComboBox.getSelectedItem();

                tableModel.addRow(new Object[]{
                        String.format("%.3f", litros),
                        currencyFormat.format(total),
                        pagamento
                });

                // Limpar campos
                litrosField.setText("");
                valorTotalField.setText("");

                int option = JOptionPane.showConfirmDialog(panel, "Deseja imprimir o cupom fiscal?", "Cupom Fiscal", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    JTextArea receiptArea = new JTextArea();
                    receiptArea.setEditable(false);
                    receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    String receiptText = "      *** CUPOM FISCAL ***\n" +
                                       "------------------------------------\n" +
                                       "Bomba: " + pumpTitle + "\n" +
                                       "Combustível: " + fuelType + "\n" +
                                       "Litros: " + String.format("%.3f", litros) + "\n" +
                                       "Valor/Litro: " + currencyFormat.format(valorPorLitro) + "\n" +
                                       "Total: " + currencyFormat.format(total) + "\n" +
                                       "Pagamento: " + pagamento + "\n" +
                                       "------------------------------------";
                    receiptArea.setText(receiptText);

                    JButton printButton = new JButton("Imprimir");
                    printButton.addActionListener(printEvent -> {
                        JOptionPane.showMessageDialog(panel, "Imprimindo cupom...", "Imprimir", JOptionPane.INFORMATION_MESSAGE);
                    });

                    JPanel receiptPanel = new JPanel(new BorderLayout());
                    receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
                    receiptPanel.add(printButton, BorderLayout.SOUTH);

                    JOptionPane.showMessageDialog(panel, receiptPanel, "Cupom Fiscal", JOptionPane.PLAIN_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Valor de litros inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
}
