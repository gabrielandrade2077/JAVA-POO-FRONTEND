package br.com.pdv.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Custo {
    private Long id;
    private BigDecimal imposto;
    private BigDecimal custoVariavel;
    private BigDecimal custoFixo;
    private BigDecimal margemLucro;
    private LocalDate dataProcessamento;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getCustoVariavel() {
        return custoVariavel;
    }

    public void setCustoVariavel(BigDecimal custoVariavel) {
        this.custoVariavel = custoVariavel;
    }

    public BigDecimal getCustoFixo() {
        return custoFixo;
    }

    public void setCustoFixo(BigDecimal custoFixo) {
        this.custoFixo = custoFixo;
    }

    public BigDecimal getMargemLucro() {
        return margemLucro;
    }

    public void setMargemLucro(BigDecimal margemLucro) {
        this.margemLucro = margemLucro;
    }

    public LocalDate getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDate dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }
}
