package com.czy.customviewlib.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardBinding;

public class PostItemViewHolder extends RecyclerView.ViewHolder {

    private final ViewRecommendCardBinding binding;

    public PostItemViewHolder(@NonNull ViewRecommendCardBinding binding,
                              OnRecommendCardClick onRecommendCardClick) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setView(){

    }
}
