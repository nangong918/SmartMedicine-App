package com.example.chattest.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chattest.R;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.ToolActivity.ArticleList.ArticleListActivity;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.FragmentUserBinding;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements CallBackInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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

    private FragmentUserBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater,container,false);

        Init();

        SetListener();

        return binding.getRoot();
    }

    //-------------------------Init-------------------------

    private UserFragment THIS;
    @SuppressLint("SetTextI18n")
    private void Init(){
        THIS = this;
        if(User.Logged){
            binding.UserName.setText("Name:"+User.Name);
            binding.UserId.setText("Id:"+User.user_id);
            ImageUtils utils = new ImageUtils();
            utils.SetImageByByte(binding.userFace,User.User_image, R.drawable.basic_user);
        }
    }

    //-------------------------Listener-------------------------

    private void SetListener(){
        binding.collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(User.Logged){
                    getUserCollectionArticle();
                }
                else {
                    Toast.makeText(THIS.getContext(), "您还未登录！！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //-------------------------Intent-------------------------

    private void StartIntent(){
        // 将JSONArray对象转换为int[]数组
        int[] intArray = new int[userCollectionArticleId_list.size()];
        for (int i = 0; i < userCollectionArticleId_list.size(); i++) {
            intArray[i] = userCollectionArticleId_list.get(i);
        }

        Intent intent = new Intent(this.getContext(), ArticleListActivity.class);
        // 传递int[]数组作为参数
        intent.putExtra("list", intArray);
        startActivity(intent);
    }

    //-------------------------HTTP + request-------------------------

    private List<Integer> userCollectionArticleId_list;
    private final String RequestClass_GetCollection = "UserFragment_GetCollection";

    private void getUserCollectionArticle(){
        MyDebug.Print("请求开始");
        RequestUtils requestUtils = new RequestUtils(10 * 1000,RequestClass_GetCollection,"GET", UrlUtil.Get_collection_url(User.user_id));
        requestUtils.callback = this;
        requestUtils.StartThread();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if(callbackClass.equals(RequestClass_GetCollection)){
            Object data = R_Util.get_dataByJsonObject("data",rData);
            userCollectionArticleId_list = (List<Integer>)data;
            StartIntent();
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(RequestClass_GetCollection)){
            userCollectionArticleId_list = new ArrayList<>();
            Toast.makeText(THIS.getContext(), "您还没有任何收藏", Toast.LENGTH_SHORT).show();
        }
    }

}