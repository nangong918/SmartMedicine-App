package com.czy.appview.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewRecommendCardBinding;
import com.czy.dal.ao.home.PostAo;

public class PostHomeViewHolder extends RecyclerView.ViewHolder {

    private final ViewRecommendCardBinding binding;

    public PostHomeViewHolder(@NonNull ViewRecommendCardBinding binding,
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
