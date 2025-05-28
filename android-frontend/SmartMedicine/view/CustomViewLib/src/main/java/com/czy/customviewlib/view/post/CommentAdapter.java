package com.czy.customviewlib.view.post;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewCommentBinding;
import com.czy.dal.vo.entity.home.CommentVo;

import java.util.List;
import java.util.Optional;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<CommentVo> commentVos;

    public CommentAdapter(List<CommentVo> commentVos){
        this.commentVos = commentVos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewCommentBinding binding = ViewCommentBinding.inflate(inflater, parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentVo commentVo = commentVos.get(position);
        ((CommentViewHolder)holder).setView(commentVo);
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(this.commentVos)
                .map(List::size)
                .orElse(0);
    }
}
