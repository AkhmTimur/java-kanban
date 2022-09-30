package managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String apiToken;
    private final String urlToServer;

    public KVTaskClient(String urlToServer) {
        this.urlToServer = urlToServer;
        apiToken = getRegistered();
    }

    private String getRegistered() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlToServer + "/register")).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }

    public void put(String key, String json) {
        URI url = URI.create(urlToServer + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public String load(String key) {
        URI url = URI.create(urlToServer + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        if (response != null) {
            return response.body();
        } else {
            return null;
        }
    }
}
