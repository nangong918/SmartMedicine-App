package com.example.chattest.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chattest.Home.ArticlePage.ArticlePage;
import com.example.chattest.Home.HTTP.DataAnalysis.ArticleDataFormat;
import com.example.chattest.Home.HTTP.DataAnalysis.RecommendJsonAnalysis;
import com.example.chattest.Home.RecyclerView.HomeAdapter;
import com.example.chattest.Home.RecyclerView.UI_QuantityData_Parser;
import com.example.chattest.R;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.User.Request.HeartbeatPostTask;
import com.example.chattest.User.User;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.SpaceItemDecoration;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements HomeFragmentInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //--------------------------------------OnCreate-----------------------------------------------

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        //Init
        Init();

        //Listener
        SetListener();

        //RecyclerView
        RecyclerMain();

        return binding.getRoot();
    }

    //--------------------------------------Init--------------------------------------------------
    private final String requestClass = "HomeFragment";
    private void Init(){
        THIS = this;
        if(User.Logged){
            HeartbeatPostTask heartbeatPostTask = new HeartbeatPostTask();
            heartbeatPostTask.start();
        }
        this.userViewTimeList = new HashMap<>();
        this.backArticleBehaviorSet = new HashSet<>();
        MyDebug.Print("获得的图像长度："+(User.User_image != null));
        if(User.User_image != null){
            ImageUtils utils = new ImageUtils();
            utils.SetImageByByte(binding.userFace,User.User_image, R.drawable.basic_user);
        }
        binding.connectText.setVisibility(View.INVISIBLE);
        articleCacheList = new ArrayList<>();
        RequestUtils requestUtils = new RequestUtils(
                10*60*1000,requestClass,"GET", UrlUtil.GetRecommendList()
        );
        //赋予回调函数
        requestUtils.callback = HomeFragment.this;
        requestUtils.StartThread();
    }

    //--------------------------------------Listener-----------------------------------------------

    private void SetListener(){
        binding.userFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomNavigationItemClickListener != null){
                    bottomNavigationItemClickListener.onBottomNavigationItemClick();
                }
            }
        });

        binding.searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchBarClickListener != null){
                    searchBarClickListener.searchBarClick();
                }
            }
        });

        binding.testButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestUtils requestUtils = new RequestUtils(
                        10*60*1000,requestClass,"GET", UrlUtil.GetRecommendList()
                );
                //赋予回调函数
                requestUtils.callback = HomeFragment.this;
                requestUtils.StartThread();
            }
        });
    }

    //--------------------------------------Result-----------------------------------------------

    private static final int ARTICLE_REQUEST_CODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    //--------------------------------------RecyclerView + HTTP--------------------------------------------

    public HomeAdapter homeAdapter;
    public static List<HomeAdapter.CardItem> cardItems;

    private void RecyclerMain(){
        //Init
        cardItems = new ArrayList<>();

        //GetData
        //recyclerGetDataTest();

        //adapter
        homeAdapter = new HomeAdapter(cardItems,this);

        //set adapter for recycler
        binding.homeFragmentRecyclerView.setAdapter(homeAdapter);

        //set spacing
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(0,0,10,10);
        binding.homeFragmentRecyclerView.addItemDecoration(itemDecoration);
    }

    private void recyclerGetDataTest(){
        HomeAdapter.CardItem[] cardItems1 = new HomeAdapter.CardItem[6];
        for(int i = 0;i<3;i++){
            cardItems1[i] = new HomeAdapter.CardItem(HomeAdapter.CardItem.VIEW_TYPE_PLUS);
            cardItems1[i].Title[0] = "PlusTest  "+i;
            cardItems1[i].UserName[0] = "PlusID  "+i;
            cardItems.add(cardItems1[i]);
        }
        for(int i = 3;i<6;i++){
            cardItems1[i] = new HomeAdapter.CardItem(HomeAdapter.CardItem.VIEW_TYPE_USER);
            cardItems1[i].Title[0] = "UserTest  "+i;
            cardItems1[i].UserName[0] = "UserID  "+i;
            cardItems1[i].Title[1] = "UserTest  "+i;
            cardItems1[i].UserName[1] = "UserID  "+i;
            cardItems.add(cardItems1[i]);
        }
    }

    //数据缓存列表
    public static List<ArticleDataFormat> articleCacheList;

    private void setRecyclerViewData(List<ArticleDataFormat> list){
        //获取的内容全部加入缓存列表 + 解决重复问题
        UI_QuantityData_Parser parser = new UI_QuantityData_Parser();
        List<ArticleDataFormat> newHttp = parser.ExistMatch(list,articleCacheList);

        //获取Ui类型
        List<Integer> ui_list = parser.GetLayoutUi(newHttp.size());

        //添加到cardItem
        if(ui_list.size() > 0){
            int index = 0;
            for(Integer ui : ui_list){
                HomeAdapter.CardItem item;
                if(ui == HomeAdapter.CardItem.VIEW_TYPE_USER){
                    item = new HomeAdapter.CardItem(HomeAdapter.CardItem.VIEW_TYPE_USER);
                    item.Title[0] = newHttp.get(index).title;
                    item.UserName[0] = newHttp.get(index).authorName;
                    item.ArticlePicture[0] = newHttp.get(index).ArticlePicture;
                    item.AuthorPicture[0] = newHttp.get(index).AuthorFacePicture;
                    item.Time[0] = newHttp.get(index).time;
                    item.Content[0] = newHttp.get(index).content;
                    item.ArticleId[0] = newHttp.get(index).articleId;
                    index += 1;
                    item.Title[1] = newHttp.get(index).title;
                    item.UserName[1] = newHttp.get(index).authorName;
                    item.ArticlePicture[1] = newHttp.get(index).ArticlePicture;
                    item.AuthorPicture[1] = newHttp.get(index).AuthorFacePicture;
                    item.Time[1] = newHttp.get(index).time;
                    item.Content[1] = newHttp.get(index).content;
                    item.ArticleId[1] = newHttp.get(index).articleId;
                }
                else {
                    item = new HomeAdapter.CardItem(HomeAdapter.CardItem.VIEW_TYPE_PLUS);
                    item.Title[0] = newHttp.get(index).title;
                    item.UserName[0] = newHttp.get(index).authorName;
                    item.ArticlePicture[0] = newHttp.get(index).ArticlePicture;
                    item.AuthorPicture[0] = newHttp.get(index).AuthorFacePicture;
                    item.Time[0] = newHttp.get(index).time;
                    item.Content[0] = newHttp.get(index).content;
                    item.ArticleId[0] = newHttp.get(index).articleId;
                }
                index += 1;
                cardItems.add(item);
            }

            //刷新RecyclerView
            homeAdapter.notifyItemRangeInserted(cardItems.size() - ui_list.size(),ui_list.size());
        }
    }

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if (Objects.equals(callbackClass, requestClass)){
            binding.progressBar.setVisibility(View.GONE);
            RecommendJsonAnalysis analysis = new RecommendJsonAnalysis();
            List<ArticleDataFormat> list = analysis.GetArticleData(rData,"data");
            //设置数据
            MyDebug.Print("list:"+list.size());
            setRecyclerViewData(list);
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        //请求回调相关
        if (Objects.equals(callbackClass, requestClass)){
            //提示连接失败
            binding.progressBar.setVisibility(View.GONE);
            binding.connectText.setVisibility(View.VISIBLE);
        }
    }

    //--------------------------------------Interface-----------------------------------------------
    public static HomeFragment THIS;
    public Map<Integer, Long> userViewTimeList;
    @Override
    public void intentReturned(Intent data) {
        int position = data.getIntExtra("position", -1);
        int cardId = data.getIntExtra("cardId",-1);
        long viewTime = data.getLongExtra("viewTime", -1);

        if (position != -1 && viewTime != -1 && cardId != -1) {
            int id = getArticleIdByPosition(position,cardId);
            userViewTimeList.put(id,viewTime);
            MyDebug.Print("测试："+userViewTimeList.get(id));
            GetArticleViewState(id);
        }
    }

    // --------------------GetArticleViewState

    public static Set<BackArticleBehavior> backArticleBehaviorSet;

    private void GetArticleViewState(int ID){
        // 获取userViewTimeList中的所有id
        long ViewTime = userViewTimeList.get(ID);

        boolean isExisting = false;
        //存在：修改
        for(BackArticleBehavior behavior : backArticleBehaviorSet){
            if (behavior.articleId == ID) {
                isExisting = true;
                behavior.viewTime = ViewTime;
                break;
            }
        }
        //不存在：添加
        if (!isExisting) {
            BackArticleBehavior behavior = new BackArticleBehavior();
            behavior.articleId = ID;
            behavior.viewTime = ViewTime;
            if(User.Logged){
                behavior.userId = User.user_id;
            }
            else {
                behavior.userId = 0;
            }
            backArticleBehaviorSet.add(behavior);
        }
    }

    // --------------------GetIdByPosition

    private int getArticleIdByPosition(int position,int cardId){
        HomeAdapter.CardItem item = cardItems.get(position);
        int id;
        if (item.viewType == HomeAdapter.CardItem.VIEW_TYPE_USER){
            id = Integer.parseInt(item.ArticleId[cardId]);
        }
        else {
            id = Integer.parseInt(item.ArticleId[0]);
        }
        return id;
    }

    // --------------------MainBar
    public interface OnBottomNavigationItemClickListener {
        void onBottomNavigationItemClick();
    }

    public interface SearchBarClickListener {
        void searchBarClick();
    }

    // 声明接口变量
    private OnBottomNavigationItemClickListener bottomNavigationItemClickListener;
    private SearchBarClickListener searchBarClickListener;

    // 在 onAttach() 方法中将 Activity 转换为接口实例
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomNavigationItemClickListener = (OnBottomNavigationItemClickListener) context;
            searchBarClickListener = (SearchBarClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBottomNavigationItemClickListener");
        }
    }

    // --------------------clickListener

    @Override
    public void onCardClickListener(int position, int cardType, int cardId,boolean[] feedbackButton) {
        Intent intent = new Intent(getActivity(), ArticlePage.class);
        // 添加要传递的参数
        intent.putExtra("position", position);
        intent.putExtra("cardType", cardType);
        intent.putExtra("cardId", cardId);
        intent.putExtra("class", requestClass);
        if(cardType == HomeAdapter.CardItem.VIEW_TYPE_USER){
            if(cardId == 0){
                intent.putExtra("like",feedbackButton[0]);
                intent.putExtra("star",feedbackButton[1]);
            }
            else{
                intent.putExtra("like",feedbackButton[3]);
                intent.putExtra("star",feedbackButton[4]);
            }
        }
        else {
            intent.putExtra("like",feedbackButton[0]);
            intent.putExtra("star",feedbackButton[1]);
        }

        // Activity跳转并发送请求：
        getActivity().startActivityForResult(intent,ARTICLE_REQUEST_CODE);
    }

    @Override
    public void onButtonClickListener(int position, int cardType, int cardId, int buttonType,boolean[] feedbackButton) {
        Log.d("Runtime", "Button: "+buttonType);
        if(User.Logged){
            if(buttonType == 0){
                Toast.makeText(getContext(), "点赞成功", Toast.LENGTH_SHORT).show();
                int id = getArticleIdByPosition(position,cardId);
                clickSet_backArticleBehaviorSet(BackArticleBehavior.LIKE,id);
            }
            else if (buttonType == 1){
                Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                int id = getArticleIdByPosition(position,cardId);
                clickSet_backArticleBehaviorSet(BackArticleBehavior.COLLECT,id);
            }
            else {
                Toast.makeText(getContext(), "您已不喜欢", Toast.LENGTH_SHORT).show();
                int id = getArticleIdByPosition(position,cardId);
                clickSet_backArticleBehaviorSet(BackArticleBehavior.UNLIKE,id);
            }
        }
        else {
            Toast.makeText(getContext(), "您还未登录！", Toast.LENGTH_SHORT).show();
        }

    }


    // --------------------click

    private void clickSet_backArticleBehaviorSet(int ClickType,int id){
        boolean isExisting = false;
        //存在：修改
        for(BackArticleBehavior behavior : backArticleBehaviorSet){
            if (behavior.articleId == id) {
                isExisting = true;
                behavior.like = ClickType;
                break;
            }
        }
        //不存在：添加
        if (!isExisting) {
            BackArticleBehavior behavior = new BackArticleBehavior();
            behavior.articleId = id;
            behavior.like = ClickType;
            if(User.Logged){
                behavior.userId = User.user_id;
            }
            else {
                behavior.userId = 0;
            }
            backArticleBehaviorSet.add(behavior);
        }
    }

    //--------------------------------------Request-----------------------------------------------


}