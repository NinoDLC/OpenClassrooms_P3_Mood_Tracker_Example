package fr.delcey.nino.p3_mrmood.common.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import org.threeten.bp.ZonedDateTime;

/**
 * POJO to store the mood the user felt on a specific day ; the comment is optional
 */
public class DailyMood {
    
    @NonNull
    private String mood;
    
    @NonNull
    private String date;
    
    @Nullable
    private String comment;
    
    DailyMood() {
        mood = Mood.NEUTRAL.name();
        date = DateUtils.getNow().toString();
    }
    
    @NonNull
    public Mood getMood() {
        return Mood.valueOf(mood);
    }
    
    void setMood(@NonNull Mood mood) {
        this.mood = mood.name();
    }
    
    @NonNull
    public ZonedDateTime getDate() {
        return ZonedDateTime.parse(date);
    }
    
    public void setDate(@NonNull String date) {
        this.date = date;
    }
    
    @Nullable
    public String getComment() {
        return comment;
    }
    
    void setComment(@Nullable String comment) {
        this.comment = comment;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "DailyMood{" +
            ", mood='" + mood + '\'' +
            ", date='" + date + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }
}
