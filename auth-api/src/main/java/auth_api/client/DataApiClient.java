package auth_api.client;

import auth_api.dto.ProcessRequest;
import auth_api.dto.TransformResponse;
import auth_api.exception.DataApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class DataApiClient {

    private final RestClient restClient;
    private final String internalToken;

    public DataApiClient(RestClient restClient,
                         @Value("${internal.token}") String internalToken) {
        this.internalToken = internalToken;
        this.restClient = restClient;
    }

    public TransformResponse transform(ProcessRequest request) {
        try {
            return restClient.post()
                    .uri("http://localhost:8081/api/transform")
                    .header("X-Internal-Token", internalToken)
                    .body(request)
                    .retrieve()
                    .body(TransformResponse.class);

        } catch (RestClientException e) {
            throw new DataApiException("Data API is unavailable");
        }
    }
}