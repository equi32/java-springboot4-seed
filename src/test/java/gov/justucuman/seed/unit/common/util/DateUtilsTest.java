package gov.justucuman.seed.unit.common.util;

import gov.justucuman.seed.common.util.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DateUtils Unit Tests")
class DateUtilsTest {

	@Test
	@DisplayName("Should convert a Timestamp to the equivalent LocalDateTime")
	void shouldConvertTimestampToLocalDateTime() {
		LocalDateTime now = LocalDateTime.of(2025, 5, 15, 12, 30, 45);
		Timestamp ts = Timestamp.valueOf(now);

		assertThat(DateUtils.toLocalDateTime(ts)).isEqualTo(now);
	}

	@Test
	@DisplayName("Should return null when input is null")
	void shouldReturnNullWhenInputIsNull() {
		assertThat(DateUtils.toLocalDateTime(null)).isNull();
	}
}
