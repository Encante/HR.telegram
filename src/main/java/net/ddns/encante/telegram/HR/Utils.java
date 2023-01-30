package net.ddns.encante.telegram.HR;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String getCurrentDateTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public static Long getCurrentUnixTime(){
        return Instant.now().getEpochSecond();
    }
}
