package auth_api.security;

import auth_api.filter.JwtAuthenticationFilter;
import auth_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldAuthenticateUserWhenValidTokenProvided() throws Exception {

        JwtService jwtService = mock(JwtService.class);
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtService.extractEmail("test-token"))
                .thenReturn("test@gmail.com");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName())
                .isEqualTo("test@gmail.com");

        verify(jwtService).extractEmail("test-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {

        JwtService jwtService = mock(JwtService.class);
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus())
                .isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        verify(jwtService).extractEmail("invalid-token");
        verify(filterChain, never()).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
}
