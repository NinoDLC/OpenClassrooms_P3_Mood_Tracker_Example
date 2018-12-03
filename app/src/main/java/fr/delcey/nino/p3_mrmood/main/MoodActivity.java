package fr.delcey.nino.p3_mrmood.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import com.airbnb.lottie.LottieAnimationView;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.Consts;
import fr.delcey.nino.p3_mrmood.common.DateUtils;
import fr.delcey.nino.p3_mrmood.common.Mood;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMood;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMoodDao;
import fr.delcey.nino.p3_mrmood.common.shared_preferences.LastTimeUsedSharedPreferences;
import fr.delcey.nino.p3_mrmood.history.HistoryActivity;

/**
 * Base activity of the application ; displays a smiley (animated thanks to Lottie) and a background color. The
 * background color is a RecyclerView whose items are matching its height and width.
 * <br/>
 * The animation and the background color / number of moods are dependant of the {@link Mood} enum.
 * <br/>
 * This application doesn't support switching TimeZones. Beware, travelers !
 */
public class MoodActivity extends AppCompatActivity {
    
    // Key used in the onSaveInstanceState bundle to know which mood was selected
    private static final String SAVE_STATE_SELECTED_MOOD = "SAVE_STATE_SELECTED_MOOD";
    
    private RecyclerView mRecycleview;
    private LottieAnimationView mLottieAnimationView;
    
    private int mRecyclerviewHeight;
    private int mScrolledY;
    
    private final int mMoodCount = Mood.values().length - 1; // Optimization : calculated only once
    
    private int mInitialPosition;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        // Smiley animation
        mLottieAnimationView = findViewById(R.id.main_lottie_animation_view);
        mLottieAnimationView.setImageAssetsFolder(Consts.LOTTIE_IMAGE_FOLDER_PATH);
        
        // "Add comment" button
        ImageButton addCommentButton = findViewById(R.id.main_ibtn_add_comment);
        addCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddCommentButtonClicked();
            }
        });
        
        // "History" button
        ImageButton historyButton = findViewById(R.id.main_ibtn_history);
        historyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(HistoryActivity.navigate(MoodActivity.this));
            }
        });
        
        // "Share mood" button
        ImageButton shareMoodButton = findViewById(R.id.main_ibtn_share_mood);
        shareMoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DailyMood currentMood = DailyMoodDao.getInstance().getCurrentMood();
                if (currentMood != null && currentMood.getComment() != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, currentMood.getComment());
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, getString(R.string.share_mood)));
                }
            }
        });
        
        // Colored & scrolling background
        mRecycleview = findViewById(R.id.main_rv);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecycleview);
        mRecycleview.setAdapter(new MoodAdapter());
        
        final int initialPosition;
        
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_STATE_SELECTED_MOOD)) {
            // Activity is being recreated from a previous state : restore it
            initialPosition = savedInstanceState.getInt(SAVE_STATE_SELECTED_MOOD);
        } else {
            DailyMood currentMood = DailyMoodDao.getInstance().getCurrentMood();
            
            if (currentMood == null) {
                // Just initialize recyclerview to "neutral" mood the first time the activity is launched
                initialPosition = getRecyclerViewDefaultInitialPosition();
            } else {
                initialPosition = currentMood.getMood().ordinal();
            }
        }
        
        addScrollListenerToRecyclerView();
        
        initRecyclerViewPosition(initialPosition);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        int lastTimeUsed = new LastTimeUsedSharedPreferences(this).getLastTimeUsed();
        
        if (DateUtils.getDateAsNumber(DateUtils.getNow()) != lastTimeUsed) {
            initRecyclerViewPosition(getRecyclerViewDefaultInitialPosition());
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        new LastTimeUsedSharedPreferences(this)
            .setLastTimeUsed(DateUtils.getDateAsNumber(DateUtils.getNow()));
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mRecycleview.getLayoutManager() != null) {
            int actualPosition =
                ((LinearLayoutManager) mRecycleview.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            
            if (actualPosition == RecyclerView.NO_POSITION) {
                // User is actually scrolling and activity is saving... unusual but OK.
                actualPosition = ((LinearLayoutManager) mRecycleview.getLayoutManager()).findFirstVisibleItemPosition();
            }
            
            if (actualPosition != RecyclerView.NO_POSITION) {
                outState.putInt(SAVE_STATE_SELECTED_MOOD, actualPosition);
            }
        }
    }
    
    private void onAddCommentButtonClicked() {
        new MoodCommentDialog().show(getSupportFragmentManager(), null);
    }
    
    private void addScrollListenerToRecyclerView() {
        // Bind Lottie animation with recyclerview scroll
        mRecycleview.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // RecyclerView is no longer scrolling
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    saveCurrentMood();
                }
            }
            
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                mScrolledY += dy;
                
                if (mRecyclerviewHeight != 0) {
                    updateAnimation();
                }
            }
        });
    }
    
    private void saveCurrentMood() {
        // SnapHelper makes some weird bug with imprecise [-1 or +1] dy with onScrolled method, so wait for the
        // RecyclerView to be truely stabilized to save the mood
        if (mScrolledY % mRecyclerviewHeight == 0) {
            int currentMoodIndex = mScrolledY / mRecyclerviewHeight;
            Mood currentMood = Mood.values()[currentMoodIndex];
            
            DailyMoodDao.getInstance().setTodayMood(currentMood);
        }
    }
    
    private int getRecyclerViewDefaultInitialPosition() {
        return (mMoodCount + 1) / 2;
    }
    
    private void initRecyclerViewPosition(int initialPosition) {
        // Restore the good color of the RecyclerView
        setRecyclerViewInitialPosition(initialPosition);
        
        computeRecyclerviewHeightAndInitialScroll();
    }
    
    private void setRecyclerViewInitialPosition(int initialPosition) {
        mInitialPosition = initialPosition;
        
        mRecycleview.scrollToPosition(initialPosition);
    }
    
    private void computeRecyclerviewHeightAndInitialScroll() {
        // Since ViewHolders have MATCH_PARENT params, ViewHolder height == Recyclerview height
        mRecycleview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            // onGlobalLayout event is trigger the first time the View is actually fully measured and laid out. This
            // is the perfect moment to get its dimension.
            @Override
            public void onGlobalLayout() {
                // Avoid unecessary calls
                mRecycleview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                
                mRecyclerviewHeight = mRecycleview.getHeight();
                
                updateInitialScrollY(mInitialPosition);
            }
        });
    }
    
    private void updateInitialScrollY(int initialPosition) {
        // OnScrollListener.onScrolled is not called when we use scrollToPosition so have to initialize the
        // mScrolledY value by ourselves
        mScrolledY = mRecyclerviewHeight * initialPosition;
        
        // Initialize animation to current recyclerview item
        updateAnimation();
    }
    
    private void updateAnimation() {
        mLottieAnimationView.setProgress(((float) mScrolledY / mRecyclerviewHeight) / mMoodCount);
    }
}
