package com.czy.appview.view.search.post;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.appview.databinding.ViewPostSearchBinding;
import com.czy.domain.constant.search.PostSearchResultListEnum;
import com.czy.domain.vo.entity.home.PostExVo;

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

    public void setAo(@NonNull PostExVo postExVo){
        setView(postExVo);
        setData(postExVo);
    }

    private void setView(@NonNull PostExVo postExVo){
        // author
        Optional.ofNullable(postExVo.postVo)
                .ifPresent(vo -> {
                    // author
                    ImageLoadUtil.loadImageViewByResource(vo.authorAvatarUrl, binding.imgvAvatar);
                    binding.tvName.setText(vo.authorName);

                    // post
                    binding.tvTitle.setText(vo.postTitle);
                    binding.tvTime.setText(vo.postPublishTime);
                    binding.tvView.setText(vo.postViewNum);
                    binding.tvLike.setText(vo.likeNum);
                    // postImage
                    Optional.ofNullable(vo.postImgUrls)
                            .filter(urls -> !urls.isEmpty())
                            .map(urls -> urls.get(0))
                            .ifPresent(url -> {
                                ImageLoadUtil.loadImageViewByResource(url, binding.imgvMain);
                            });
                });

        // type
        String type = PostSearchResultListEnum.getEnum(postExVo.type).getName();
        binding.tvType.setText(type);
    }

    private Long postId;

    private void setData(@NonNull PostExVo postExVo){
        this.postId = Optional.ofNullable(postExVo.postVo)
                .map(vo -> vo.postId)
                .orElse(null);
    }

    private void setClick(@NonNull OnPostClick onPostClick){
        binding.baseCard.setOnClickListener(v -> {
            onPostClick.onPostClick(getAdapterPosition(), postId);
        });
    }
}
