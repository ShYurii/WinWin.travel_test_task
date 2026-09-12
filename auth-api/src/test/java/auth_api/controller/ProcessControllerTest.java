package auth_api.controller;

import auth_api.client.DataApiClient;
import auth_api.dto.TransformResponse;
import auth_api.entity.User;
import auth_api.exception.DataApiException;
import auth_api.filter.JwtAuthenticationFilter;
import auth_api.repository.ProcessingLogRepository;
import auth_api.service.AuthService;
import auth_api.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DataApiClient dataApiClient;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private ProcessingLogRepository processingLogRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldProcessText() throws Exception {

        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(
                    invocation.getArgument(0),
                    invocation.getArgument(1)
            );
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@gmail.com",
                        null,
                        null
                );

        User user = new User("test@gmail.com", "hash");

        ReflectionTestUtils.setField(
                user,
                "id",
                UUID.randomUUID()
        );

        when(authService.findByEmail(any()))
                .thenReturn(user);

        when(dataApiClient.transform(any()))
                .thenReturn(new TransformResponse("HELLO"));

        mockMvc.perform(post("/api/process")
                        .with(authentication(authentication))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("HELLO"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenDataApiFails() throws Exception {

        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(
                    invocation.getArgument(0),
                    invocation.getArgument(1)
            );
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@gmail.com",
                        null,
                        null
                );

        User user = new User("test@gmail.com", "hash");

        ReflectionTestUtils.setField(
                user,
                "id",
                UUID.randomUUID()
        );

        when(authService.findByEmail(any()))
                .thenReturn(user);

        when(dataApiClient.transform(any()))
                .thenThrow(new DataApiException("Data API is unavailable"));

        mockMvc.perform(post("/api/process")
                        .with(authentication(authentication))
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                                "text": "hello"
                            }
                            """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value("Data API is unavailable"));
    }

    @Test
    void shouldRejectBlankText() throws Exception {

        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(
                    invocation.getArgument(0),
                    invocation.getArgument(1)
            );
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@gmail.com",
                        null,
                        null
                );

        mockMvc.perform(post("/api/process")
                        .with(authentication(authentication))
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                                "text": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
}
