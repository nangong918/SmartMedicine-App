package com.czy.customviewlib.view.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
                ImageLoadUtil.loadImageViewByResource(url, binding.cardImage);
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

        // card 1
        loadPostData(new ViewBinding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardImage);
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

        // card 2
        loadPostData(new ViewBinding() {
            @Override
            public void setUserFace(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardUserFace2);
            }

            @Override
            public void setPostImage(String url) {
                ImageLoadUtil.loadImageViewByResource(url, binding.cardImage2);
            }

            @Override
            public void setTitle(String title) {
                binding.textTitle2.setText(title);
            }

            @Override
            public void setUserID(String userID) {
                binding.cardUserID2.setText(userID);
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
                binding.basicCard.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.favorite.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.star.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.unlike.setOnClickListener(v ->
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
                binding.basicCard.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.favorite.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.star.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.unlike.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, viewHolder);

        // card 2
        setCommonClickListeners(new ClickBinding() {
            final int cardId = 1;
            @Override
            public void setBasicCardClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.basicCard.setOnClickListener(v ->
                        onClick.onCardClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId)
                );
            }

            @Override
            public void setFavoriteClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.favorite.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.LIKE)
                );
            }

            @Override
            public void setStarClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.star.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.COLLECT)
                );
            }

            @Override
            public void setUnlikeClick(OnRecommendCardClick onClick, RecyclerView.ViewHolder viewHolder) {
                binding.unlike.setOnClickListener(v ->
                        onClick.onButtonClick(viewHolder.getAdapterPosition(), RecommendCardType.SINGLE_BIG_CARD, cardId, RecommendButtonType.DISLIKE)
                );
            }
        }, onClick, viewHolder);
    }
}