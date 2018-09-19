package fr.delcey.nino.p3_mrmood.common.dao;

import static fr.delcey.nino.p3_mrmood.common.Consts.MAXIMUM_NUMBER_OF_MOOD_DISPLAYED_IN_HISTORY;

import android.support.annotation.NonNull;
import android.util.Log;
import fr.delcey.nino.p3_mrmood.common.Consts;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import javax.annotation.Nullable;
import org.threeten.bp.ZonedDateTime;

/**
 * Store the daily mood of the user. Can store maximum one mood per day (if a second one is provided the same day,
 * the older one will be updated. {@link #setTodayComment(String)} is optionnal.
 * TODO VOLKO CHECK OTHER USES OF REALM.GETDEFAULT()
 */
public class DailyMoodDao {
    
    private static final String TAG = DailyMoodDao.class.getSimpleName();
    
    private static volatile DailyMoodDao sDailyMoodDao;
    
    private DailyMoodDao() {
        // Don't allow instanciation of this Singleton
    }
    
    public static DailyMoodDao getInstance() {
        if (DailyMoodDao.sDailyMoodDao == null) {
            synchronized (DailyMoodDao.class) {
                if (DailyMoodDao.sDailyMoodDao == null) {
                    DailyMoodDao.sDailyMoodDao = new DailyMoodDao();
                }
            }
        }
        
        return DailyMoodDao.sDailyMoodDao;
    }
    
    public void setTodayMood(@NonNull Mood mood) {
        Log.i(TAG, "setTodayMood() called with: " + "mood = [" + mood + "]");
        
        Realm.getDefaultInstance().beginTransaction();
        
        DailyMood currentMood = getCurrentMood();
        
        if (currentMood == null) {
            currentMood = new DailyMood();
            currentMood.setDate(DateUtils.getNow());
        }
        
        currentMood.setMood(mood);
        Realm.getDefaultInstance().copyToRealmOrUpdate(currentMood);
        
        Realm.getDefaultInstance().commitTransaction();
    }
    
    public void setTodayComment(@Nullable String comment) {
        Log.i(TAG, "setTodayComment() called with: " + "comment = [" + comment + "]");
        
        Realm.getDefaultInstance().beginTransaction();
        
        DailyMood currentMood = getCurrentMood();
        
        // Should happen only in extreme cases (saving the mood around midnight)
        if (currentMood == null) {
            currentMood = new DailyMood();
            currentMood.setDate(DateUtils.getNow());
            currentMood.setMood(Mood.NEUTRAL);
        }
        
        currentMood.setComment(comment);
        Realm.getDefaultInstance().copyToRealmOrUpdate(currentMood);
        
        Realm.getDefaultInstance().commitTransaction();
    }
    
    @NonNull
    public RealmResults<DailyMood> getLastSevenMoods() {
        return Realm.getDefaultInstance()
                    .where(DailyMood.class)
                    .lessThan(DailyMood.NAME_KEY,
                              DateUtils.getDateAsNumber(DateUtils.getNow()))
                    .sort(DailyMood.NAME_KEY, Sort.DESCENDING)
                    .limit(MAXIMUM_NUMBER_OF_MOOD_DISPLAYED_IN_HISTORY)
                    .findAll();
    }
    
    @Nullable
    public DailyMood getCurrentMood() {
        return Realm.getDefaultInstance()
                    .where(DailyMood.class)
                    .equalTo(DailyMood.NAME_KEY,
                             DateUtils.getDateAsNumber(DateUtils.getNow()))
                    .findFirst();
    }
}
