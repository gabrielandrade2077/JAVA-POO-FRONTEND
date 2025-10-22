package br.com.pdv.service;

import br.com.pdv.model.Preco;
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
 * Classe de serviço para gerenciar a comunicação com a API de Preços.
 */
public class PrecoService {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private final String API_URL = "http://localhost:8080/api/v1/precos";

    // Classe interna para ajudar a desserializar a resposta paginada do Spring
    private static class PageResponse<T> {
        List<T> content;
    }

    /**
     * Busca a lista de todos os preços da API.
     * @return Uma lista de preços.
     */
    public List<Preco> listarPrecos() {
        try {
            String urlComPaginacao = API_URL + "?page=0&size=100"; // Pede até 100 itens
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlComPaginacao))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type pageType = new TypeToken<PageResponse<Preco>>() {}.getType();
                PageResponse<Preco> pageResponse = gson.fromJson(response.body(), pageType);
                return pageResponse.content;
            } else {
                System.err.println("Erro ao listar preços: " + response.statusCode() + " " + response.body());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Salva um novo preço via API (somente POST).
     * @param preco O objeto Preco a ser salvo.
     * @return O preço salvo.
     */
    public Preco salvarPreco(Preco preco) {
        try {
            String jsonBody = gson.toJson(preco);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) { // HTTP 201 Created
                return gson.fromJson(response.body(), Preco.class);
            } else {
                System.err.println("Erro ao salvar preço: " + response.statusCode() + " " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
