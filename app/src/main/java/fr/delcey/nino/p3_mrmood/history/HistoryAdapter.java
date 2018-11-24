package fr.delcey.nino.p3_mrmood.history;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMood;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Provide Views that represent the mood felt during a day. Depending on the
 * {@link fr.delcey.nino.p3_mrmood.common.Mood}, the background color and the horizontal size of the item will change.
 * A button appears if a comment is available for that day.
 */
class HistoryAdapter extends Adapter<HistoryAdapter.HistoryViewHolder> {
    
    @NonNull
    private final HistoryBusinessService mHistoryBusinessService = new HistoryBusinessService();
    
    @NonNull
    private final List<DailyMood> mData;
    @NonNull
    private final Callback mCallback;
    
    HistoryAdapter(@NonNull List<DailyMood> moods, @NonNull Callback callback) {
        mData = moods;
        mCallback = callback;
    }
    
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext())
                                                   .inflate(R.layout.history_recyclerview_item, parent, false),
                                     this);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int position) {
        DailyMood mood = mData.get(position);
        viewHolder.bind(mood);
    }
    
    @Override
    public int getItemCount() {
        return mData.size();
    }
    
    private void onItemClicked(int adapterPosition) {
        DailyMood mood = mData.get(adapterPosition);
        mCallback.onHistoryMoodClicked(mood);
    }
    
    static class HistoryViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        
        private final WeakReference<HistoryAdapter> mAdapter;
        
        private final TextView textViewHoldLongAgo;
        private final ImageView imageViewIconCommentAvailable;
        private final View background;
        
        HistoryViewHolder(@NonNull View itemView, HistoryAdapter adapter) {
            super(itemView);
            
            mAdapter = new WeakReference<>(adapter);
            
            textViewHoldLongAgo = itemView.findViewById(R.id.history_item_tv_how_long_ago);
            imageViewIconCommentAvailable = itemView.findViewById(R.id.history_item_iv_comment_available);
            background = itemView.findViewById(R.id.history_item_view_background);
        }
        
        void bind(@NonNull DailyMood mood) {
            if (mAdapter.get() == null) {
                return;
            }
            String howLongAgo = mAdapter.get()
                                        .getHistoryBusinessService()
                                        .getHowLongAgo(itemView.getContext(), mood.getDate());
            textViewHoldLongAgo.setText(howLongAgo);
            
            imageViewIconCommentAvailable.setVisibility(mood.getComment() == null ? View.GONE : View.VISIBLE);
            imageViewIconCommentAvailable.setOnClickListener(this);
            
            background.setBackgroundResource(mood.getMood().getColorRes());
            ((LayoutParams) background.getLayoutParams()).matchConstraintPercentWidth = mood.getMood()
                                                                                            .getPercentage();
        }
        
        @Override
        public void onClick(View v) {
            if (mAdapter.get() != null) {
                mAdapter.get().onItemClicked(getAdapterPosition());
            }
        }
    }
    
    @NonNull
    private HistoryBusinessService getHistoryBusinessService() {
        return mHistoryBusinessService;
    }
    
    public interface Callback {
        
        void onHistoryMoodClicked(@NonNull DailyMood dailyMood);
    }
}
