package helloworld;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.core.type.TypeReference;
import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * The intelliJ template for an AWS project isn't great.  This does not test what is on the server,
 * but it illustrates on how one can test events coming from other AWS services.  It's left in here
 * for illustration purposes.
 */
class AppTest {
    static final App app = new App();
    static final ObjectMapper mapper = new ObjectMapper();

    final APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
    final TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
    };

    @Test
    void completesSuccessfully() {
        APIGatewayProxyResponseEvent result = executeHandler();

        Assertions.assertEquals(200, result.getStatusCode());
    }

    private APIGatewayProxyResponseEvent executeHandler() {
        event.setBody("{\"key\":\"value\"}");
        return app.handleRequest(event, null);
    }

    @Test
    void hasCorrectContentType() {
        APIGatewayProxyResponseEvent result = executeHandler();

        Assertions.assertEquals("application/json", result.getHeaders().get("Content-Type"));
    }

    @Test
    void returnsExpectedMessage() throws Throwable {
        APIGatewayProxyResponseEvent result = executeHandler();

        Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");
        expected.put("location", "95.145.134.118");
        expected.put("id", "This is a lambda-java test");

        Assertions.assertEquals(expected, mapper.readValue(result.getBody(), typeRef));
    }
}
