package com.czy.customviewlib.view.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.constant.home.RecommendCardType;

import java.util.List;
import java.util.Optional;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        if (viewType == RecommendCardType.TWO_SMALL_CARD.value) {
            ViewRecommendCardBinding binding = ViewRecommendCardBinding.inflate(inflater, parent, false);
            return new PostItemViewHolder(binding, onRecommendCardClick);
        }
        else{
            ViewRecommendCardPlusBinding binding = ViewRecommendCardPlusBinding.inflate(inflater, parent, false);
            return new PostItemPlusViewHolder(binding, onRecommendCardClick);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostAo postAo = postAoList.get(position);
        if (postAo == null){
            return;
        }
        if (postAo.viewType == RecommendCardType.TWO_SMALL_CARD.value){
            ((PostItemViewHolder)holder).setView();
        }
        else {
            ((PostItemPlusViewHolder)holder).setView();
        }
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(postAoList)
                .map(List::size)
                .orElse(0);
    }
}
