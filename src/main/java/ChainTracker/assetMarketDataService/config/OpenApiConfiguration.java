package ChainTracker.assetMarketDataService.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI assetMarketDataOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChainTracker - assetMarketDataService")
                        .description("""
                                Catálogo e cotações de criptomoedas e ações do ChainTracker.

                                As rotas sob /api/currency, /api/stock e /api/internal exigem um JWT do
                                realm ChainTracker. As rotas sob /api/bot exigem um token de serviço com
                                o escopo bot.

                                Use o botão Authorize com um Bearer token do Keycloak para testar as rotas.
                                """)
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT emitido pelo Keycloak. Para /api/bot/**, o token precisa ter o escopo bot.")));
    }
}
