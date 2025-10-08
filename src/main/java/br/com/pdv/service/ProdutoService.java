package br.com.pdv.service;

import br.com.pdv.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe de serviço MOCK para gerenciar a comunicação com a API de Produtos.
 * Esta versão simula as operações CRUD em memória para testes do front-end.
 */
public class ProdutoService {

    private final List<Produto> mockDatabase = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public ProdutoService() {
        // Adiciona alguns produtos de exemplo ao iniciar o serviço
        Produto prod1 = new Produto();
        prod1.setId(idCounter.incrementAndGet());
        prod1.setNome("Gasolina Comum");
        prod1.setReferencia("GC001");
        prod1.setFornecedor("Petrobras");
        prod1.setCategoria("Combustível");
        prod1.setMarca("BR");
        mockDatabase.add(prod1);

        Produto prod2 = new Produto();
        prod2.setId(idCounter.incrementAndGet());
        prod2.setNome("Água Mineral 500ml");
        prod2.setReferencia("AM500");
        prod2.setFornecedor("Nestlé");
        prod2.setCategoria("Conveniência");
        prod2.setMarca("Pureza");
        mockDatabase.add(prod2);

        Produto prod3 = new Produto();
        prod3.setId(idCounter.incrementAndGet());
        prod3.setNome("Óleo Lubrificante 1L");
        prod3.setReferencia("OL100");
        prod3.setFornecedor("Shell");
        prod3.setCategoria("Automotivo");
        prod3.setMarca("Helix");
        mockDatabase.add(prod3);
    }

    /**
     * Busca a lista de todos os produtos (mock).
     * @return Uma lista de produtos.
     */
    public List<Produto> listarProdutos() {
        System.out.println("MOCK: Listando todos os produtos...");
        return new ArrayList<>(mockDatabase); // Retorna uma cópia para evitar modificações externas
    }

    /**
     * Salva um novo produto ou atualiza um existente (mock).
     * @param produto O objeto Produto a ser salvo.
     * @return O produto salvo (com ID, se for novo).
     */
    public Produto salvarProduto(Produto produto) {
        if (produto.getId() == null) {
            // Novo produto
            produto.setId(idCounter.incrementAndGet());
            mockDatabase.add(produto);
            System.out.println("MOCK: Criando novo produto: " + produto.getNome() + " com ID: " + produto.getId());
        } else {
            // Atualizar produto existente
            for (int i = 0; i < mockDatabase.size(); i++) {
                if (mockDatabase.get(i).getId().equals(produto.getId())) {
                    mockDatabase.set(i, produto);
                    System.out.println("MOCK: Atualizando produto com ID: " + produto.getId());
                    break;
                }
            }
        }
        return produto; // Retorna o produto (com ID atribuído se for novo)
    }

    /**
     * Deleta um produto pelo ID (mock).
     * @param id O ID do produto a ser deletado.
     */
    public void deletarProduto(Long id) {
        mockDatabase.removeIf(p -> p.getId().equals(id));
        System.out.println("MOCK: Deletando produto com ID: " + id);
    }
}
