package com.czy.customviewlib.view.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.constant.home.RecommendButtonType;
import com.czy.dal.constant.home.RecommendCardType;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.Optional;

public class PostItemViewManager {

    private interface ViewBinding {
        void setUserFace(String url);
        void setPostImage(String url);
        void setTitle(String title);
        void setUserID(String userID);
    }

    private interface ClickBinding {
        void setBasicCardClick(OnRecommendCardClick onClick, int currentPosition);
        void setFavoriteClick(OnRecommendCardClick onClick, int currentPosition);
        void setStarClick(OnRecommendCardClick onClick, int currentPosition);
        void setUnlikeClick(OnRecommendCardClick onClick, int currentPosition);
    }

    private static void loadPostData(ViewBinding viewBinding, PostAo postAo, int index) {
        if (postAo.postVos == null || postAo.postVos.length <= index) {
            return;
        }
        PostVo vo = postAo.postVos[index];

        // avatarUrl
        viewBinding.setUserFace(Optional.ofNullable(vo.authorAvatarUrl).orElse(""));

        // postUrl
        viewBinding.setPostImage(Optional.ofNullable(vo.postImgUrls)
                .filter(urls -> !urls.isEmpty())
                .map(urls -> urls.get(0))
                .orElse(""));

        // title
        viewBinding.setTitle(Optional.ofNullable(vo.postTitle).orElse(""));

        // userID (其实就是username)
        viewBinding.setUserID(Optional.ofNullable(vo.authorName)
                .filter(name -> !TextUtils.isEmpty(name))
                .orElseGet(() -> Optional.ofNullable(vo.authorId)
                        .map(String::valueOf)
                        .orElse("")));
    }

    public static void setView(@NonNull ViewRecommendCardPlusBinding binding, @NonNull PostAo postAo) {
        loadPostData(new ViewBinding() {
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
        loadPostData(new ViewBinding() {
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

        loadPostData(new ViewBinding() {
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

    private static void setCommonClickListeners(ClickBinding clickBinding, OnRecommendCardClick onClick, int currentPosition) {
        clickBinding.setBasicCardClick(onClick, currentPosition);
        clickBinding.setFavoriteClick(onClick, currentPosition);
        clickBinding.setStarClick(onClick, currentPosition);
        clickBinding.setUnlikeClick(onClick, currentPosition);
    }

    public static void setClick(@NonNull ViewRecommendCardPlusBinding binding,
                                @NonNull OnRecommendCardClick onClick,
                                int currentPosition) {
        setCommonClickListeners(new ClickBinding() {
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.basicCard.setOnClickListener(v ->
                        onClick.onCardClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.favorite.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.star.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.unlike.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, currentPosition);
    }

    public static void setClick(@NonNull ViewRecommendCardBinding binding,
                                @NonNull OnRecommendCardClick onClick,
                                int currentPosition) {
        setCommonClickListeners(new ClickBinding() {
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.basicCard.setOnClickListener(v ->
                        onClick.onCardClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.favorite.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.star.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, int currentPosition) {
                binding.unlike.setOnClickListener(v ->
                        onClick.onButtonClick(currentPosition, RecommendCardType.SINGLE_BIG_CARD, 0, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, currentPosition);
    }
}