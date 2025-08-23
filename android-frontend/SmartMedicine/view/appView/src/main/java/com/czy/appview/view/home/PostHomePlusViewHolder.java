package com.czy.appview.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.ao.home.PostAo;

public class PostHomePlusViewHolder extends RecyclerView.ViewHolder{

    private final ViewRecommendCardPlusBinding binding;

    public PostHomePlusViewHolder(@NonNull ViewRecommendCardPlusBinding binding,
                                  @NonNull OnRecommendCardClick onClick) {
        super(binding.getRoot());
        this.binding = binding;

        // setClick
        PostHomeItemViewManager.setClick(binding, onClick, this);
    }

    public void setView(@NonNull PostAo postAo){
        PostHomeItemViewManager.setView(binding, postAo);
    }
}
