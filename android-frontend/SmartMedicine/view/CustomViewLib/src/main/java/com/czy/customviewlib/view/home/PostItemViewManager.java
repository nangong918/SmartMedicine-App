package com.czy.customviewlib.view.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.Optional;

public class PostItemViewManager {

    private interface Binding {
        void setUserFace(String url);
        void setPostImage(String url);
        void setTitle(String title);
        void setUserID(String userID);
    }

    private static void loadPostData(Binding binding, PostAo postAo, int index) {
        if (postAo.postVos == null || postAo.postVos.length <= index) {
            return;
        }
        PostVo vo = postAo.postVos[index];

        // avatarUrl
        binding.setUserFace(Optional.ofNullable(vo.authorAvatarUrl).orElse(""));

        // postUrl
        binding.setPostImage(Optional.ofNullable(vo.postImgUrls)
                .filter(urls -> !urls.isEmpty())
                .map(urls -> urls.get(0))
                .orElse(""));

        // title
        binding.setTitle(Optional.ofNullable(vo.postTitle).orElse(""));

        // userID (其实就是username)
        binding.setUserID(Optional.ofNullable(vo.authorName)
                .filter(name -> !TextUtils.isEmpty(name))
                .orElseGet(() -> Optional.ofNullable(vo.authorId)
                        .map(String::valueOf)
                        .orElse("")));
    }

    public static void setView(@NonNull ViewRecommendCardPlusBinding binding, @NonNull PostAo postAo) {
        loadPostData(new Binding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByLocalFile(url, binding.cardImage);
            }

            @Override
            public void setTitle(String title) {
                binding.textTitle.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.cardUserID.setText(userID);
            }
        }, postAo, 0);
    }

    public static void setView(@NonNull ViewRecommendCardBinding binding, @NonNull PostAo postAo) {
        loadPostData(new Binding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByLocalFile(url, binding.cardImage);
            }

            @Override
            public void setTitle(String title) {
                binding.textTitle.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.cardUserID.setText(userID);
            }
        }, postAo, 0);

        loadPostData(new Binding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByLocalFile(url, binding.cardImage);
            }

            @Override
            public void setTitle(String title) {
                binding.textTitle.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.cardUserID.setText(userID);
            }
        }, postAo, 1);
    }
}