package br.com.pdv.service;

import br.com.pdv.model.Estoque;
import br.com.pdv.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe de serviço MOCK para gerenciar a comunicação com a API de Estoque.
 * Esta versão simula as operações em memória para testes do front-end.
 */
public class EstoqueService {

    private final List<Estoque> mockDatabase = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);
    private final ProdutoService produtoService = new ProdutoService(); // Para obter produtos mockados

    public EstoqueService() {
        // Adiciona alguns itens de estoque de exemplo ao iniciar o serviço
        List<Produto> produtosMock = produtoService.listarProdutos();

        if (produtosMock.size() >= 3) { // Certifica-se de que há produtos suficientes
            Produto prod1 = produtosMock.get(0); // Ex: Gasolina Comum
            Produto prod2 = produtosMock.get(1); // Ex: Água Mineral
            Produto prod3 = produtosMock.get(2); // Ex: Óleo Lubrificante

            Estoque e1 = new Estoque();
            e1.setId(idCounter.incrementAndGet());
            e1.setProduto(prod1);
            e1.setQuantidade(new BigDecimal("5000.00"));
            e1.setLocalTanque("Tanque 1");
            e1.setLoteFabricacao("LOTE-GC-2023");
            e1.setDataValidade(LocalDate.now().plusYears(1));
            mockDatabase.add(e1);

            Estoque e2 = new Estoque();
            e2.setId(idCounter.incrementAndGet());
            e2.setProduto(prod2);
            e2.setQuantidade(new BigDecimal("200.00"));
            e2.setLocalTanque("Prateleira A");
            e2.setLoteFabricacao("LOTE-AM-2024");
            e2.setDataValidade(LocalDate.now().plusMonths(6));
            mockDatabase.add(e2);

            Estoque e3 = new Estoque();
            e3.setId(idCounter.incrementAndGet());
            e3.setProduto(prod3);
            e3.setQuantidade(new BigDecimal("50.00"));
            e3.setLocalTanque("Armário 3");
            e3.setLoteFabricacao("LOTE-OL-2023");
            e3.setDataValidade(LocalDate.now().plusYears(2));
            mockDatabase.add(e3);
        }
    }

    /**
     * Busca a lista de todos os itens de estoque (mock).
     * @return Uma lista de itens de estoque.
     */
    public List<Estoque> listarEstoque() {
        System.out.println("MOCK: Listando todos os itens de estoque...");
        return new ArrayList<>(mockDatabase); // Retorna uma cópia para evitar modificações externas
    }

    /**
     * Adiciona um novo item ao estoque (mock).
     * @param estoque O objeto Estoque a ser salvo.
     * @return O item de estoque salvo (com ID, se for novo).
     */
    public Estoque adicionarItemEstoque(Estoque estoque) {
        estoque.setId(idCounter.incrementAndGet());
        // Em um cenário real, você buscaria o produto pelo ID para garantir que ele existe
        // Aqui, como estamos mockando, assumimos que o produto já vem preenchido corretamente
        mockDatabase.add(estoque);
        System.out.println("MOCK: Adicionando item ao estoque com ID: " + estoque.getId() + " para o produto: " + estoque.getProduto().getNome());
        return estoque; // Retorna o item de estoque com ID atribuído
    }
}
