package fr.delcey.nino.p3_mrmood.common.dao;

import android.support.annotation.NonNull;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import javax.annotation.Nullable;
import org.threeten.bp.ZonedDateTime;

/**
 * POJO to store the mood the user felt on a specific day ; the comment is optional
 */
public class DailyMood extends RealmObject {
    
    static final transient String NAME_KEY = "key";
    
    // Key is the day in the form "YYYYMMDD", since only one mood should be stored per day !
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @PrimaryKey
    private int key;
    
    private String mood;
    private String date;
    private String comment;
    
    @NonNull
    public Mood getMood() {
        return Mood.valueOf(mood);
    }
    
    public void setMood(@NonNull Mood mood) {
        this.mood = mood.name();
    }
    
    @NonNull
    public ZonedDateTime getDate() {
        return ZonedDateTime.parse(date);
    }
    
    public void setDate(@NonNull ZonedDateTime zonedDateTime) {
        key = DateUtils.getDateAsNumber(zonedDateTime);
        date = zonedDateTime.toString();
    }
    
    @Nullable
    public String getComment() {
        return comment;
    }
    
    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }
    
    @Override
    public String toString() {
        return "DailyMood{" +
            "key=" + key +
            ", mood='" + mood + '\'' +
            ", date='" + date + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }
}
