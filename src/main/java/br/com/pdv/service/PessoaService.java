package br.com.pdv.service;

import br.com.pdv.model.Contato;
import br.com.pdv.model.Pessoa;
import br.com.pdv.model.TipoPessoa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe de serviço MOCK para gerenciar a comunicação com a API de Pessoas.
 * Esta versão simula as operações CRUD em memória para testes do front-end.
 */
public class PessoaService {

    private final List<Pessoa> mockDatabase = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public PessoaService() {
        // Adiciona algumas pessoas de exemplo ao iniciar o serviço
        Contato contato1 = new Contato();
        contato1.setEmail("joao.silva@example.com");
        contato1.setTelefone("11987654321");
        contato1.setEndereco("Rua A, 123");

        Pessoa p1 = new Pessoa();
        p1.setId(idCounter.incrementAndGet());
        p1.setNomeCompleto("João Silva");
        p1.setCpfCnpj("111.111.111-11");
        p1.setTipoPessoa(TipoPessoa.FISICA);
        p1.setContato(contato1);
        mockDatabase.add(p1);

        Contato contato2 = new Contato();
        contato2.setEmail("empresa@example.com");
        contato2.setTelefone("2134567890");
        contato2.setEndereco("Av. B, 456");

        Pessoa p2 = new Pessoa();
        p2.setId(idCounter.incrementAndGet());
        p2.setNomeCompleto("Empresa XYZ Ltda.");
        p2.setCpfCnpj("00.000.000/0001-00");
        p2.setTipoPessoa(TipoPessoa.JURIDICA);
        p2.setContato(contato2);
        mockDatabase.add(p2);
    }

    /**
     * Busca a lista de todas as pessoas (mock).
     * @return Uma lista de pessoas.
     */
    public List<Pessoa> listarPessoas() {
        System.out.println("MOCK: Listando todas as pessoas...");
        return new ArrayList<>(mockDatabase); // Retorna uma cópia para evitar modificações externas
    }

    /**
     * Salva uma nova pessoa ou atualiza uma existente (mock).
     * @param pessoa O objeto Pessoa a ser salvo.
     * @return A pessoa salva (com ID, se for nova).
     */
    public Pessoa salvarPessoa(Pessoa pessoa) {
        if (pessoa.getId() == null) {
            // Nova pessoa
            pessoa.setId(idCounter.incrementAndGet());
            mockDatabase.add(pessoa);
            System.out.println("MOCK: Criando nova pessoa: " + pessoa.getNomeCompleto() + " com ID: " + pessoa.getId());
        } else {
            // Atualizar pessoa existente
            for (int i = 0; i < mockDatabase.size(); i++) {
                if (mockDatabase.get(i).getId().equals(pessoa.getId())) {
                    mockDatabase.set(i, pessoa);
                    System.out.println("MOCK: Atualizando pessoa com ID: " + pessoa.getId());
                    break;
                }
            }
        }
        return pessoa; // Retorna a pessoa (com ID atribuído se for nova)
    }

    /**
     * Deleta uma pessoa pelo ID (mock).
     * @param id O ID da pessoa a ser deletada.
     */
    public void deletarPessoa(Long id) {
        mockDatabase.removeIf(p -> p.getId().equals(id));
        System.out.println("MOCK: Deletando pessoa com ID: " + id);
    }
}
