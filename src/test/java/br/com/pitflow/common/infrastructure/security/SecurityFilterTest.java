package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.common.core.gateway.TokenGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {
    private final TokenGateway tokens = mock(TokenGateway.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);
    private final SecurityFilter filter = new SecurityFilter(tokens);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesCustomerRemovingSubjectPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer customer-token");
        when(tokens.validateToken("customer-token")).thenReturn("customer:78177454048");
        when(tokens.getClaims("customer-token")).thenReturn(Map.of("role", "ROLE_CUSTOMER"));

        filter.doFilterInternal(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("78177454048", authentication.getName());
        assertEquals("ROLE_CUSTOMER", authentication.getAuthorities().iterator().next().getAuthority());
        verify(chain).doFilter(request, response);
    }

    @Test
    void authenticatesMechanicWithoutChangingSubject() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer mechanic-token");
        when(tokens.validateToken("mechanic-token")).thenReturn("mechanic.user");
        when(tokens.getClaims("mechanic-token")).thenReturn(Map.of("role", "ROLE_MECHANIC"));

        filter.doFilterInternal(request, response, chain);

        assertEquals("mechanic.user", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void ignoresMissingMalformedUnsupportedAndNullSubjectTokens() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn(null)
                .thenReturn("Basic abc")
                .thenReturn("Bearer unsupported")
                .thenReturn("Bearer null-subject");
        when(tokens.validateToken("unsupported")).thenReturn("user");
        when(tokens.getClaims("unsupported")).thenReturn(Map.of("role", "ROLE_ADMIN"));
        when(tokens.validateToken("null-subject")).thenReturn(null);

        for (int i = 0; i < 4; i++) {
            filter.doFilterInternal(request, response, chain);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
        verify(chain, times(4)).doFilter(request, response);
    }

    @Test
    void clearsExistingContextWhenValidationFails() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer broken");
        when(tokens.validateToken("broken")).thenThrow(new RuntimeException("invalid"));

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
