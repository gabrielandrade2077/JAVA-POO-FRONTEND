package br.com.pdv.service;

import br.com.pdv.model.Produto;
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
import java.util.List;

/**
 * Classe de serviço para gerenciar a comunicação com a API de Produtos.
 */
public class ProdutoService {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private final String API_URL = "http://localhost:8080/api/v1/produtos";

    private static class PageResponse<T> {
        List<T> content;
    }


    public List<Produto> listarProdutos() {
        try {
            String urlComPaginacao = API_URL + "?page=0&size=100";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlComPaginacao))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type pageType = new TypeToken<PageResponse<Produto>>() {}.getType();
                PageResponse<Produto> pageResponse = gson.fromJson(response.body(), pageType);
                return pageResponse.content;
            } else {
                System.err.println("Erro ao listar produtos: " + response.statusCode() + " " + response.body());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Produto salvarProduto(Produto produto) {
        try {
            String jsonBody = gson.toJson(produto);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json");

            if (produto.getId() == null) {
                requestBuilder.uri(URI.create(API_URL))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
            } else {
                requestBuilder.uri(URI.create(API_URL + "/" + produto.getId()))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return gson.fromJson(response.body(), Produto.class);
            } else {
                System.err.println("Erro ao salvar produto: " + response.statusCode() + " " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deletarProduto(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 204) { // O backend retorna 204 No Content
                System.err.println("Erro ao deletar produto: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
