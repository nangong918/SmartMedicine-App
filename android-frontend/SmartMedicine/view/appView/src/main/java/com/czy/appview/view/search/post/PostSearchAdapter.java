package com.czy.appview.view.search.post;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewPostSearchBinding;
import com.czy.domain.vo.entity.home.PostPreviewExVo;

import java.util.List;


public class PostSearchAdapter extends RecyclerView.Adapter<PostSearchViewHolder>{

    private final static String TAG = PostSearchAdapter.class.getName();

    // list指针
    private final List<PostPreviewExVo> postPreviewExVosPointer;

    private final OnPostClick onPostClick;

    public PostSearchAdapter(@NonNull List<PostPreviewExVo> postSearchResultAo,
                             @NonNull OnPostClick onPostClick){
        this.postPreviewExVosPointer = postSearchResultAo;
        this.onPostClick = onPostClick;
    }

    @NonNull
    @Override
    public PostSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewPostSearchBinding binding = ViewPostSearchBinding.inflate(inflater, parent, false);
        return new PostSearchViewHolder(binding, onPostClick);
    }

    @Override
    public void onBindViewHolder(@NonNull PostSearchViewHolder holder, int position) {
        PostPreviewExVo postPreviewExVo = postPreviewExVosPointer.get(position);
        if (postPreviewExVo == null){
            Log.w(TAG, "postExVo数据为空");
            return;
        }
        // 设置数据
        holder.setAo(postPreviewExVo);
    }

    @Override
    public int getItemCount() {
        return postPreviewExVosPointer.size();
    }
}
