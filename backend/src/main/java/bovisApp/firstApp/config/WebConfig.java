package bovisApp.firstApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permitir CORS em todas as rotas
                .allowedOrigins("*") // Permitir acesso de qualquer frontend (pode restringir para produção)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permitir todos os métodos HTTP
                .allowedHeaders("*") // Permitir qualquer cabeçalho
                .exposedHeaders("Authorization") // Expor cabeçalhos como Authorization (se precisar)
                .allowCredentials(false); // ⚠️ Não usar `true` se `allowedOrigins("*")` estiver definido!
    }
}
