package com.czy.appview.view.post;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewCommentItemBinding;
import com.czy.domain.ao.entity.CommentAo;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final List<CommentAo> commentAos;

    public CommentAdapter(@NonNull List<CommentAo> commentAos){
        this.commentAos = commentAos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewCommentItemBinding binding = ViewCommentItemBinding.inflate(inflater, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentAo commentVo = commentAos.get(position);
        ((CommentViewHolder)holder).setView(commentVo);
    }

    @Override
    public int getItemCount() {
        return commentAos.size();
    }
}
