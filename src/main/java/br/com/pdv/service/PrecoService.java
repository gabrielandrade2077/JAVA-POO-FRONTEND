package br.com.pdv.service;

import br.com.pdv.model.Preco;
import br.com.pdv.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe de serviço MOCK para gerenciar a comunicação com a API de Preços.
 * Esta versão simula as operações em memória para testes do front-end.
 */
public class PrecoService {

    private final List<Preco> mockDatabase = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);
    private final ProdutoService produtoService = new ProdutoService(); // Para obter produtos mockados

    public PrecoService() {
        // Adiciona alguns preços de exemplo ao iniciar o serviço
        // Certifique-se de que os produtos mockados existam no ProdutoService mock
        List<Produto> produtosMock = produtoService.listarProdutos();

        if (!produtosMock.isEmpty()) {
            Produto prod1 = produtosMock.get(0); // Ex: Gasolina Comum
            Produto prod2 = produtosMock.get(1); // Ex: Água Mineral

            Preco p1 = new Preco();
            p1.setId(idCounter.incrementAndGet());
            p1.setValor(new BigDecimal("5.99"));
            p1.setDataAlteracao(LocalDate.now().minusDays(10));
            p1.setProduto(prod1);
            mockDatabase.add(p1);

            Preco p2 = new Preco();
            p2.setId(idCounter.incrementAndGet());
            p2.setValor(new BigDecimal("2.50"));
            p2.setDataAlteracao(LocalDate.now().minusDays(5));
            p2.setProduto(prod2);
            mockDatabase.add(p2);

            Preco p3 = new Preco();
            p3.setId(idCounter.incrementAndGet());
            p3.setValor(new BigDecimal("6.10"));
            p3.setDataAlteracao(LocalDate.now());
            p3.setProduto(prod1);
            mockDatabase.add(p3);
        }
    }

    /**
     * Busca a lista de todos os preços (mock).
     * @return Uma lista de preços.
     */
    public List<Preco> listarPrecos() {
        System.out.println("MOCK: Listando todos os preços...");
        return new ArrayList<>(mockDatabase); // Retorna uma cópia para evitar modificações externas
    }

    /**
     * Salva um novo preço (mock).
     * @param preco O objeto Preco a ser salvo.
     * @return O preço salvo (com ID, se for novo).
     */
    public Preco salvarPreco(Preco preco) {
        // A API de preços não tem PUT, apenas POST para novos registros
        preco.setId(idCounter.incrementAndGet());
        preco.setDataAlteracao(LocalDate.now()); // Garante que a data seja a atual para novos registros
        mockDatabase.add(preco);
        System.out.println("MOCK: Criando novo preço com ID: " + preco.getId() + " para o produto: " + preco.getProduto().getNome());
        return preco; // Retorna o preço com ID atribuído
    }
}
