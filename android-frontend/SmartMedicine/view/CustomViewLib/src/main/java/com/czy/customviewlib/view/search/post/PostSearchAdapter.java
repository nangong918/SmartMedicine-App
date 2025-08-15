package com.czy.customviewlib.view.search.post;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewPostSearchBinding;
import com.czy.dal.vo.entity.home.PostExVo;

import java.util.List;


public class PostSearchAdapter extends RecyclerView.Adapter<PostSearchViewHolder>{

    private final static String TAG = PostSearchAdapter.class.getName();

    // list指针
    private final List<PostExVo> postExVosPointer;

    private final OnPostClick onPostClick;

    public PostSearchAdapter(@NonNull List<PostExVo> postSearchResultAo,
                             @NonNull OnPostClick onPostClick){
        this.postExVosPointer = postSearchResultAo;
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
        PostExVo postExVo = postExVosPointer.get(position);
        if (postExVo == null){
            Log.w(TAG, "postExVo数据为空");
            return;
        }
        // 设置数据
        holder.setAo(postExVo);
    }

    @Override
    public int getItemCount() {
        return postExVosPointer.size();
    }
}
