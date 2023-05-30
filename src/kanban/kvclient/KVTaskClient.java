package kanban.kvclient;

import kanban.exception.ManagerClntException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.net.HttpURLConnection.HTTP_OK;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private String apiToken = "DEBUG";

    public KVTaskClient(String url) {
        this.url = url;
        register();
    }

    private void register() {
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).GET()
                .timeout(Duration.of(1000, ChronoUnit.MILLIS)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            apiToken = (response.statusCode() == HTTP_OK) ? response.body() : "DEBUG";
            System.out.println("API_TOKEN = " + apiToken);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerClntException("Error on register", e);
        }
    }

    public void put(String key, String value) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(value, StandardCharsets.UTF_8))
                .timeout(Duration.of(200, ChronoUnit.MILLIS))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HTTP_OK) {
                System.out.println("Value stored");
            } else {
                System.out.println(("Response code = " + response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerClntException("Error on put "+key, e);
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).GET()
                .timeout(Duration.of(200, ChronoUnit.MILLIS))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HTTP_OK) {
                System.out.println("Value loaded");
                return response.body();
            } else {
                System.out.println(("Response code = " + response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerClntException("Error on get "+key, e);
        }
        return null;
    }
}
