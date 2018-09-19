package fr.delcey.nino.p3_mrmood;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.jakewharton.threetenabp.AndroidThreeTen;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMood;
import io.realm.Realm;
import java.util.Random;

public class MyApp extends Application {
    
    private static final String TAG = MyApp.class.getSimpleName();
    
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
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        AndroidThreeTen.init(this);
        Realm.init(this);
        
        if (BuildConfig.DEBUG) {
            injectMockData();
        }
    }
    
    private void injectMockData() {
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
            Toast.makeText(this, "injectMockData() called !", Toast.LENGTH_SHORT).show();
            
            Realm.getDefaultInstance().commitTransaction();
        }
    }
}
