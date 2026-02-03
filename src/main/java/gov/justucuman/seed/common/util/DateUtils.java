package gov.justucuman.seed.common.util;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@UtilityClass
public class DateUtils {

    public LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toLocalDateTime();
    }
}
