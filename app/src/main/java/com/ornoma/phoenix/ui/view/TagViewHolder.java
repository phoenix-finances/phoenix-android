package com.ornoma.phoenix.ui.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.ui.activities.TagsActivity;
/**
 * Created by de76 on 5/27/17.
 */

public class TagViewHolder extends RecyclerView.ViewHolder {
    public final void update(final TransactionTag transactionTag) {
        final TextView textViewName = (TextView) itemView.findViewById(R.id.textView_name);
        textViewName.setText(transactionTag.getName());
        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                onTagSelected(transactionTag, it.getContext());
            }
        }));
    }

    private final void onTagSelected(TransactionTag transactionTag, Context context) {
        if (context instanceof TagsActivity) {
            TagsActivity activity = (TagsActivity) context;
            if (activity.isInSelectionMode) {
                Intent intent = new Intent();
                intent.putExtra("_key_tag_id", transactionTag.getUid());
                activity.setResult(-1, intent);
                activity.finish();
            }

        }
    }

    public TagViewHolder(View itemView) {
        super(itemView);
    }
}