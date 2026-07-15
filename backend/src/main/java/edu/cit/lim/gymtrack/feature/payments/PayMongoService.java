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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class PayMongoService {

    private static final String API_BASE = "https://api.paymongo.com/v1";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${paymongo.secret-key:}")
    private String secretKey;

    @Value("${paymongo.webhook-secret:}")
    private String webhookSecret;

    @Value("${paymongo.mock-enabled:false}")
    private boolean mockEnabled;

    public record CheckoutSessionResult(String checkoutId, String checkoutUrl, boolean mockCheckout) {}

    public boolean isMockEnabled() {
        return mockEnabled || secretKey == null || secretKey.isBlank();
    }

    public CheckoutSessionResult createCheckoutSession(
            SubscriptionPlan plan,
            String reference,
            String successUrl,
            String cancelUrl) {
        if (isMockEnabled()) {
            return new CheckoutSessionResult(
                    "mock_" + reference,
                    successUrl + (successUrl.contains("?") ? "&" : "?") + "reference=" + reference,
                    true
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
            paymentMethods.add("grab_pay");
            paymentMethods.add("card");
            paymentMethods.add("qrph");
            attributes.set("payment_method_types", paymentMethods);
            attributes.put("success_url", successUrl + (successUrl.contains("?") ? "&" : "?") + "reference=" + reference);
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
            if (checkoutId.isBlank() || checkoutUrl.isBlank()) {
                throw new IllegalStateException("PayMongo returned an incomplete checkout session.");
            }
            return new CheckoutSessionResult(checkoutId, checkoutUrl, false);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create PayMongo checkout session: " + e.getMessage(), e);
        }
    }

    public boolean verifyWebhookSignature(String signatureHeader, String rawBody) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return false;
        }

        try {
            String timestamp = null;
            String testSignature = null;
            String liveSignature = null;
            for (String part : signatureHeader.split(",")) {
                String trimmed = part.trim();
                if (trimmed.startsWith("t=")) {
                    timestamp = trimmed.substring(2);
                } else if (trimmed.startsWith("te=")) {
                    testSignature = trimmed.substring(3);
                } else if (trimmed.startsWith("li=")) {
                    liveSignature = trimmed.substring(3);
                }
            }

            String providedSignature = isLiveMode() ? liveSignature : testSignature;
            if (timestamp == null || providedSignature == null || providedSignature.isBlank()) {
                return false;
            }

            String signedPayload = timestamp + "." + rawBody;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computed = mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
            byte[] provided = hexToBytes(providedSignature);
            return MessageDigest.isEqual(computed, provided);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPaidEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("data").path("attributes").path("type").asText("");
            return eventType.contains("payment.paid")
                    || eventType.contains("checkout.session.completed")
                    || eventType.contains("checkout_session.payment.paid");
        } catch (Exception e) {
            return false;
        }
    }

    public String extractCheckoutIdFromWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode data = root.path("data");
            JsonNode attributes = data.path("attributes");
            JsonNode nested = attributes.path("data");

            String nestedId = nested.path("id").asText(null);
            if (nestedId != null && nestedId.startsWith("cs_")) {
                return nestedId;
            }

            String checkoutFromPayment = nested.path("attributes").path("checkout_session_id").asText(null);
            if (checkoutFromPayment != null && !checkoutFromPayment.isBlank()) {
                return checkoutFromPayment;
            }

            if (data.path("type").asText("").contains("checkout")) {
                return data.path("id").asText(null);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String extractReferenceFromWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode attributes = root.path("data").path("attributes").path("data").path("attributes");
            String reference = attributes.path("reference_number").asText(null);
            if (reference != null && !reference.isBlank()) {
                return reference;
            }
            return root.path("data").path("attributes").path("reference_number").asText(null);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractPaymentMethod(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String method = root.path("data").path("attributes").path("data").path("attributes")
                    .path("source").path("type").asText("");
            return method.isBlank() ? "paymongo" : method;
        } catch (Exception e) {
            return "paymongo";
        }
    }

    public String extractPaymongoPaymentId(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode nested = root.path("data").path("attributes").path("data");
            String paymentId = nested.path("id").asText(null);
            if (paymentId != null && paymentId.startsWith("pay_")) {
                return paymentId;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isLiveMode() {
        return secretKey != null && secretKey.startsWith("sk_live");
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + token);
        return headers;
    }

    private static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
