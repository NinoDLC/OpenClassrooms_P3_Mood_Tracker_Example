package fr.delcey.nino.p3_mrmood.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.Mood;

/**
 * Provide itemviews that match parent's size with full coloration, depending of the {@link Mood}.
 */
public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {
    
    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoodViewHolder(LayoutInflater.from(parent.getContext())
                                                .inflate(R.layout.main_recyclerview_item, parent, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        holder.bind(Mood.values()[position]);
    }
    
    @Override
    public int getItemCount() {
        return Mood.values().length;
    }
    
    static class MoodViewHolder extends RecyclerView.ViewHolder {
        
        private final View mBackground;
        
        MoodViewHolder(View itemView) {
            super(itemView);
            
            mBackground = itemView.findViewById(R.id.main_item_fl);
        }
        
        void bind(@NonNull Mood mood) {
            mBackground.setBackgroundResource(mood.getColorRes());
        }
    }
}
