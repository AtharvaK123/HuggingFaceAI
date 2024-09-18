import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HuggingFaceAPIExample {
    private static final String API_URL = "https://api-inference.huggingface.co/models/mattshumer/Reflection-Llama-3.1-70B"; // Replace with your model
    private static final String API_TOKEN = "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"; // Replace with your API token

    public static void main(String[] args) {
        String inputText = "What is one plus one?";
        String response = callHuggingFaceAPI(inputText);
        System.out.println("Response: " + response);
    }

    public static String callHuggingFaceAPI(String inputText) {
        String responseString = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Authorization", "Bearer " + API_TOKEN);
            post.setHeader("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("inputs", inputText);

            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                responseString = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }
}
