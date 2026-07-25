package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.common.infrastructure.security.JwtServiceImp;
import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventorySecurityBeansConfig {
    @Bean
    TokenGateway tokenGateway(@Value("${api.security.token.secret}") String secret,
                              @Value("${api.security.token.expiration-hours:3}") Integer expirationHours) {
        return new JwtServiceImp(secret, expirationHours);
    }

    @Bean
    SecurityFilter securityFilter(TokenGateway tokenGateway) {
        return new SecurityFilter(tokenGateway);
    }
}
