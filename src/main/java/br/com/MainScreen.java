package br.com;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela principal da aplicação CRUD de produtos.
 */
public class MainScreen extends JFrame {
    private final ProductDAO productDAO;
    private final JTable productTable;
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField priceField;

    public MainScreen() {
        productDAO = new ProductDAO();

        setTitle("CRUD de Produtos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Tabela para exibir os produtos
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Preço"}, 0);
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Painel de formulário para adicionar/atualizar produtos
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Nome:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        formPanel.add(new JLabel("Preço:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Adicionar");
        JButton updateButton = new JButton("Atualizar");
        JButton deleteButton = new JButton("Deletar");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Adicionando os painéis ao frame
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ações dos botões
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        // Carregar dados iniciais
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Product> products = productDAO.getAllProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice()});
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        productDAO.addProduct(new Product(0, name, price));
        refreshTable();
        clearFields();
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            productDAO.updateProduct(new Product(id, name, price));
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para atualizar.");
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            productDAO.deleteProduct(id);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para deletar.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
    }
}
