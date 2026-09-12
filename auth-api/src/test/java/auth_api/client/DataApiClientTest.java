package auth_api.client;

import auth_api.dto.ProcessRequest;
import auth_api.dto.TransformResponse;
import auth_api.exception.DataApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DataApiClientTest {

    @Test
    void shouldTransformText() {

        RestClient.Builder builder = RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder.build();

        DataApiClient client = new DataApiClient(
                restClient,
                "my-secret",
                "http://localhost:8081"
        );

        server.expect(requestTo("http://localhost:8081/api/transform"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(header("X-Internal-Token", "my-secret"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(
                        """
                        {
                            "result": "HELLO"
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        TransformResponse response =
                client.transform(new ProcessRequest("hello"));

        assertThat(response.result()).isEqualTo("HELLO");

        server.verify();
    }

    @Test
    void shouldThrowDataApiExceptionWhenDataApiIsUnavailable() {

        RestClient.Builder builder = RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder.build();

        DataApiClient client = new DataApiClient(
                restClient,
                "my-secret",
                "http://localhost:8081"
        );

        server.expect(requestTo("http://localhost:8081/api/transform"))
                .andRespond(request -> {
                    throw new java.io.IOException("Connection failed");
                });

        assertThatThrownBy(() ->
                client.transform(new ProcessRequest("hello"))
        )
                .isInstanceOf(DataApiException.class)
                .hasMessage("Data API is unavailable");

        server.verify();
    }
}