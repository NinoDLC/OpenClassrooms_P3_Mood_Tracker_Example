package fr.delcey.nino.p3_mrmood;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMoodDao;
import io.realm.Realm;

public class MyApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        AndroidThreeTen.init(this);
        Realm.init(this);
        
        if (BuildConfig.DEBUG) {
            DailyMoodDao.getInstance().injectMockData(this);
        }
    }
}
