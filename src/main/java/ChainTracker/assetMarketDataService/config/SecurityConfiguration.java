package ChainTracker.assetMarketDataService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> {
            // Pagina de erro interna do Spring. Sem liberar, QUALQUER falha da
            // aplicacao vira 401 e a causa real fica escondida atras dele.
            auth.requestMatchers("/error").permitAll();
            // Catalogo de moedas e dado publico de mercado.
            auth.requestMatchers("/api/currency/**").permitAll();
            // Todo o resto exige um Bearer token valido do Keycloak.
            auth.anyRequest().authenticated();
        });
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
