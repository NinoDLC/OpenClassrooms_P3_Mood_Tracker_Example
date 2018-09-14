package fr.delcey.nino.p3_mrmood;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoodViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        holder.bind(Mood.values()[position]);
    }

    @Override
    public int getItemCount() {
        return Mood.values().length;
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {
        View mBackground;

        MoodViewHolder(View itemView) {
            super(itemView);

            mBackground = itemView.findViewById(R.id.main_item_fl);
        }

        void bind(Mood mood) {
            mBackground.setBackgroundColor(mBackground.getResources().getColor(mood.getColorRes()));
        }
    }
}
