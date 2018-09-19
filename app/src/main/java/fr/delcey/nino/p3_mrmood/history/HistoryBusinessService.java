package fr.delcey.nino.p3_mrmood.history;

import android.content.Context;
import android.support.annotation.NonNull;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

class HistoryBusinessService {
    
    @NonNull
    String getHowLongAgo(Context context, @NonNull ZonedDateTime date) {
        String result;
        
        int daysBetween = (int) ChronoUnit.DAYS.between(date, DateUtils.getLastInstantOfToday());
        
        switch (daysBetween) {
            case 0:
                result = context.getString(R.string.today);
                break;
            case 1:
                result = context.getString(R.string.yesterday);
                break;
            case 2:
                result = context.getString(R.string.the_day_before_yesterday);
                break;
            case 7:
                result = context.getString(R.string.a_week_ago);
                break;
            default:
                result = context.getString(R.string.x_days_before, daysBetween);
                break;
        }
        
        return result;
    }
}