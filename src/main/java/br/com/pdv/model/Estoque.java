package br.com.pdv.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Estoque {
    private Long id;
    private Produto produto;
    private BigDecimal quantidade;
    private String localTanque;
    private String loteFabricacao;
    private LocalDate dataValidade;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getLocalTanque() {
        return localTanque;
    }

    public void setLocalTanque(String localTanque) {
        this.localTanque = localTanque;
    }

    public String getLoteFabricacao() {
        return loteFabricacao;
    }

    public void setLoteFabricacao(String loteFabricacao) {
        this.loteFabricacao = loteFabricacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}
