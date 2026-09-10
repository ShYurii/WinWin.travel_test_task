package data_api.controller;

import data_api.dto.TransformRequest;
import data_api.dto.TransformResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransformController {

    @PostMapping("/transform")
    public TransformResponse transform(@RequestBody TransformRequest request) {

        String result = request.text().toUpperCase();

        return new TransformResponse(result);
    }
}