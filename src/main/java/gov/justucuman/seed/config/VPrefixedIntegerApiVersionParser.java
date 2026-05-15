package gov.justucuman.seed.config;

import org.springframework.web.accept.ApiVersionParser;

class VPrefixedIntegerApiVersionParser implements ApiVersionParser<Integer> {

    @Override
    public Integer parseVersion(String version) {
        String raw = (version.startsWith("v") || version.startsWith("V"))
                ? version.substring(1)
                : version;
        return Integer.parseInt(raw);
    }
}
