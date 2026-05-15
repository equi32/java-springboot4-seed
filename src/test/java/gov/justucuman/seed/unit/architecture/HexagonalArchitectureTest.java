package gov.justucuman.seed.unit.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import gov.justucuman.seed.SeedApplication;

/**
 * Enforces the hexagonal (ports & adapters) layering of this codebase. Violations
 * fail the build on {@code ./gradlew check}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Domain is framework-agnostic (no Spring / JPA / Hibernate / Kafka / OpenSearch / etc.).</li>
 *   <li>Domain does not depend on application or infrastructure.</li>
 *   <li>Application does not depend on infrastructure.</li>
 *   <li>Port-in interfaces are implemented only in the application layer; port-out interfaces
 *       are implemented only in the infrastructure output-adapter layer.</li>
 * </ul>
 */
@AnalyzeClasses(packagesOf = SeedApplication.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_be_framework_agnostic = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..",
                    "jakarta.servlet..",
                    "org.hibernate..",
                    "org.apache.kafka..",
                    "org.springframework.kafka..",
                    "org.opensearch..",
                    "org.elasticsearch..",
                    "org.springdoc..",
                    "org.mapstruct..",
                    "com.fasterxml.jackson..",
                    "reactor..",
                    "io.swagger..")
            .because("the domain layer must not depend on frameworks or infrastructure libraries");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application_or_infrastructure = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..application..", "..infrastructure..")
            .because("dependencies in hexagonal architecture flow inward: infrastructure -> application -> domain");

    @ArchTest
    static final ArchRule application_should_not_depend_on_infrastructure = noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .because("use cases must not know about adapters; they depend on ports defined in the domain");

    @ArchTest
    static final ArchRule port_in_should_be_implemented_only_in_application = classes()
            .that()
            .implement(JavaClass.Predicates.resideInAPackage("..domain.port.in.."))
            .should()
            .resideInAPackage("..application..")
            .because("port-in (use case) interfaces must be implemented by application-layer use cases");

    @ArchTest
    static final ArchRule port_out_should_be_implemented_only_in_infrastructure_output = classes()
            .that()
            .implement(JavaClass.Predicates.resideInAPackage("..domain.port.out.."))
            .should()
            .resideInAPackage("..infrastructure.adapter.output..")
            .because("port-out interfaces must be implemented by infrastructure output adapters");
}
