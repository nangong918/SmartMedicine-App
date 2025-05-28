package com.czy.customviewlib.view.post;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.date.DateUtils;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewCommentBinding;
import com.czy.dal.vo.entity.home.CommentVo;


public class CommentViewHolder extends RecyclerView.ViewHolder{

    public ViewCommentBinding binding;

    public CommentViewHolder(@NonNull ViewCommentBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void setView(CommentVo commentVo) {
        ImageLoadUtil.loadImageViewByUrl(commentVo.commentAvatarUrl, binding.imvgAvatar);
        if (commentVo.replyCommentId != null){
            String nameToName = commentVo.commentName + "->" + commentVo.replyUserName;
            binding.tvNameToName.setText(nameToName);
        }
        else {
            binding.tvNameToName.setText(commentVo.commentName);
        }
        binding.tvCommend.setText(commentVo.content);
        binding.tvTime.setText(DateUtils.timestampToDate(commentVo.commentTimestamp));
    }
}
