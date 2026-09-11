package auth_api.controller;

import auth_api.client.DataApiClient;
import auth_api.dto.ProcessRequest;
import auth_api.dto.TransformResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProcessController {

    private final DataApiClient dataApiClient;

    public ProcessController(DataApiClient dataApiClient) {
        this.dataApiClient = dataApiClient;
    }

    @PostMapping("/process")
    public TransformResponse process(@RequestBody ProcessRequest request) {
        return dataApiClient.transform(request);
    }
}