package com.dairymart.dairyappserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    public static Date getDateFromString(String dateStr) {
        sdf = new SimpleDateFormat("dd-mm-yyyy");
        java.util.Date d = new java.util.Date();
        try {
            if(dateStr == null || dateStr.isEmpty()) {
                d = new Date(System.currentTimeMillis());
            }
            else {
                d = sdf.parse(dateStr);
            }
        } catch(ParseException e) {
            logger.error("Unable to parse date string: {}", dateStr);
        }
        return new Date(d.getTime());
    }

    public static boolean isSameDay(Timestamp timestamp) {
        LocalDate date1 = timestamp.toLocalDateTime().toLocalDate();
        LocalDate date2 = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime().toLocalDate();
        return date1.isEqual(date2);
    }

    public static boolean isYesterday(Timestamp timestamp) {
        if (timestamp == null) {
            return false; // Handle null values appropriately
        }

        // Get the current date in IST
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime nowIst = LocalDateTime.now(istZone).truncatedTo(ChronoUnit.DAYS);

        // Convert the Timestamp to LocalDateTime in IST
        LocalDateTime timestampIst = timestamp.toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(istZone).toLocalDateTime().truncatedTo(ChronoUnit.DAYS);

        // Calculate yesterday's date in IST
        LocalDateTime yesterdayIst = nowIst.minusDays(1);

        return timestampIst.isEqual(yesterdayIst);
    }
}
