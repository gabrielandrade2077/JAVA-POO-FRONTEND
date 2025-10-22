package br.com.pdv.view;

import br.com.pdv.model.Contato;
import br.com.pdv.model.Pessoa;
import br.com.pdv.model.TipoPessoa;
import br.com.pdv.service.PessoaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PessoaScreen extends JPanel {

    private PessoaService pessoaService;

    // Componentes do formulário
    private JTextField idField, nomeField, cpfCnpjField, dataNascimentoField, emailField, telefoneField, enderecoField;
    private JComboBox<TipoPessoa> tipoPessoaCombo;
    private JButton salvarButton, limparButton, deletarButton;

    // Componentes da tabela
    private JTable tabelaPessoas;
    private DefaultTableModel tableModel;
    private List<Pessoa> listaPessoas = new ArrayList<>();

    public PessoaScreen() {
        this.pessoaService = new PessoaService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        adicionarListeners();
        atualizarTabela();
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dados da Pessoa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField();
        idField.setVisible(false);

        // Linha 1: Nome Completo
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; nomeField = new JTextField(30); panel.add(nomeField, gbc);

        // Linha 2: CPF/CNPJ e Tipo Pessoa
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("CPF/CNPJ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; cpfCnpjField = new JTextField(15); panel.add(cpfCnpjField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; tipoPessoaCombo = new JComboBox<>(TipoPessoa.values()); panel.add(tipoPessoaCombo, gbc);

        // Linha 3: Data de Nascimento
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Nascimento (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dataNascimentoField = new JTextField(15); panel.add(dataNascimentoField, gbc);

        // Linha 4: Email e Telefone
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; emailField = new JTextField(15); panel.add(emailField, gbc);
        gbc.gridx = 2; gbc.gridy = 3; panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; telefoneField = new JTextField(15); panel.add(telefoneField, gbc);

        // Linha 5: Endereço
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3; enderecoField = new JTextField(30); panel.add(enderecoField, gbc);

        // Linha 6: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarButton = new JButton("Salvar");
        limparButton = new JButton("Limpar");
        deletarButton = new JButton("Deletar");
        painelBotoes.add(salvarButton);
        painelBotoes.add(limparButton);
        painelBotoes.add(deletarButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(painelBotoes, gbc);

        return panel;
    }

    private JPanel criarPainelTabela() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Pessoas Cadastradas"));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF/CNPJ", "Nascimento", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPessoas = new JTable(tableModel);

        panel.add(new JScrollPane(tabelaPessoas), BorderLayout.CENTER);
        return panel;
    }

    private void adicionarListeners() {
        salvarButton.addActionListener(e -> salvarPessoa());
        limparButton.addActionListener(e -> limparFormulario());
        deletarButton.addActionListener(e -> deletarPessoa());

        tabelaPessoas.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tabelaPessoas.getSelectedRow() != -1) {
                int selectedRow = tabelaPessoas.getSelectedRow();
                Pessoa pessoaSelecionada = listaPessoas.get(selectedRow);
                preencherFormulario(pessoaSelecionada);
            }
        });
    }

    private void limparFormulario() {
        idField.setText("");
        nomeField.setText("");
        cpfCnpjField.setText("");
        dataNascimentoField.setText("");
        emailField.setText("");
        telefoneField.setText("");
        enderecoField.setText("");
        tipoPessoaCombo.setSelectedIndex(0);
        tabelaPessoas.clearSelection();
        nomeField.requestFocus();
    }

    private void preencherFormulario(Pessoa pessoa) {
        idField.setText(String.valueOf(pessoa.getId()));
        nomeField.setText(pessoa.getNomeCompleto());
        cpfCnpjField.setText(pessoa.getCpfCnpj());
        tipoPessoaCombo.setSelectedItem(pessoa.getTipoPessoa());

        if (pessoa.getDataNascimento() != null) {
            dataNascimentoField.setText(pessoa.getDataNascimento().toString());
        } else {
            dataNascimentoField.setText("");
        }

        if (pessoa.getContato() != null) {
            emailField.setText(pessoa.getContato().getEmail());
            telefoneField.setText(pessoa.getContato().getTelefone());
            enderecoField.setText(pessoa.getContato().getEndereco());
        } else {
            emailField.setText("");
            telefoneField.setText("");
            enderecoField.setText("");
        }
    }

    private void salvarPessoa() {
        if (nomeField.getText().trim().isEmpty() || cpfCnpjField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF/CNPJ são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Pessoa pessoa = new Pessoa();
            Contato contato = new Contato();
            contato.setEmail(emailField.getText());
            contato.setTelefone(telefoneField.getText());
            contato.setEndereco(enderecoField.getText());

            pessoa.setNomeCompleto(nomeField.getText());
            pessoa.setCpfCnpj(cpfCnpjField.getText());
            pessoa.setTipoPessoa((TipoPessoa) tipoPessoaCombo.getSelectedItem());
            pessoa.setContato(contato);

            String dataNascimentoStr = dataNascimentoField.getText();
            if (dataNascimentoStr != null && !dataNascimentoStr.trim().isEmpty()) {
                pessoa.setDataNascimento(LocalDate.parse(dataNascimentoStr));
            }

            if (!idField.getText().isEmpty()) {
                pessoa.setId(Long.parseLong(idField.getText()));
            }

            Pessoa pessoaSalva = pessoaService.salvarPessoa(pessoa);
            if (pessoaSalva != null) {
                JOptionPane.showMessageDialog(this, "Pessoa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a pessoa.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            limparFormulario();
            atualizarTabela();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "O formato da data de nascimento é inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarPessoa() {
        int selectedRow = tabelaPessoas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar a pessoa selecionada?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Pessoa pessoaSelecionada = listaPessoas.get(selectedRow);
            pessoaService.deletarPessoa(pessoaSelecionada.getId());
            JOptionPane.showMessageDialog(this, "Pessoa deletada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
            limparFormulario();
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        listaPessoas = pessoaService.listarPessoas();

        if (listaPessoas != null) {
            for (Pessoa p : listaPessoas) {
                tableModel.addRow(new Object[]{p.getId(), p.getNomeCompleto(), p.getCpfCnpj(), p.getDataNascimento(), p.getTipoPessoa()});
            }
        }
    }
}
