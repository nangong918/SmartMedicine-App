package com.czy.customviewlib.view.home;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.ao.home.PostAo;

public class PostItemPlusViewHolder extends RecyclerView.ViewHolder{

    private final ViewRecommendCardPlusBinding binding;

    public PostItemPlusViewHolder(@NonNull ViewRecommendCardPlusBinding binding,
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
