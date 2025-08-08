package com.czy.customviewlib.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.dal.ao.home.PostAo;

public class PostItemViewHolder extends RecyclerView.ViewHolder {

    private final ViewRecommendCardBinding binding;

    public PostItemViewHolder(@NonNull ViewRecommendCardBinding binding,
                              @NonNull OnRecommendCardClick onClick) {
        super(binding.getRoot());
        this.binding = binding;

        // setClick
        PostItemViewManager.setClick(binding, onClick, getAdapterPosition());
    }

    public void setView(@NonNull PostAo postAo){
        PostItemViewManager.setView(binding, postAo);
    }
}
