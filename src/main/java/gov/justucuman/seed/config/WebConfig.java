package gov.justucuman.seed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

    private static final String VERSIONED_PATH_PREFIX = "/api/";

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .usePathSegment(1, path -> {
                    String value = path.pathWithinApplication().value();
                    return value.startsWith(VERSIONED_PATH_PREFIX) && value.length() > VERSIONED_PATH_PREFIX.length();
                })
                .setVersionParser(new VPrefixedIntegerApiVersionParser())
                .setVersionRequired(false);
    }
}
