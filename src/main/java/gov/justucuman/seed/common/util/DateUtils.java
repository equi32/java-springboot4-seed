package gov.justucuman.seed.common.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    public LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
