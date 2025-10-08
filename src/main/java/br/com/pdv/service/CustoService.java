package br.com.pdv.service;

import br.com.pdv.model.Custo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe de serviço MOCK para gerenciar a comunicação com a API de Custos.
 * Esta versão simula as operações em memória para testes do front-end.
 */
public class CustoService {

    private final List<Custo> mockDatabase = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public CustoService() {
        // Adiciona alguns custos de exemplo ao iniciar o serviço
        Custo c1 = new Custo();
        c1.setId(idCounter.incrementAndGet());
        c1.setImposto(new BigDecimal("0.15"));
        c1.setCustoVariavel(new BigDecimal("0.05"));
        c1.setCustoFixo(new BigDecimal("1500.00"));
        c1.setMargemLucro(new BigDecimal("0.20"));
        c1.setDataProcessamento(LocalDate.now().minusDays(30));
        mockDatabase.add(c1);

        Custo c2 = new Custo();
        c2.setId(idCounter.incrementAndGet());
        c2.setImposto(new BigDecimal("0.18"));
        c2.setCustoVariavel(new BigDecimal("0.06"));
        c2.setCustoFixo(new BigDecimal("1600.00"));
        c2.setMargemLucro(new BigDecimal("0.22"));
        c2.setDataProcessamento(LocalDate.now());
        mockDatabase.add(c2);
    }

    /**
     * Busca a lista de todos os custos (mock).
     * @return Uma lista de custos.
     */
    public List<Custo> listarCustos() {
        System.out.println("MOCK: Listando todos os custos...");
        return new ArrayList<>(mockDatabase); // Retorna uma cópia para evitar modificações externas
    }

    /**
     * Salva um novo custo (mock).
     * @param custo O objeto Custo a ser salvo.
     * @return O custo salvo (com ID, se for novo).
     */
    public Custo salvarCusto(Custo custo) {
        // A API de custos não tem PUT, apenas POST para novos registros
        custo.setId(idCounter.incrementAndGet());
        custo.setDataProcessamento(LocalDate.now()); // Garante que a data seja a atual para novos registros
        mockDatabase.add(custo);
        System.out.println("MOCK: Criando novo custo com ID: " + custo.getId());
        return custo; // Retorna o custo com ID atribuído
    }
}
