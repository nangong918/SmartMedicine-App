package com.czy.customviewlib.view.search.post;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewPostSearchBinding;
import com.czy.dal.constant.search.PostSearchResultListEnum;
import com.czy.dal.vo.entity.home.PostExVo;

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
        // userFace
        Optional.ofNullable(postExVo.authorAvatarUrl)
                .ifPresent(
                        url -> ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace)
                );

        // postImage
        Optional.ofNullable(postExVo.postImgUrls)
                .filter(urls -> !urls.isEmpty())
                .ifPresent(
                        urls -> ImageLoadUtil.loadImageViewByResource(urls.get(0), binding.cardImage)
                );

        // title
        String title = Optional.ofNullable(postExVo.postTitle).orElse("");
        binding.textTitle.setText(title);

        // userID (其实就是username)
        String userID = Optional.ofNullable(postExVo.authorName).orElse("");
        binding.cardUserID.setText(userID);

        // type
        String type = PostSearchResultListEnum.getEnum(postExVo.type).getName();
        binding.tvType.setText(type);
    }

    private Long postId;

    private void setData(@NonNull PostExVo postExVo){
        this.postId = postExVo.postId;
    }

    private void setClick(@NonNull OnPostClick onPostClick){
        binding.basicCard.setOnClickListener(v -> {
            onPostClick.onPostClick(getAdapterPosition(), postId);
        });
    }
}
