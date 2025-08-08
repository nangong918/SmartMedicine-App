package com.czy.customviewlib.view.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.constant.home.RecommendCardType;

import java.util.List;
import java.util.Optional;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = PostAdapter.class.getName();

    private final List<PostAo> postAoList;
    private final OnRecommendCardClick onRecommendCardClick;

    public PostAdapter(List<PostAo> postAoList,
                       OnRecommendCardClick onRecommendCardClick) {
        this.postAoList = postAoList;
        this.onRecommendCardClick = onRecommendCardClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecommendCardType recommendCardType = RecommendCardType.valueOf(viewType);
        switch (recommendCardType){
            // 大的卡片
            case SINGLE_BIG_CARD -> {
                ViewRecommendCardPlusBinding binding = ViewRecommendCardPlusBinding.inflate(inflater, parent, false);
                return new PostItemPlusViewHolder(binding, onRecommendCardClick);
            }
            // 两个小卡片
            case TWO_SMALL_CARD -> {
                ViewRecommendCardBinding binding = ViewRecommendCardBinding.inflate(inflater, parent, false);
                return new PostItemViewHolder(binding, onRecommendCardClick);
            }
            default -> {
                Log.w(TAG, "未知的推荐卡片类型");
                throw new IllegalArgumentException("不支持的推荐卡片类型");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostAo postAo = postAoList.get(position);
        if (postAo == null){
            return;
        }
        RecommendCardType recommendCardType = RecommendCardType.valueOf(postAo.viewType);
        switch (recommendCardType){
            case SINGLE_BIG_CARD -> ((PostItemPlusViewHolder)holder).setView();
            case TWO_SMALL_CARD -> ((PostItemViewHolder)holder).setView();
            default -> Log.w(TAG, "未知的推荐卡片类型");
        }
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(postAoList)
                .map(List::size)
                .orElse(0);
    }
}
