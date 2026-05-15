package gov.justucuman.seed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import java.util.regex.Pattern;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Spring Boot 4 API versioning + VPrefixedIntegerApiVersionParser accepts
    // /api/v1/... at runtime, but SpringDoc renders the parser's Integer value
    // verbatim, producing /api/1/... in the spec. Re-attach the v prefix here.
    private static final Pattern API_VERSION_SEGMENT = Pattern.compile("^/api/(\\d+)(/.*)?$");

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Seed API").version("1.0").description("API REST for seed"));
    }

    @Bean
    public OpenApiCustomizer apiVersionPathPrefixCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            Paths rewritten = new Paths();
            openApi.getPaths().forEach((path, item) -> {
                var matcher = API_VERSION_SEGMENT.matcher(path);
                String newPath = matcher.matches()
                        ? "/api/v" + matcher.group(1) + (matcher.group(2) == null ? "" : matcher.group(2))
                        : path;
                rewritten.addPathItem(newPath, item);
            });
            openApi.setPaths(rewritten);
        };
    }
}
