package br.com.pdv.model;

import java.math.BigDecimal;

/**
 * Classe auxiliar para representar um item dentro da tela de venda.
 */
public class VendaItem {
    private Produto produto;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public VendaItem(Produto produto, BigDecimal quantidade, BigDecimal precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = quantidade.multiply(precoUnitario);
    }

    // Getters
    public Produto getProduto() {
        return produto;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
