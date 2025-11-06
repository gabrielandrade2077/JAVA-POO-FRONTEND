package br.com.pdv.service;

import br.com.pdv.model.Estoque;
import br.com.pdv.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de serviço para gerenciar a comunicação com a API de Estoque.
 */
public class EstoqueService {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private final String API_URL = "http://localhost:8080/api/v1/estoques";

    // Classe interna para ajudar a desserializar a resposta paginada do Spring
    private static class PageResponse<T> {
        List<T> content;
    }

    public List<Estoque> listarEstoque() {
        try {
            String urlComPaginacao = API_URL + "?page=0&size=100";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlComPaginacao))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type pageType = new TypeToken<PageResponse<Estoque>>() {}.getType();
                PageResponse<Estoque> pageResponse = gson.fromJson(response.body(), pageType);
                return pageResponse.content;
            } else {
                System.err.println("Erro ao listar estoque: " + response.statusCode() + " " + response.body());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Estoque salvarEstoque(Estoque estoque) {
        try {
            // Cria um Map para representar o DTO EstoqueRequest esperado pelo backend
            Map<String, Object> estoqueRequest = new HashMap<>();
            estoqueRequest.put("quantidade", estoque.getQuantidade());
            estoqueRequest.put("localTanque", estoque.getLocalTanque());
            estoqueRequest.put("loteFabricacao", estoque.getLoteFabricacao());
            estoqueRequest.put("dataValidade", estoque.getDataValidade() != null ? estoque.getDataValidade().toString() : null);

            if (estoque.getProduto() != null) {
                estoqueRequest.put("produtoId", estoque.getProduto().getId());
            } else {
                estoqueRequest.put("produtoId", null);
            }

            String jsonBody = gson.toJson(estoqueRequest);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json");

            if (estoque.getId() == null) {
                requestBuilder.uri(URI.create(API_URL))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
            } else {
                requestBuilder.uri(URI.create(API_URL + "/" + estoque.getId()))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return gson.fromJson(response.body(), Estoque.class);
            } else {
                System.err.println("Erro ao salvar estoque: " + response.statusCode() + " " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deletarEstoque(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 204) { // No Content
                System.err.println("Erro ao deletar item do estoque: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
