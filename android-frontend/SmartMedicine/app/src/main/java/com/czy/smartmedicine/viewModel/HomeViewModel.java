package com.czy.smartmedicine.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.ao.home.FeatureContext;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.constant.home.RecommendCardType;
import com.czy.dal.dto.http.request.RecommendPostRequest;
import com.czy.dal.dto.http.response.RecommendPostResponse;
import com.czy.dal.vo.entity.home.PostVo;
import com.czy.dal.vo.viewModeVo.home.HomeVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public HomeViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public HomeVo homeVo = new HomeVo();

    public void init(HomeVo homeVo){
        initVo(homeVo);
        initialNetworkRequest();
    }

    private void initVo(HomeVo homeVo){
        this.homeVo = homeVo;
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }

    // 获取推荐帖子
    public void getPostList(){
        FeatureContext currentFeatureContext = getFeatureContext();
        RecommendPostRequest request = new RecommendPostRequest();
        request.featureContext = currentFeatureContext;
        request.userAccount = MainApplication.getInstance().getUserLoginInfoAo().account;
        apiRequestImpl.getPost(
                request,
                this::handleGetPostList,
                ViewModelUtil::globalThrowableToast
        );
    }

    /**
     * 处理获取帖子列表
     * @param response  接口返回的数据
     */
    private void handleGetPostList(BaseResponse<RecommendPostResponse> response) {
        List<PostInfoUrlAo> postInfoAos = Optional.ofNullable(response)
                .map(BaseResponse::getData)
                .map(RecommendPostResponse::getPostInfoUrlAos)
                .orElse(new ArrayList<>());

        if (postInfoAos.isEmpty()){
            return;
        }

        // postInfoUrlAo -> PostAo
        List<PostAo> postAoList = getPostAoListByResponse(postInfoAos);

        // homeList原先存在的列表
        List<PostAo> homeList = Optional.ofNullable(homeVo.postListVo.postAoListLd)
                        .map(MutableLiveData::getValue)
                        .orElse(new ArrayList<>());
        homeList.addAll(postAoList);

        // 设置值，观察者模式会通知view更新
        homeVo.postListVo.postAoListLd.setValue(homeList);
    }

    //---------------------------Logic---------------------------

    private final FeatureContext featureContext = new FeatureContext();

    public void setFeatureContext(List<Long> postIds){
        // 添加全部上下文
        featureContext.postIds.addAll(postIds);
        featureContext.timestamp = System.currentTimeMillis();
    }

    public FeatureContext getFeatureContext(){
        FeatureContext copyFeatureContext = this.featureContext.copy();
        clearFeatureContext();
        return copyFeatureContext;
    }

    private void clearFeatureContext(){
        this.featureContext.clear();
    }

    // postInfoUrlAoList -> PostAoList
    private List<PostAo> getPostAoListByResponse(List<PostInfoUrlAo> postInfoAos){
        List<PostAo> postAoList = new ArrayList<>();
        List<Integer> postTypeList = getPostType(postInfoAos.size());
        if (postTypeList.isEmpty()){
            return new ArrayList<>();
        }
        int index = 0;
        for (Integer postType : postTypeList){
            PostAo postAo = new PostAo(postType);
            if (RecommendCardType.TWO_SMALL_CARD.value == postType){
                // post1
                PostVo postVo1 = PostVo.getRecommendPostVoFromPostInfoUrlAo(
                        postInfoAos.get(index)
                );
                postAo.postVos[0] = postVo1;
                // post2
                PostVo postVo2 = PostVo.getRecommendPostVoFromPostInfoUrlAo(
                        postInfoAos.get(index + 1)
                );
                postAo.postVos[1] = postVo2;
            }
            else {
                postAo.postVos[0] = PostVo.getRecommendPostVoFromPostInfoUrlAo(
                        postInfoAos.get(index)
                );
            }
            index += postType;
            postAoList.add(postAo);
        }
        return postAoList;
    }

    public static List<Integer> getPostType(Integer count){
        /**
         * 整除：(4 + 1) * 4 = 20；四个一组为TWO_SMALL_CARD；第五个为SINGLE_BIG_CARD；一直延续
         * 余数：
         *      余下1为SINGLE_BIG_CARD
         *      余下2为2个TWO_SMALL_CARD；
         *      余下3为2个TWO_SMALL_CARD + 1个为SINGLE_BIG_CARD；
         *      余下4为4个TWO_SMALL_CARD；
         *      余下5为4个TWO_SMALL_CARD + 1个为SINGLE_BIG_CARD；
         */
        List<Integer> postTypes = new ArrayList<>();
        // 每组包含 4 个 TWO_SMALL_CARD 和 1 个 SINGLE_BIG_CARD
        int fullGroups = count / 5; // 完整组的数量
        int remainder = count % 5;   // 剩余的数量

        // 添加完整组
        for (int i = 0; i < fullGroups; i++) {
            postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
            postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//            postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//            postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
            postTypes.add(RecommendCardType.SINGLE_BIG_CARD.value);
        }

        // 处理剩余的视图类型
        switch (remainder) {
            case 1:
                postTypes.add(RecommendCardType.SINGLE_BIG_CARD.value);
                break;
            case 2:
                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
                break;
            case 3:
                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
                postTypes.add(RecommendCardType.SINGLE_BIG_CARD.value);
                break;
            case 4:
                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
//                postTypes.add(RecommendCardType.TWO_SMALL_CARD.value);
                break;
        }

        return postTypes;
    }

}
