package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.cache.TagCache;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.ui.view.TagViewHolder;

/**
 * Created by de76 on 5/27/17.
 */

public final class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {
    private final LayoutInflater layoutInflater;
    private final TagCache tagCache;
    private Context context;
    private final int[] idArray;



    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = this.layoutInflater.inflate(R.layout.layout_tag, null);
        return new TagViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        int id = this.idArray[position];
        TransactionTag tag = this.tagCache.getTag(id);
        holder.update(tag);
    }

    public int getItemCount() {
        return this.idArray.length;
    }

    public TagAdapter(Context context, int[] idArray) {
        this.context = context;
        this.idArray = idArray;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.tagCache = TagCache.getInstance(this.context);
    }
}

