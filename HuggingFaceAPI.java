import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HuggingFaceAPI {

    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-Nemo-Instruct-2407";
    private static final String API_TOKEN = "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"; // Replace with your new token
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_WAIT_TIME_MS = 20000; // 20 seconds

    public static void main(String[] args) {
        try {
            String apiToken = API_TOKEN;

            if (apiToken == null || apiToken.isEmpty()) {
                throw new IllegalArgumentException("Hugging Face API Token is not set.");
            }

            String inputText = "Quadratic Formula?";
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = "{\"inputs\": \"" + inputText + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();

            // Retry logic in case the model is loading
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    // Process and simplify the output
                    String simplifiedText = simplifyOutput(responseBody);
                    System.out.println("Simplified Output: " + simplifiedText);
                    break;
                } else if (response.body().contains("is currently loading")) {
                    System.out.println("Model is still loading, retrying in " + (RETRY_WAIT_TIME_MS / 1000) + " seconds...");
                    Thread.sleep(RETRY_WAIT_TIME_MS);
                } else {
                    throw new RuntimeException("Error: " + response.body());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to simplify the output by removing special characters and keeping only legible words/numbers
    private static String simplifyOutput(String responseBody) {
        // Step 1: Extract the generated text from the API response
        String generatedText = extractGeneratedText(responseBody);

        // Step 2: Remove unnecessary special characters, keeping only letters, numbers, and basic punctuation
        if (generatedText != null) {
            // Replace newlines, tabs, etc. with spaces
            generatedText = generatedText.replace("\\n", " ").replace("\\t", " ").replace("\\r", " ").trim();
            // Remove all special characters except letters, digits, basic punctuation
            generatedText = generatedText.replaceAll("[^a-zA-Z0-9.,!?+\\-*/= ]", "");
            // Normalize multiple spaces into a single space
            generatedText = generatedText.replaceAll("\\s+", " ");
        }

        return generatedText != null ? generatedText : "No legible output found.";
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
}
