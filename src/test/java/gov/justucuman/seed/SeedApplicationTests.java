package gov.justucuman.seed;

import gov.justucuman.seed.test.containers.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class SeedApplicationTests {

	@Test
	void contextLoads() {
	}

}
