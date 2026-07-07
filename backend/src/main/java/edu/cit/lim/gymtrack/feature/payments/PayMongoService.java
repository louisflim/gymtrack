package edu.cit.lim.gymtrack.feature.payments;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PayMongoService {

    private static final String API_BASE = "https://api.paymongo.com/v1";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${paymongo.secret-key:}")
    private String secretKey;

    @Value("${paymongo.success-url:http://localhost:5173/payment/success}")
    private String successUrl;

    @Value("${paymongo.cancel-url:http://localhost:5173/payment/cancel}")
    private String cancelUrl;

    @Value("${paymongo.mock-enabled:false}")
    private boolean mockEnabled;

    public record CheckoutSessionResult(String checkoutId, String checkoutUrl) {}

    public CheckoutSessionResult createCheckoutSession(SubscriptionPlan plan, String reference) {
        if (mockEnabled || secretKey == null || secretKey.isBlank()) {
            return new CheckoutSessionResult(
                    "mock_" + reference,
                    successUrl + "?reference=" + reference
            );
        }

        try {
            long amountCentavos = plan.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

            ObjectNode lineItem = objectMapper.createObjectNode();
            lineItem.put("currency", "PHP");
            lineItem.put("amount", amountCentavos);
            lineItem.put("name", plan.getName());
            lineItem.put("quantity", 1);

            ArrayNode lineItems = objectMapper.createArrayNode();
            lineItems.add(lineItem);

            ObjectNode attributes = objectMapper.createObjectNode();
            attributes.put("send_email_receipt", false);
            attributes.set("line_items", lineItems);
            ArrayNode paymentMethods = objectMapper.createArrayNode();
            paymentMethods.add("gcash");
            paymentMethods.add("paymaya");
            paymentMethods.add("card");
            attributes.set("payment_method_types", paymentMethods);
            attributes.put("success_url", successUrl + "?reference=" + reference);
            attributes.put("cancel_url", cancelUrl);
            attributes.put("description", "GymTrack subscription: " + plan.getName());
            attributes.put("reference_number", reference);

            ObjectNode data = objectMapper.createObjectNode();
            data.put("type", "checkout_session");
            data.set("attributes", attributes);

            ObjectNode body = objectMapper.createObjectNode();
            body.set("data", data);

            HttpHeaders headers = authHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> response = restTemplate.exchange(
                    API_BASE + "/checkout_sessions",
                    HttpMethod.POST,
                    new HttpEntity<>(body.toString(), headers),
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode sessionData = root.path("data");
            String checkoutId = sessionData.path("id").asText();
            String checkoutUrl = sessionData.path("attributes").path("checkout_url").asText();
            return new CheckoutSessionResult(checkoutId, checkoutUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create PayMongo checkout session: " + e.getMessage(), e);
        }
    }

    public String extractCheckoutIdFromWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode data = root.path("data");
            String type = data.path("attributes").path("type").asText("");
            if (type.contains("checkout.session")) {
                return data.path("attributes").path("data").path("id").asText(null);
            }
            return data.path("id").asText(null);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractPaymentMethod(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            return root.path("data").path("attributes").path("data").path("attributes")
                    .path("source").path("type").asText("paymongo");
        } catch (Exception e) {
            return "paymongo";
        }
    }

    public boolean isPaidEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("data").path("attributes").path("type").asText("");
            return eventType.contains("payment.paid") || eventType.contains("checkout.session.completed");
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + token);
        return headers;
    }
}
