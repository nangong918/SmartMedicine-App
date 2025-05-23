package com.czy.customviewlib.view.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewRecommendCardBinding;
import com.czy.customviewlib.databinding.ViewRecommendCardPlusBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.home.PostAo;

import java.util.List;
import java.util.Optional;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PostAo> postAoList;
    private final OnPositionItemClick onPositionItemClick;
    private final OnClickArticleCardCallBack onClickArticleCardCallBack;

    public PostAdapter(List<PostAo> postAoList,
                       OnPositionItemClick onPositionItemClick,
                       OnClickArticleCardCallBack onClickArticleCardCallBack) {
        this.postAoList = postAoList;
        this.onPositionItemClick = onPositionItemClick;
        this.onClickArticleCardCallBack = onClickArticleCardCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == PostAo.VIEW_TYPE_USER) {
            ViewRecommendCardBinding  binding = ViewRecommendCardBinding.inflate(inflater, parent, false);
            return new PostItemViewHolder(binding, onClickArticleCardCallBack);
        }
        else{
            ViewRecommendCardPlusBinding binding = ViewRecommendCardPlusBinding.inflate(inflater, parent, false);
            return new PostItemPlusViewHolder(binding, onClickArticleCardCallBack);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostAo postAo = postAoList.get(position);
        if (postAo == null){
            return;
        }
        if (postAo.viewType == PostAo.VIEW_TYPE_USER){
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
