package fr.delcey.nino.p3_mrmood.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import fr.delcey.nino.p3_mrmood.R;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMood;
import fr.delcey.nino.p3_mrmood.common.dao.DailyMoodDao;

/**
 * The user can add a comment about their day with this dialog. Display any previous comment provided during the day.
 */
public class MoodCommentDialog extends DialogFragment {
    
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getActivity() != null;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_comment, null);
        final EditText editText = view.findViewById(R.id.dialog_comment_et);
        
        DailyMood currentMood = DailyMoodDao.getInstance().getCurrentMood(getContext());
        
        if (currentMood != null) {
            editText.setText(currentMood.getComment());
        }
        
        builder.setView(view)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       DailyMoodDao.getInstance()
                                   .upsertTodayComment(editText.getContext(), editText.getText().toString());
                   }
               })
               .setNegativeButton(android.R.string.cancel, null);
        
        return builder.create();
    }
    
}