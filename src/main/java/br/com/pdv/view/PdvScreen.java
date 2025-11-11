package br.com.pdv.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdvScreen extends JPanel {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final String API_BASE_URL = "http://localhost:8080/api";

    public PdvScreen() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loadingLabel = new JLabel("Carregando bombas...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(loadingLabel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::fetchAndCreatePumps);
    }

    private void fetchAndCreatePumps() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/bombas")) // URL corrigida conforme solicitado
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            removeAll();

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                setLayout(new GridLayout(1, 0, 10, 10));

                // Padrão para encontrar cada objeto "bomba" na resposta JSON
                Pattern bombaPattern = Pattern.compile("\\{\\\"id\\\":.*?\"numero\":(\\d+),.*?\"bicos\":\\[(.*?)\\]\\}");
                Matcher bombaMatcher = bombaPattern.matcher(responseBody);

                int totalBicosCount = 0;

                while (bombaMatcher.find()) {
                    String bombaNumero = bombaMatcher.group(1);
                    String bicosJson = bombaMatcher.group(2);

                    // Padrão para encontrar cada "bico" dentro do array "bicos"
                    Pattern bicoPattern = Pattern.compile("\\{\\\"id\\\":(\\d+),.*?\"combustivel\":\\{.*?\"nome\":\"(.*?)\",\"preco\":(\\d+\\.\\d+).*?\\}\\}");
                    Matcher bicoMatcher = bicoPattern.matcher(bicosJson);

                    while (bicoMatcher.find()) {
                        int bicoId = Integer.parseInt(bicoMatcher.group(1));
                        String combustivelNome = bicoMatcher.group(2);
                        double combustivelPreco = Double.parseDouble(bicoMatcher.group(3));

                        String pumpTitle = "Bomba " + bombaNumero;
                        add(createPumpPanel(bicoId, pumpTitle, combustivelNome, combustivelPreco));
                        totalBicosCount++;
                    }
                }


                if (totalBicosCount == 0) {
                    showError("Nenhuma bomba de combustível encontrada na resposta da API.");
                }

            } else {
                showError(String.format("Erro ao buscar dados das bombas.\nStatus: %d", response.statusCode()));
            }

        } catch (IOException | InterruptedException e) {
            removeAll();
            showError("Não foi possível conectar ao servidor para buscar os dados das bombas.\nVerifique se o backend está rodando.");
            Thread.currentThread().interrupt();
        }
        revalidate();
        repaint();
    }

    private void showError(String message) {
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        setLayout(new BorderLayout());
        add(errorLabel, BorderLayout.CENTER);
    }


    private JPanel createPumpPanel(int bicoId, String pumpTitle, String fuelType, double price) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(pumpTitle + " - " + fuelType));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BigDecimal valorPorLitro = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        JTextField valorPorLitroField = new JTextField(currencyFormat.format(valorPorLitro), 10);
        valorPorLitroField.setEditable(false);
        valorPorLitroField.setFont(new Font("SansSerif", Font.BOLD, 12));

        JTextField litrosField = new JTextField(10);
        JTextField valorTotalField = new JTextField(10);
        valorTotalField.setEditable(false);
        valorTotalField.setFont(new Font("SansSerif", Font.BOLD, 14));
        JComboBox<String> pagamentoComboBox = new JComboBox<>(new String[]{"Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito"});
        JButton abastecerButton = new JButton("Abastecer");


        // Status Label
        JLabel statusLabel = new JLabel("Status: Ativa");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0, 150, 0)); // Green color for active

        // Progress Bar and Countdown Label
        JProgressBar progressBar = new JProgressBar(0, 3000); // 3 seconds = 3000 ms
        progressBar.setStringPainted(true);
        progressBar.setVisible(false); // Initially hidden

        JLabel countdownLabel = new JLabel("Tempo restante: 3.0s");
        countdownLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countdownLabel.setVisible(false); // Initially hidden


        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Valor/Litro:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(valorPorLitroField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Litros:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(litrosField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(valorTotalField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Pagamento:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(pagamentoComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(statusLabel, gbc); // Add status label
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; inputPanel.add(abastecerButton, gbc);

        // Add progress bar and countdown label below the button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; inputPanel.add(countdownLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(progressBar, gbc);


        panel.add(inputPanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Litros", "Valor Total", "Pagamento"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        DocumentListener calculationListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(DocumentEvent e) { calculateTotal(); }
            public void changedUpdate(DocumentEvent e) { calculateTotal(); }

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
            String litrosText = litrosField.getText().replace(',', '.');
            if (litrosText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Por favor, informe a quantidade de litros.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            abastecerButton.setEnabled(false);
            statusLabel.setText("Status: Ocupada");
            statusLabel.setForeground(Color.ORANGE);

            progressBar.setValue(0);
            progressBar.setVisible(true);
            countdownLabel.setText("Tempo restante: 3.0s");
            countdownLabel.setVisible(true);

            final int totalTime = 3000; // 3 seconds in milliseconds
            final int updateInterval = 100; // Update every 100 ms
            final Timer animationTimer = new Timer(updateInterval, null);

            animationTimer.addActionListener(e_timer -> {
                int currentValue = progressBar.getValue();
                if (currentValue < totalTime) {
                    progressBar.setValue(currentValue + updateInterval);
                    double remainingSeconds = (double)(totalTime - progressBar.getValue()) / 1000.0;
                    countdownLabel.setText(String.format("Tempo restante: %.1fs", remainingSeconds));
                } else {
                    animationTimer.stop();
                }
            });
            animationTimer.start();


            new SwingWorker<HttpResponse<String>, Void>() {
                @Override
                protected HttpResponse<String> doInBackground() throws Exception {
                    BigDecimal litros = new BigDecimal(litrosText);
                    BigDecimal valorTotal = litros.multiply(valorPorLitro).setScale(2, RoundingMode.HALF_UP);
                    String formaPagamento = (String) pagamentoComboBox.getSelectedItem();

                    String jsonBody = String.format(
                        "{\"bicoId\": %d, \"litros\": %s, \"precoPorLitro\": %s, \"valorTotal\": %s, \"formaPagamento\": \"%s\"}",
                        bicoId,
                        litros.toPlainString(),
                        valorPorLitro.toPlainString(),
                        valorTotal.toPlainString(),
                        formaPagamento
                    );

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE_URL + "/abastecimentos"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    return client.send(request, HttpResponse.BodyHandlers.ofString());
                }

                @Override
                protected void done() {
                    try {
                        HttpResponse<String> response = get(); // Get the result from doInBackground

                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            JOptionPane.showMessageDialog(panel, "Abastecimento registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                            BigDecimal litros = new BigDecimal(litrosText);
                            BigDecimal valorTotal = litros.multiply(valorPorLitro).setScale(2, RoundingMode.HALF_UP);
                            String formaPagamento = (String) pagamentoComboBox.getSelectedItem();

                            tableModel.addRow(new Object[]{
                                    String.format("%.3f", litros),
                                    currencyFormat.format(valorTotal),
                                    formaPagamento
                            });

                            String cupomFiscal = "--- Cupom Fiscal ---\n" +
                                                 "Bomba: " + pumpTitle + "\n" +
                                                 "Combustível: " + fuelType + "\n" +
                                                 "Litros: " + String.format("%.3f", litros) + "\n" +
                                                 "Valor/Litro: " + currencyFormat.format(valorPorLitro) + "\n" +
                                                 "Total: " + currencyFormat.format(valorTotal) + "\n" +
                                                 "Pagamento: " + formaPagamento + "\n" +
                                                 "--------------------";

                            int option = JOptionPane.showConfirmDialog(panel, cupomFiscal, "Deseja imprimir o cupom fiscal?", JOptionPane.YES_NO_OPTION);
                            if (option == JOptionPane.YES_OPTION) {
                                // Simulação de impressão
                                JOptionPane.showMessageDialog(panel, "Imprimindo cupom fiscal:\n" + cupomFiscal, "Imprimindo", JOptionPane.INFORMATION_MESSAGE);
                            }

                            litrosField.setText("");
                            valorTotalField.setText("");

                        } else {
                            String errorMessage = String.format("Erro ao registrar abastecimento.\nStatus: %d\nResposta: %s",
                                                                response.statusCode(), response.body());
                            JOptionPane.showMessageDialog(panel, errorMessage, "Erro de API", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel, "Valor de litros inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        JOptionPane.showMessageDialog(panel, "Operação de abastecimento interrompida.", "Erro", JOptionPane.ERROR_MESSAGE);
                    } catch (java.util.concurrent.ExecutionException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IOException) {
                            JOptionPane.showMessageDialog(panel, "Erro de comunicação com o servidor: " + cause.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel, "Erro inesperado durante o abastecimento: " + cause.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } finally {
                        // Revert status and re-enable button after a delay
                        Timer timer = new Timer(3000, event -> { // 3 seconds delay
                            statusLabel.setText("Status: Ativa");
                            statusLabel.setForeground(new Color(0, 150, 0)); // Green color for active
                            abastecerButton.setEnabled(true);
                            animationTimer.stop(); // Stop the animation timer
                            progressBar.setVisible(false);
                            countdownLabel.setVisible(false);
                            progressBar.setValue(0); // Reset progress bar
                        });
                        timer.setRepeats(false); // Ensure the timer only runs once
                        timer.start();
                    }
                }
            }.execute();
        });

        return panel;
    }
}
