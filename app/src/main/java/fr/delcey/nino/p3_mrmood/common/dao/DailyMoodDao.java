package fr.delcey.nino.p3_mrmood.common.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.threeten.bp.ZonedDateTime;

/**
 * Store the daily mood of the user. Can store maximum one mood per day (if a second one is provided the same day,
 * the older one will be updated). {@link #upsertTodayComment(Context, String)} is optionnal.
 */
public class DailyMoodDao {
    
    private static final String TAG = DailyMoodDao.class.getSimpleName();
    
    private static final String KEY_SHARED_PREFERENCE_NAME = "DAILY_MOOD_DAO";
    
    private final Gson mGson = new Gson();
    
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
    
    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(KEY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    
    private String getKey(ZonedDateTime zonedDateTime) {
        return String.valueOf(DateUtils.getDateAsNumber(zonedDateTime));
    }
    
    public void upsertTodayMood(Context context, @NonNull Mood mood) {
        Log.i(TAG, "upsertTodayMood() called with: " + "mood = [" + mood + "]");
        
        DailyMood currentMood = getCurrentMood(context);
        
        if (currentMood == null) {
            currentMood = new DailyMood();
        }
        
        currentMood.setMood(mood);
        
        upsertMood(context, currentMood);
    }
    
    public void upsertTodayComment(Context context, @Nullable String comment) {
        Log.i(TAG, "upsertTodayComment() called with: " + "comment = [" + comment + "]");
        
        DailyMood currentMood = getCurrentMood(context);
        
        // Should happen only in extreme cases (inserting a new comment around midnight)
        if (currentMood == null) {
            currentMood = new DailyMood();
        }
        
        currentMood.setComment(comment);
        
        upsertMood(context, currentMood);
    }
    
    private void upsertMood(Context context, @NonNull DailyMood currentMood) {
        getSharedPreferences(context).edit()
                                     .putString(getKey(currentMood.getDate()), mGson.toJson(currentMood))
                                     .apply();
    }
    
    @NonNull
    public List<DailyMood> getLastSevenMoods(Context context) {
        // SharePreferences.getAll() return a map, which doesn't garantee the insertion order
        // and iteration order is respected. It's time for some algotrithm.
        Map<String, ?> map = getSharedPreferences(context).getAll();
        
        Set<DailyMood> allDailyMoodsSorted = new TreeSet<>(new Comparator<DailyMood>() {
            @Override
            public int compare(DailyMood o1, DailyMood o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        
        // TODO OMG FIX THIS ABOMINATION AND OPTIMIZE IT (WITH SMART SUBCLASS OF TREESET ORDERING ONLY 7 ELEMENTS ?)
        for (Object serializedDailyMood : map.values()) {
            if (serializedDailyMood instanceof String) {
                try {
                    DailyMood dailyMood = mGson.fromJson((String) serializedDailyMood, DailyMood.class);
                    allDailyMoodsSorted.add(dailyMood);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        
        List<DailyMood> first7Results = new ArrayList<>(7);
        int i = 0;
        for (DailyMood dailyMood : allDailyMoodsSorted) {
            if (i < 7) {
                i++;
            } else {
                break;
            }
            
            first7Results.add(dailyMood);
        }
        
        return first7Results;
    }
    
    @Nullable
    public DailyMood getCurrentMood(Context context) {
        String serializedDailyMood = getSharedPreferences(context).getString(getKey(DateUtils.getNow()), null);
        
        if (serializedDailyMood == null) {
            return null;
        }
        
        return mGson.fromJson(serializedDailyMood, DailyMood.class);
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
    
    // Insert 7 moods if database is empty
    public void injectMockData(Context context) {
        
        if (getSharedPreferences(context).getAll().isEmpty()) {
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
                                                .withMinute(random.nextInt(60))
                                                .toString());
                    
                    Log.v(TAG, "injectMockData() called, injecting mockedMood = [" + mockedMood + "]");
                    
                    upsertMood(context, mockedMood);
                }
            }
            
            Log.i(TAG, "injectMockData() called !");
            Toast.makeText(context, "injectMockData() called !", Toast.LENGTH_SHORT).show();
        }
    }
    
    // endregion
}
