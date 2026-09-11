package data_api.controller;

import data_api.dto.TransformRequest;
import data_api.dto.TransformResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class TransformController {

    @Value("${internal.token}")
    private String internalToken;

    @PostMapping("/transform")
    public TransformResponse transform(
            @RequestHeader(value = "X-Internal-Token", required = false) String token,
            @Valid @RequestBody TransformRequest request) {

        if (!internalToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String result = request.text().toUpperCase();

        return new TransformResponse(result);
    }
}