package com.example.chattest.Home.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.R;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.databinding.MyviewRecommendCardBinding;
import com.example.chattest.databinding.MyviewRecommendCardPlusBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{




    //------------------------------------ItemList--------------------------------------------------

    public static class CardItem{
        public final static int VIEW_TYPE_USER = 2,VIEW_TYPE_PLUS = 1;
        public int viewType;
        public String[] Title;
        public String[] UserName;
        //推荐图片+用户头像
        public byte[][] ArticlePicture;
        public byte[][] AuthorPicture;
        public String[] Content;
        public String[] Time;
        public String[] ArticleId;


        public CardItem(int viewType){
            this.viewType = viewType;
            Title = new String[viewType];
            UserName = new String[viewType];
            ArticlePicture = new byte[viewType][];
            AuthorPicture = new byte[viewType][];
            Content = new String[viewType];
            Time = new String[viewType];
            this.ArticleId = new String[viewType];
        }
    }

    private List<CardItem> cardItems;

    //------------------------------------Interface---------------------------------------------------

    private onClickArticleCardCallBack clickArticleCallBack;

    //------------------------------------Adapter---------------------------------------------------


    public HomeAdapter(List<CardItem> cardItems, onClickArticleCardCallBack clickArticleCallBack){
        this.cardItems = cardItems;
        this.clickArticleCallBack = clickArticleCallBack;
    }


    @Override
    public int getItemViewType(int position) {
        return cardItems.get(position).viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == CardItem.VIEW_TYPE_USER) {
            MyviewRecommendCardBinding binding = MyviewRecommendCardBinding.inflate(inflater, parent, false);
            return new UserCardViewHolder(binding,clickArticleCallBack);
        }
        else{
            MyviewRecommendCardPlusBinding binding = MyviewRecommendCardPlusBinding.inflate(inflater, parent, false);
            return new CardPlusViewHolder(binding,clickArticleCallBack);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        ImageUtils imageUtils = new ImageUtils();
        if(cardItem.viewType == CardItem.VIEW_TYPE_USER){
            ((UserCardViewHolder)holder).binding.textTitle.setText(cardItem.Title[0]);
            ((UserCardViewHolder)holder).binding.cardUserID.setText(cardItem.UserName[0]);
            imageUtils.SetImageByByte(((UserCardViewHolder)holder).binding.cardImage,cardItem.ArticlePicture[0], R.drawable.chat_ai);
            imageUtils.SetImageByByte(((UserCardViewHolder)holder).binding.cardUserFace,cardItem.AuthorPicture[0], R.drawable.chat_ai);

            ((UserCardViewHolder)holder).binding.textTitle2.setText(cardItem.Title[1]);
            ((UserCardViewHolder)holder).binding.cardUserID2.setText(cardItem.UserName[1]);
            imageUtils.SetImageByByte(((UserCardViewHolder)holder).binding.cardImage2,cardItem.ArticlePicture[1], R.drawable.chat_ai);
            imageUtils.SetImageByByte(((UserCardViewHolder)holder).binding.cardUserFace2,cardItem.AuthorPicture[1], R.drawable.chat_ai);
        }
        else{
            ((CardPlusViewHolder)holder).binding.textTitle.setText(cardItem.Title[0]);
            ((CardPlusViewHolder)holder).binding.cardUserID.setText(cardItem.UserName[0]);
            imageUtils.SetImageByByte(((CardPlusViewHolder)holder).binding.cardImage,cardItem.ArticlePicture[0], R.drawable.chat_ai);
            imageUtils.SetImageByByte(((CardPlusViewHolder)holder).binding.cardUserFace,cardItem.AuthorPicture[0], R.drawable.chat_ai);
        }
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }


    //-------------------------------------ViewHolder----------------------------------------------

    public static class UserCardViewHolder extends RecyclerView.ViewHolder  {
        private MyviewRecommendCardBinding binding;
        private onClickArticleCardCallBack clickArticleCallBack;
        boolean[] feedbackButton;

        public UserCardViewHolder(MyviewRecommendCardBinding binding, onClickArticleCardCallBack clickArticleCallBack) {
            super(binding.getRoot());
            this.binding = binding;
            feedbackButton = new boolean[6];
            this.clickArticleCallBack = clickArticleCallBack;
            ListenerSet();
        }


        private void ListenerSet(){
            binding.basicCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onCardClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,0,feedbackButton);
                }
            });
            binding.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,0,0,feedbackButton);
                }
            });
            binding.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,0,1,feedbackButton);
                }
            });
            binding.unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,0,2,feedbackButton);
                }
            });
            //2
            binding.basicCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onCardClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,1,feedbackButton);
                }
            });
            binding.favorite2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,1,0,feedbackButton);
                }
            });
            binding.star2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,1,1,feedbackButton);
                }
            });
            binding.unlike2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_USER,1,2,feedbackButton);
                }
            });
        }

    }

    public static class CardPlusViewHolder extends RecyclerView.ViewHolder{
        private MyviewRecommendCardPlusBinding binding;
        private onClickArticleCardCallBack clickArticleCallBack;
        boolean[] feedbackButton;
        public CardPlusViewHolder(MyviewRecommendCardPlusBinding binding, onClickArticleCardCallBack clickArticleCallBack) {
            super(binding.getRoot());
            this.binding = binding;
            feedbackButton = new boolean[3];
            this.clickArticleCallBack = clickArticleCallBack;
            ListenerSet();
        }


        private void ListenerSet(){
            binding.basicCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onCardClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_PLUS,0,feedbackButton);
                }
            });
            binding.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_PLUS,0,0,feedbackButton);
                }
            });
            binding.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_PLUS,0,1,feedbackButton);
                }
            });
            binding.unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArticleCallBack.onButtonClickListener(getAdapterPosition(),CardItem.VIEW_TYPE_PLUS,0,2,feedbackButton);
                }
            });
        }
    }


}
