package com.czy.customviewlib.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;

public class PostItemPlusViewHolder extends RecyclerView.ViewHolder{

    private final ViewRecommendCardPlusBinding binding;

    public PostItemPlusViewHolder(@NonNull ViewRecommendCardPlusBinding binding, OnClickArticleCardCallBack clickArticleCallBack) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setView(){

    }
}
