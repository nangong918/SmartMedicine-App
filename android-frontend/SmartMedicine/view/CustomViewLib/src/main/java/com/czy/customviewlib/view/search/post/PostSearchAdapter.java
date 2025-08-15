package com.czy.customviewlib.view.search.post;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewPostSearchBinding;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.List;

public class PostSearchAdapter extends RecyclerView.Adapter<PostSearchViewHolder>{

    private final static String TAG = PostSearchAdapter.class.getName();

    // list指针
    private final List<PostVo> postVoListPointer;

    private final OnPostClick onPostClick;

    public PostSearchAdapter(@NonNull List<PostVo> postVoList,
                             @NonNull OnPostClick onPostClick){
        this.postVoListPointer = postVoList;
        this.onPostClick = onPostClick;
    }

    @NonNull
    @Override
    public PostSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewPostSearchBinding binding = ViewPostSearchBinding.inflate(inflater, parent, false);
        return new PostSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostSearchViewHolder holder, int position) {
        PostVo postVo = postVoListPointer.get(position);
        if (postVo == null){
            Log.w(TAG, "Post数据为空");
            return;
        }
        // 设置view数据
        holder.setView(postVo);
        // 设置点击事件
        this.onPostClick.onPostClick(position, postVo.postId);
    }

    @Override
    public int getItemCount() {
        return postVoListPointer.size();
    }
}
