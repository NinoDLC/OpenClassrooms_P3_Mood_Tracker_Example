package fr.delcey.nino.p3_mrmood.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.Consts;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMood;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMoodDao;
import fr.delcey.nino.p3_mrmood.history.HistoryAdapter.Callback;

/**
 * Display the last 7 moods the user felt.
 */
public class HistoryActivity extends AppCompatActivity implements Callback {
    
    public static Intent navigate(Context context) {
        return new Intent(context, HistoryActivity.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_history);
        
        RecyclerView recyclerView = findViewById(R.id.history_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams layoutParams) {
                // force height of viewHolder here, this will override layout_height from xml
                layoutParams.height = getHeight() / Consts.MAXIMUM_NUMBER_OF_MOOD_DISPLAYED_IN_HISTORY;
                return true;
            }
        });
        recyclerView.setAdapter(new HistoryAdapter(DailyMoodDao.getInstance().getLastSevenMoods(this), this));
    }
    
    @Override
    public void onHistoryMoodClicked(@NonNull DailyMood dailyMood) {
        Toast.makeText(this, dailyMood.getComment(), Toast.LENGTH_LONG).show();
    }
}
