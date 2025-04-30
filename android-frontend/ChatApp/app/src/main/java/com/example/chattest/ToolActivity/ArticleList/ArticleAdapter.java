package com.example.chattest.ToolActivity.ArticleList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.R;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.databinding.MyviewArticleListBinding;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //---------------------定义item---------------------

    public static class ArticleItem{
        public String Title;
        public String UserName;
        public byte[] ArticlePicture;
        public byte[] AuthorPicture;
        public String Content;
        public String Time;
        public ArticleItem(){

        }

    }

    public static List<ArticleItem> articleItemList;

    //---------------------Constructor---------------------

    public ArticleAdapter(
            List<ArticleItem> articleItemList,
            com.example.chattest.ToolActivity.ArticleList.onClickArticleListCallBack onClickArticleListCallBack) {
        ArticleAdapter.articleItemList = articleItemList;
        this.onClickArticleListCallBack = onClickArticleListCallBack;
    }


    //---------------------Interface---------------------

    private final onClickArticleListCallBack onClickArticleListCallBack;

    //---------------------Adapter---------------------

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MyviewArticleListBinding binding = MyviewArticleListBinding.inflate(inflater,parent,false);
        return new ArticleListViewHolder(binding,this.onClickArticleListCallBack);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ArticleItem articleItem = articleItemList.get(position);
        ImageUtils imageUtils = new ImageUtils();

        //binding
        ArticleListViewHolder articleListViewHolder = (ArticleListViewHolder)holder;
        articleListViewHolder.binding.textTitle.setText(articleItem.Title);
        articleListViewHolder.binding.cardUserID.setText(articleItem.UserName);
        imageUtils.SetImageByByte(articleListViewHolder.binding.ListImage,articleItem.ArticlePicture, R.drawable.chat_ai);
        imageUtils.SetImageByByte(articleListViewHolder.binding.cardUserFace,articleItem.AuthorPicture, R.drawable.chat_ai);

    }

    @Override
    public int getItemCount() {
        return articleItemList.size();
    }

    //----------------------ViewHolder---------------------

    private static class ArticleListViewHolder extends RecyclerView.ViewHolder{

        private final MyviewArticleListBinding binding;
        private final onClickArticleListCallBack onClickArticleListCallBack;

        private void Set_Listener(){
            this.binding.linearTorch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickArticleListCallBack.onListClickListener(getAdapterPosition());
                }
            });
        }

        public ArticleListViewHolder(MyviewArticleListBinding binding, onClickArticleListCallBack onClickArticleListCallBack) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClickArticleListCallBack = onClickArticleListCallBack;
            this.Set_Listener();
        }
    }
}
