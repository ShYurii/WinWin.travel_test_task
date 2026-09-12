package data_api.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = "internal.token=my-secret")
@WebMvcTest(TransformController.class)
class TransformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldTransformText() throws Exception {

        mockMvc.perform(post("/api/transform")
                        .header("X-Internal-Token", "my-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("HELLO"));
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {

        mockMvc.perform(post("/api/transform")
                        .header("X-Internal-Token", "wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "text": "hello"
                            }
                            """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectBlankText() throws Exception {

        mockMvc.perform(post("/api/transform")
                        .header("X-Internal-Token", "my-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "text": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
}