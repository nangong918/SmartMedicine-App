package com.example.chattest.Home.RecyclerView;

import android.util.Log;

import com.example.chattest.Home.HTTP.DataAnalysis.ArticleDataFormat;

import java.util.ArrayList;
import java.util.List;

public class UI_QuantityData_Parser {

    private final int MaxNum = 9;

    public List<Integer> GetLayoutUi(int UiNum){
        List<Integer> uiList = new ArrayList<>();
        if(UiNum >= MaxNum){
            for(int i = 0;i < 4;i++){
                uiList.add(HomeAdapter.CardItem.VIEW_TYPE_USER);
            }
            uiList.add(HomeAdapter.CardItem.VIEW_TYPE_PLUS);
        }
        else if (UiNum <= 0) {
            return uiList;
        }
        else if (UiNum % 2 == 0){//偶数个
            int AllNum = UiNum / 2;
            for(int i = 0;i < AllNum;i++){
                uiList.add(HomeAdapter.CardItem.VIEW_TYPE_USER);
            }
        }
        else {//奇数个
            int AllNum = (UiNum - 1) / 2;
            if(AllNum > 0){
                for(int i = 0;i < AllNum;i++){
                    uiList.add(HomeAdapter.CardItem.VIEW_TYPE_USER);
                }
            }
            uiList.add(HomeAdapter.CardItem.VIEW_TYPE_PLUS);
        }
        return uiList;
    }

    public List<ArticleDataFormat> ExistMatch(List<ArticleDataFormat> http,List<ArticleDataFormat> cache){
        List<ArticleDataFormat> newHttp = new ArrayList<>();
        for(ArticleDataFormat httpData : http){
            boolean match = false;
            for(ArticleDataFormat cacheData : cache){///Bug:为0不添加，非0多次添加
                if(httpData.authorId.equals(cacheData.authorId) &&
                        httpData.title.equals(cacheData.title) &&
                        httpData.time.equals(cacheData.time)){//时间、作者、标题都相同视为相同
                    match = true;
                    break;
                }
            }
            if (!match){
                newHttp.add(httpData);
            }
        }
        cache.addAll(newHttp);
        return newHttp;
    }
}
