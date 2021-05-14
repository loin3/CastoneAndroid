package com.example.capstoneandroid.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneandroid.R;
import com.example.capstoneandroid.domain.Post;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>{
    private Post[] dataSet;
    private OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView listItemTitle;
        public final TextView createdTime;
        public final TextView postLikes;
        public final OnItemClickListener onItemClickListener;
        public ViewHolder(View v, OnItemClickListener onItemClickListener) {
            super(v);
            listItemTitle = v.findViewById(R.id.listItemTitle);
            createdTime = v.findViewById(R.id.postCreatedTime);
            postLikes = v.findViewById(R.id.postLikesTextView);

            this.onItemClickListener = onItemClickListener;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });
        }

    }

    //----------------------------------------
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    //----------------------------------------
    public PostRecyclerViewAdapter(LiveData<Post[]> dataSet, OnItemClickListener itemClickListener){
        this.dataSet = dataSet.getValue();
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_row_item, parent, false);
        return new ViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post currentPost = dataSet[position];
        holder.listItemTitle.setText(currentPost.title);
        holder.createdTime.setText(currentPost.content);
        holder.postLikes.setText(currentPost.likes + "");
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}
