package com.czy.customviewlib.view.search.post;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewPostSearchBinding;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.Optional;

public class PostSearchViewHolder extends RecyclerView.ViewHolder {

    private final ViewPostSearchBinding binding;

    public PostSearchViewHolder(@NonNull ViewPostSearchBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void setView(@NonNull PostVo postVo){
        // userFace
        Optional.ofNullable(postVo.authorAvatarUrl)
                .ifPresent(
                        url -> ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace)
                );

        // postImage
        Optional.ofNullable(postVo.postImgUrls)
                .filter(urls -> !urls.isEmpty())
                .ifPresent(
                        urls -> ImageLoadUtil.loadImageViewByResource(urls.get(0), binding.cardImage)
                );

        // title
        String title = Optional.ofNullable(postVo.postTitle).orElse("");
        binding.textTitle.setText(title);

        // userID (其实就是username)
        String userID = Optional.ofNullable(postVo.authorName).orElse("");
        binding.cardUserID.setText(userID);
    }
}
