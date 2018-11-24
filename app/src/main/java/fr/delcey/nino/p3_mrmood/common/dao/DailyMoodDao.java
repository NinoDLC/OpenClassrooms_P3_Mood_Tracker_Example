package fr.delcey.nino.p3_mrmood.common.dao;

import static fr.delcey.nino.p3_mrmood.common.Consts.MAXIMUM_NUMBER_OF_MOOD_DISPLAYED_IN_HISTORY;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.Random;
import javax.annotation.Nullable;

/**
 * Store the daily mood of the user. Can store maximum one mood per day (if a second one is provided the same day,
 * the older one will be updated. {@link #setTodayComment(String)} is optionnal.
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
    
    // region debug
    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do "
        + "eiusmod tempor incididunt ut labore et dolore magna aliqua. Lacinia quis vel eros donec ac odio. Morbi non"
        + " arcu risus quis varius. Sem et tortor consequat id. Arcu non sodales neque sodales ut. Blandit massa enim"
        + " nec dui nunc mattis enim ut. Sed euismod nisi porta lorem mollis aliquam ut. Dictum fusce ut placerat "
        + "orci nulla. Mauris augue neque gravida in fermentum et sollicitudin ac. Sem fringilla ut morbi tincidunt "
        + "augue interdum velit. Parturient montes nascetur ridiculus mus mauris vitae ultricies. Bibendum neque "
        + "egestas congue quisque egestas diam in arcu. Nulla pharetra diam sit amet. Euismod nisi porta lorem mollis"
        + ". Vitae semper quis lectus nulla. Sed id semper risus in hendrerit gravida. Aliquet nibh praesent "
        + "tristique magna sit amet purus gravida quis. Porta nibh venenatis cras sed felis eget velit aliquet. Eu "
        + "sem integer vitae justo eget magna fermentum. Neque vitae tempus quam pellentesque nec nam aliquam sem.";
    
    public void injectMockData(Context context) {
        DailyMood dailyMood = Realm.getDefaultInstance().where(DailyMood.class).findFirst();
        
        if (dailyMood == null) {
            Realm.getDefaultInstance().beginTransaction();
            
            Random random = new Random();
            
            for (int i = 0, total = 0; total < 7; i++) {
                // 62.5% chance to occur
                if (random.nextInt(9) > 2) {
                    total++;
                    DailyMood mockedMood = new DailyMood();
                    
                    // 50% chance to occur
                    if (random.nextInt() % 2 == 0) {
                        String mock = LOREM_IPSUM.substring(0,
                                                            (int) (LOREM_IPSUM.length() * random.nextDouble()));
                        mockedMood.setComment(mock);
                    }
                    
                    mockedMood.setMood(Mood.values()[random.nextInt(Mood.values().length)]);
                    mockedMood.setDate(DateUtils.getNow()
                                                .minusDays(i + 1)
                                                .withHour(random.nextInt(24))
                                                .withMinute(random.nextInt(60)));
                    
                    Log.v(TAG, "injectMockData() called, injecting mockedMood = [" + mockedMood + "]");
                    
                    Realm.getDefaultInstance().copyToRealmOrUpdate(mockedMood);
                }
            }
            
            Log.i(TAG, "injectMockData() called !");
            Toast.makeText(context, "injectMockData() called !", Toast.LENGTH_SHORT).show();
            
            Realm.getDefaultInstance().commitTransaction();
        }
    }
    
    // endregion
}
