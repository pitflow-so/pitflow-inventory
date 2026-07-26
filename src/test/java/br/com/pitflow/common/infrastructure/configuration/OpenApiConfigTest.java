package br.com.pitflow.common.infrastructure.configuration;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void shouldConfigureInventoryMetadataAndJwtSecurityScheme() {
        var openAPI = config.customOpenAPI();
        var bearerAuth = openAPI.getComponents()
                .getSecuritySchemes()
                .get(OpenApiConfig.SECURITY_SCHEME_NAME);

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("PitFlow Inventory API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
        assertThat(openAPI.getInfo().getDescription()).contains("catálogo de peças");
        assertThat(bearerAuth.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(bearerAuth.getScheme()).isEqualTo("bearer");
        assertThat(bearerAuth.getBearerFormat()).isEqualTo("JWT");
    }
}
