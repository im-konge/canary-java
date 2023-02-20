/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package common.time;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class TimeUtils {

    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static Timestamp getCurrentTime() {
        ZoneId zone = ZoneId.of(DEFAULT_TIMEZONE);
        LocalDateTime dateTime = LocalDateTime.now(zone);

        return Timestamp.valueOf(dateTime);
    }

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_TIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

        return dateFormat;
    }
}
