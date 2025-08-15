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

public class PostHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = PostHomeAdapter.class.getName();

    private final List<PostAo> postAoList;
    private final OnRecommendCardClick onRecommendCardClick;

    public PostHomeAdapter(List<PostAo> postAoList,
                           OnRecommendCardClick onRecommendCardClick) {
        this.postAoList = postAoList;
        this.onRecommendCardClick = onRecommendCardClick;
    }

    /**
     * 给出不同的viewType
     * @param position position to query
     * @return  不同的viewType
     */
    @Override
    public int getItemViewType(int position) {
        PostAo postAo = postAoList.get(position);
        if (postAo != null) {
            return postAo.viewType; // 确保 viewType 是枚举类型的整数值
        }
        return -1; // 或者其他默认值
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
                return new PostHomePlusViewHolder(binding, onRecommendCardClick);
            }
            // 两个小卡片
            case TWO_SMALL_CARD -> {
                ViewRecommendCardBinding binding = ViewRecommendCardBinding.inflate(inflater, parent, false);
                return new PostHomeViewHolder(binding, onRecommendCardClick);
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
            case SINGLE_BIG_CARD -> ((PostHomePlusViewHolder)holder).setView(postAo);
            case TWO_SMALL_CARD -> ((PostHomeViewHolder)holder).setView(postAo);
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
