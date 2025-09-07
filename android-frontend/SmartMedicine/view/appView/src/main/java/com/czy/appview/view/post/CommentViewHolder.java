package com.czy.appview.view.post;


import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewCommentItemBinding;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.domain.ao.entity.CommentAo;


public class CommentViewHolder extends RecyclerView.ViewHolder{

    public ViewCommentItemBinding binding;

    public CommentViewHolder(@NonNull ViewCommentItemBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void setView(CommentAo commentAo) {
        if (commentAo == null){
            return;
        }
        ImageLoadUtil.loadImageViewByUrl(commentAo.commentVo.avatarUrl, binding.imgvAvatar);
        if (commentAo.parentCommentId != null && !TextUtils.isEmpty(commentAo.commentVo.replyUserName)){
            String nameToName = commentAo.commentVo.userName + "->" + commentAo.commentVo.replyUserName;
            binding.tvName.setText(nameToName);
            binding.vMessage.setVisibility(View.GONE);
        }
        else {
            binding.tvName.setText(commentAo.commentVo.userName);
            binding.vMessage.setVisibility(View.VISIBLE);
        }
        binding.tvTime.setText(commentAo.commentVo.commentTime);
        binding.tvContent.setText(commentAo.commentVo.commentContent);
    }
}
