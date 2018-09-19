package fr.delcey.nino.p3_mrmood.common.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LastTimeUsedSharedPreferences {
    
    private static final String TAG = LastTimeUsedSharedPreferences.class.getSimpleName();
    
    private static final String SHARED_PREFERENCE_KEY = "SHARED_PREFERENCE_KEY_LAST_TIME_USED";
    private static final String KEY_LAST_TIME_USED = "KEY_LAST_TIME_USED";
    
    private final SharedPreferences mSharedPreferences;
    
    public LastTimeUsedSharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }
    
    public void setLastTimeUsed(int lastTimeUsed) {
        Log.i(TAG, "setLastTimeUsed() called with: " + "lastTimeUsed = [" + lastTimeUsed + "]");
        
        mSharedPreferences.edit().putInt(KEY_LAST_TIME_USED, lastTimeUsed).apply();
    }
    
    public int getLastTimeUsed() {
        return mSharedPreferences.getInt(KEY_LAST_TIME_USED, 0);
    }
}
