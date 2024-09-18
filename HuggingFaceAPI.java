import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HuggingFaceAPI {

    private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2";
    private static final String API_TOKEN = "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"; // Replace with your new token

    public static void main(String[] args) {
        try {
            String apiToken = API_TOKEN;

            if (apiToken == null || apiToken.isEmpty()) {
                throw new IllegalArgumentException("Hugging Face API Token is not set.");
            }

            String inputText = "What are some good coding websites?";

            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = "{\"inputs\": \"" + inputText + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error: " + response.body());
            }

            String responseBody = response.body();
            System.out.println("Response Body: " + responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to extract the "generated_text" from the API response
    private static String extractGeneratedText(String responseBody) {
        String startKey = "\"generated_text\":\"";
        int startIndex = responseBody.indexOf(startKey);
        if (startIndex != -1) {
            startIndex += startKey.length();
            int endIndex = responseBody.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return responseBody.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    // Method to extract a simple math answer from the generated text
    private static String extractSimpleMathAnswer(String generatedText) {
        // Replace newlines and special characters with spaces
        generatedText = generatedText.replace("\\n", " ").replaceAll("[^0-9+\\-*/= ]", "").trim();

        // Split the text into individual parts and only consider simple math expressions
        String[] parts = generatedText.split(" ");
        String bestExpression = "";

        for (String part : parts) {
            // Include only simple math expressions (e.g., "1 + 1")
            if (part.matches("[0-9]+[+\\-*/][0-9]+")) {
                bestExpression = part; // Keep the first valid expression we encounter
                break; // Exit early once we find a valid expression
            }
        }

        return bestExpression.isEmpty() ? "Answer not found." : bestExpression;
    }
}