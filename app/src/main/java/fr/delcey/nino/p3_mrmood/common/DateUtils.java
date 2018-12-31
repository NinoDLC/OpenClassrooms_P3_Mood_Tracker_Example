package fr.delcey.nino.p3_mrmood.common;

import android.support.annotation.NonNull;
import org.threeten.bp.ZonedDateTime;

public class DateUtils {
    
    /**
     * @return any passed date as an int, formatted YYYYMMDD. Example : 20181118 for November, 18th 2018 or 20000901 for
     * September 1st 2000
     */
    public static int getDateAsNumber(@NonNull ZonedDateTime zonedDateTime) {
        return zonedDateTime.getDayOfMonth() +
            zonedDateTime.getMonth().getValue() * 100 +
            zonedDateTime.getYear() * 100_00;
    }
    
    /**
     * TODO : Support TimeZoneOffset switching
     *
     * @return the current date
     */
    public static ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }
}
