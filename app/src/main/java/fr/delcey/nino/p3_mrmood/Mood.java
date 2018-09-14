package fr.delcey.nino.p3_mrmood;

import android.support.annotation.ColorRes;

public enum Mood {
    APOCALYSPE(R.color.apocalypse_mood),
    BAD(R.color.bad_mood),
    NEUTRAL(R.color.neutral_mood),
    GOOD(R.color.good_mood),
    AWESOME(R.color.awesome_mood);

    @ColorRes
    private int mColorRes;

    Mood(@ColorRes int colorRes) {
        mColorRes = colorRes;
    }

    @ColorRes
    public int getColorRes() {
        return mColorRes;
    }
}
