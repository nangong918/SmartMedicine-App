package com.czy.appview.view.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.appview.databinding.ViewRecommendCardBinding;
import com.czy.appview.databinding.ViewRecommendCardPlusBinding;
import com.czy.domain.ao.home.PostAo;
import com.czy.domain.constant.home.RecommendButtonType;
import com.czy.domain.constant.home.RecommendCardType;
import com.czy.domain.vo.entity.home.PostVo;

import java.util.Optional;

public class PostHomeItemViewManager {

    private interface ViewBinding {
        void setUserFace(String url);
        void setPostImage(String url);
        void setTitle(String title);
        void setUserID(String userID);
    }

    // currentPosition -> RecyclerView.ViewHolder; 因为创建时候的getAdapterPosition()是死值，需要动态的从ViewHolder获取
    private interface ClickBinding {
        void setBasicCardClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder);
        void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder);
        void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder);
        void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder);
    }

    private static void loadPostData(ViewBinding viewBinding, PostAo postAo, int index) {
        if (postAo.postVos == null || postAo.postVos.length <= index) {
            return;
        }
        PostVo vo = postAo.postVos[index];

        // avatarUrl
        String avatarUrl = Optional.ofNullable(vo.authorAvatarUrl).orElse("");
        viewBinding.setUserFace(avatarUrl);

        // postUrl
        String postUrl = Optional.ofNullable(vo.postImgUrls)
                .filter(urls -> !urls.isEmpty())
                .map(urls -> urls.get(0))
                .orElse("");
        viewBinding.setPostImage(postUrl);

        // title
        viewBinding.setTitle(Optional.ofNullable(vo.postTitle).orElse(""));

        // userID (其实就是username)
        viewBinding.setUserID(Optional.ofNullable(vo.authorName)
                .filter(name -> !TextUtils.isEmpty(name))
                .orElse(""));
    }

    public static void setView(@NonNull ViewRecommendCardPlusBinding binding, @NonNull PostAo postAo) {
        loadPostData(new ViewBinding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvAvatar);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvMain);
            }

            @Override
            public void setTitle(String title) {
                binding.tvTitle.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.tvName.setText(userID);
            }
        }, postAo, 0);
    }

    public static void setView(@NonNull ViewRecommendCardBinding binding, @NonNull PostAo postAo) {

        // card 1
        loadPostData(new ViewBinding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvAvatar1);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvMain1);
            }

            @Override
            public void setTitle(String title) {
                binding.tvTitle1.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.tvName1.setText(userID);
            }
        }, postAo, 0);

        // card 2
        loadPostData(new ViewBinding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvAvatar2);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvMain2);
            }

            @Override
            public void setTitle(String title) {
                binding.tvTitle2.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.tvName2.setText(userID);
            }
        }, postAo, 1);
    }

    private static void setCommonClickListeners(ClickBinding clickBinding, OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
        clickBinding.setBasicCardClick(onClick, viewHolder);
        clickBinding.setFavoriteClick(onClick, viewHolder);
        clickBinding.setStarClick(onClick, viewHolder);
        clickBinding.setUnlikeClick(onClick, viewHolder);
    }

    public static void setClick(@NonNull ViewRecommendCardPlusBinding binding,
                                @NonNull OnRecommendCardClick onClick,
                                RecyclerView.ViewHolder viewHolder) {
        setCommonClickListeners(new ClickBinding() {
            final int cardId = 0;
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.baseCard.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnLike.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnCollect.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnUnlike.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, viewHolder);
    }

    public static void setClick(@NonNull ViewRecommendCardBinding binding,
                                @NonNull OnRecommendCardClick onClick,
                                RecyclerView.ViewHolder viewHolder) {
        // card 1
        setCommonClickListeners(new ClickBinding() {
            final int cardId = 0;
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.baseCard1.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnLike1.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnCollect1.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnUnlike1.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, viewHolder);

        // card 2
        setCommonClickListeners(new ClickBinding() {
            final int cardId = 1;
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.baseCard2.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnLike2.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnCollect2.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.btnUnlike2.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, viewHolder);
    }
}