package helloworld;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.core.type.TypeReference;
import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map<String, String>, APIGatewayProxyResponseEvent> {
    // this really ought to go into a factory of sorts...
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
    };

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        final APIGatewayProxyResponseEvent response = makeResponseEvent();
        try {
            Map<String, String> map = mapper.readValue(input.getBody(), typeRef);
            map.put("location",  this.getPageLocation());
            map.put("id", "This is a lambda-java test");
            return response
                    .withStatusCode(200)
                    .withBody(mapper.writeValueAsString(map));
        } catch (IOException e) {
            // TODO find out which logger is best for AWS to report errors.
            System.err.println(e);
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private static APIGatewayProxyResponseEvent makeResponseEvent() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        return new APIGatewayProxyResponseEvent().withHeaders(headers);
    }

    private String getPageLocation() throws IOException {
        URL url = new URL("https://checkip.amazonaws.com");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, String> input, Context context) {

        System.out.println(input);

        final APIGatewayProxyResponseEvent response = makeResponseEvent();
        try {
            input.put("location",  this.getPageLocation());
            input.put("id", "This is a lambda-java test");
            return response
                    .withStatusCode(200)
                    .withBody(mapper.writeValueAsString(input));
        } catch (IOException e) {
            // TODO find out which logger is best for AWS to report errors.
            System.err.println(e);
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }
}
