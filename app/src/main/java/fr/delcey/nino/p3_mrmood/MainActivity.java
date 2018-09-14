package fr.delcey.nino.p3_mrmood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.ViewTreeObserver;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private int mRecyclerviewHeight;
    private int mScrolledY;
    private int mMoodCount = (Mood.values().length - 1); // Optimization : calculated only once

    private LottieAnimationView mLottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLottieAnimationView = findViewById(R.id.main_lottie_animation_view);
        mLottieAnimationView.setImageAssetsFolder("images/");

        final RecyclerView recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new MoodAdapter());

        // Initialize recyclerview to "neutral" mood
        recyclerView.scrollToPosition(mMoodCount / 2);

        // Link Lottie animation with recyclerview scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mScrolledY += dy;

                if (mRecyclerviewHeight != 0) {
                    updateAnimation();
                }
            }
        });

        // Since ViewHolders have MATCH_PARENT params, ViewHolder height == Recyclerview height
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mRecyclerviewHeight = recyclerView.getHeight();

                // Recyclerview is initialized with "neutral mood"
                mScrolledY = mRecyclerviewHeight * (mMoodCount / 2);

                // Initialize animation to current recyclerview item
                updateAnimation();
            }
        });
    }

    private void updateAnimation() {
        mLottieAnimationView.setProgress(((float) mScrolledY / mRecyclerviewHeight) / mMoodCount);
    }
}
