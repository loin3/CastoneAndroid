package com.example.capstoneandroid.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneandroid.R;
import com.example.capstoneandroid.domain.Comment;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>{
    private Comment[] dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView commentContent;
        public final TextView commentCreatedTime;
        public final ImageButton commentLikeButton;
        public final TextView commentLikes;
        public ViewHolder(View v){
            super(v);
            commentContent = v.findViewById(R.id.comment_content);
            commentCreatedTime = v.findViewById(R.id.comment_created_time);
            commentLikeButton = v.findViewById(R.id.comment_like_button);
            commentLikes = v.findViewById(R.id.comment_likes);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.commentContent.setText(dataSet[position].content);
        holder.commentCreatedTime.setText(dataSet[position].createdTime+"");
        holder.commentLikes.setText(dataSet[position].likes+"");
    }

    @Override
    public int getItemCount() {
        if(dataSet != null){
            return dataSet.length;
        }else{
            return 0;
        }
    }

    public void setDataSet(Comment[] comments){this.dataSet = comments;}
}
