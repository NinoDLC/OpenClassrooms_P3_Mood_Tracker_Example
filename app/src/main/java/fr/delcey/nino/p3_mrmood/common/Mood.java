package fr.delcey.nino.p3_mrmood.common;

import android.support.annotation.ColorRes;
import fr.delcey.nino.p3_mrmood.R;

/**
 * Store the different moods the user can feel
 */
public enum Mood {
    APOCALYSPE(R.color.apocalypse_mood, 0.2f),
    BAD(R.color.bad_mood, 0.4f),
    NEUTRAL(R.color.neutral_mood, 0.6f),
    GOOD(R.color.good_mood, 0.8f),
    AWESOME(R.color.awesome_mood, 1);
    
    @ColorRes
    private final int mColorRes;
    private final float mPercentage;
    
    Mood(@ColorRes int colorRes, float percentage) {
        mColorRes = colorRes;
        mPercentage = percentage;
    }
    
    @ColorRes
    public int getColorRes() {
        return mColorRes;
    }
    
    /**
     * The percentage of screen, horizontaly, that the History item view should take
     */
    public float getPercentage() {
        return mPercentage;
    }
}
