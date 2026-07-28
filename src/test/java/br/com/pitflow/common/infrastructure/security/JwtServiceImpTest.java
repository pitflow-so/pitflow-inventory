package br.com.pitflow.common.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImpTest {
    private static final String SECRET =
            "364161b203a948480795c643b01859942a0094072f883b27b407a51801311099";
    private JwtServiceImp service;

    @BeforeEach
    void setUp() {
        service = new JwtServiceImp(SECRET, 1);
    }

    @Test
    void generatesValidatesAndReadsClaims() {
        var token = service.generateToken("customer:123",
                Map.of("role", "ROLE_CUSTOMER", "name", "Cliente"));

        assertEquals(3, token.split("\\.").length);
        assertEquals("customer:123", service.validateToken(token));
        assertEquals("ROLE_CUSTOMER", service.getClaims(token).get("role"));
    }

    @Test
    void rejectsMalformedTokenForValidationAndClaims() {
        assertAll(
                () -> assertThrows(RuntimeException.class, () -> service.validateToken("invalid")),
                () -> assertThrows(RuntimeException.class, () -> service.getClaims("invalid"))
        );
    }

    @Test
    void wrapsTokenGenerationFailure() {
        var invalidSecretService = new JwtServiceImp("short", 1);
        var exception = assertThrows(RuntimeException.class,
                () -> invalidSecretService.generateToken("subject", Map.of()));
        assertEquals("Error generating JWT token", exception.getMessage());
    }
}
