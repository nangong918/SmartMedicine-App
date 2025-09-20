package com.czy.appview.view.search.post;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewPostSearchBinding;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.domain.constant.search.PostSearchResultListEnum;
import com.czy.domain.vo.entity.home.PostPreviewExVo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PostSearchViewHolder extends RecyclerView.ViewHolder {

    private final ViewPostSearchBinding binding;

    public PostSearchViewHolder(@NonNull ViewPostSearchBinding binding,
                                @NonNull OnPostClick onPostClick
    ) {
        super(binding.getRoot());
        this.binding = binding;
        setClick(onPostClick);
    }

    public void setAo(@NonNull PostPreviewExVo postPreviewExVo){
        setView(postPreviewExVo);
        setData(postPreviewExVo);
    }

    @SuppressLint("SimpleDateFormat")
    private void setView(@NonNull PostPreviewExVo postPreviewExVo){
        // author
        Optional.ofNullable(postPreviewExVo.postPreviewVo)
                .ifPresent(vo -> {
                    // author
                    ImageLoadUtil.loadImageViewByResource(vo.authorAvatarUrl, binding.imgvAvatar);
                    binding.tvName.setText(vo.authorName);

                    // post
                    binding.tvTitle.setText(vo.postTitle);
                    String postPublishTime = vo.postPublishTime;
                    try {
                        // yyyy-MM-dd HH:mm:ss
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDateTime changeTime = LocalDateTime.parse(postPublishTime, inputFormatter);
                        postPublishTime = changeTime.format(outputFormatter);
                    } catch (Exception e){
                        Log.e(PostSearchViewHolder.class.getName(), "setView, change time error: " + postPublishTime, e);
                    }
                    binding.tvTime.setText(postPublishTime);
                    binding.tvView.setText(vo.postViewNum);
                    binding.tvLike.setText(vo.likeNum);
                    // postImage
                    ImageLoadUtil.loadImageViewByResource(vo.postImgUrl0, binding.imgvMain);
                });

        // type
        String type = PostSearchResultListEnum.getEnum(postPreviewExVo.type).getName();
        binding.tvType.setText(type);
    }

    private Long postId;

    private void setData(@NonNull PostPreviewExVo postPreviewExVo){
        this.postId = Optional.ofNullable(postPreviewExVo.postPreviewVo)
                .map(vo -> vo.postId)
                .orElse(null);
    }

    private void setClick(@NonNull OnPostClick onPostClick){
        binding.baseCard.setOnClickListener(v -> {
            onPostClick.onPostClick(getAdapterPosition(), postId);
        });
    }
}
