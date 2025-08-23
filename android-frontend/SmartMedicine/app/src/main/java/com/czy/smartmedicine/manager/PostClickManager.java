package com.czy.smartmedicine.manager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appview.view.home.OnRecommendCardClick;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.ao.home.PostIntentAo;
import com.czy.dal.constant.NettyConstants;
import com.czy.dal.constant.home.PostOperation;
import com.czy.dal.constant.home.RecommendButtonType;
import com.czy.dal.constant.home.RecommendCardType;
import com.czy.dal.constant.netty.NettyOptionEnum;
import com.czy.dal.dto.netty.request.PostCollectRequest;
import com.czy.dal.dto.netty.request.PostDisLikeRequest;
import com.czy.dal.dto.netty.request.PostLikeRequest;
import com.czy.dal.dto.netty.request.UserBrowseTimeRequest;
import com.czy.dal.dto.netty.request.UserClickPostRequest;
import com.czy.dal.vo.entity.home.PostVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.PostActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PostClickManager {

    private static final String TAG = PostClickManager.class.getName();

    // postList指针
    private final List<PostAo> postAoList;
    // Logger行为的netty发送者
    private final SocketMessageSender socketSender;
    // 启动postActivity的IntentLauncher
    private ActivityResultLauncher<Intent> openPostActivityLauncher;

    public PostClickManager(@NonNull List<PostAo> postAoList, @NonNull ActivityResultCaller activityResultCaller){
        this.postAoList = postAoList;
        this.socketSender = MainApplication.getInstance().getMessageSender();
        Log.i(TAG, "PostClickManager init: this.socketSender : "
                + this.socketSender + " this.socketSender == null?"
                + (this.socketSender == null));
        initActivityLauncher(activityResultCaller);
    }

    private long startReadPostTime = System.currentTimeMillis();

    private void initActivityLauncher(ActivityResultCaller caller) {
        openPostActivityLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 计算观看时长
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startReadPostTime;

                        if (result.getData() == null) {
                            Log.w(TAG, "浏览post返回结果失败，result.getData() == null");
                            return;
                        }

                        Long postId = result.getData().getLongExtra(PostIntentAo.POST_ID, NettyConstants.ERROR_ID);
                        // 记录观看时长
                        recordViewingDuration(duration, postId);
                    } else {
                        Log.w(TAG, "打开帖子失败");
                    }
                }
        );
    }

    public OnRecommendCardClick getOnRecommendCardClick(FragmentActivity activity) {
        return new OnRecommendCardClick() {
            @Override
            public void onCardClick(int position, RecommendCardType cardType, int cardId) {
                PostVo postVo = getPostInfoByList(position, cardId);
                Long postId = Optional.ofNullable(postVo)
                        .map(p -> p.postId)
                        .orElse(null);
                if (postId == null){
                    Log.e(TAG, "帖子id为空");
                    Toast.makeText(activity, activity.getString(com.czy.appview.R.string.post_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                startPostActivityIntent(postId, activity);
            }

            @Override
            public void onButtonClick(int position, RecommendCardType cardType, int cardId, RecommendButtonType buttonType) {
                PostClickManager.this.onButtonClick(position, cardType, cardId, buttonType);
            }
        };
    }

    public void onButtonClick(int position, RecommendCardType cardType, int cardId, RecommendButtonType buttonType){
        PostVo postVo = getPostInfoByList(position, cardId);

        // 根据当前状态得出操作类型
        PostOperation postOperation = postVo.clickChange(buttonType);
        // 状态切换
        postVo.clickChange(postOperation);

        // 埋点行为交给后端
        // 此处智能处理这6中情况
        switch (postOperation){
            case LIKE -> {
                PostLikeRequest request = new PostLikeRequest(postVo.postId, NettyOptionEnum.ADD.getCode());
                socketSender.postLike(request);
            }
            case CANCEL_LIKE -> {
                PostLikeRequest request = new PostLikeRequest(postVo.postId, NettyOptionEnum.DELETE.getCode());
                socketSender.postLike(request);
            }
            case COLLECT -> {
                // 默认文件夹
                PostCollectRequest request = new PostCollectRequest(postVo.postId, null, NettyOptionEnum.ADD.getCode());
                socketSender.postCollect(request);
            }
            case CANCEL_COLLECT -> {
                // 默认文件夹
                PostCollectRequest request = new PostCollectRequest(postVo.postId, null, NettyOptionEnum.DELETE.getCode());
                socketSender.postCollect(request);
            }
            case NOT_INTERESTED -> {
                PostDisLikeRequest request = new PostDisLikeRequest(postVo.postId, NettyOptionEnum.ADD.getCode());
                socketSender.notInterested(request);
            }
            case CANCEL_NOT_INTERESTED -> {
                PostDisLikeRequest request = new PostDisLikeRequest(postVo.postId, NettyOptionEnum.DELETE.getCode());
                socketSender.notInterested(request);
            }
        }
    }

    /**
     * 通过cardId获取cardType获取post的view信息
     * @param position  行索引
     * @param cardId    卡片id
     * @return          post的view信息
     */
    public PostVo getPostInfoByList(int position, int cardId){
        if (postAoList.isEmpty()){
            return null;
        }
//        int realIndex = getPostRealIndexByPosition(position, cardId);
        PostAo postAo = postAoList.get(position);
        assert cardId >= 0 && cardId < 2;
        if (RecommendCardType.TWO_SMALL_CARD.value == postAo.viewType){
            return postAo.postVos[cardId];
        }
        return postAo.postVos[0];
    }

    /**
     * 通过行索引获取list中真实的位置
     * @param position  行索引
     * @param cardId    卡片索引(列索引)
     * @return  list中真实位置
     */
    @Deprecated(since = "2025/8/11 [理解错误: postAoList是直接可用的, 并不需要进行转换]")
    private int getPostRealIndexByPosition(int position, int cardId){
        if (position > postAoList.size() || position < 0 || (cardId != 0 && cardId != 1)){
            Log.w(TAG, "getPostRealIndexByPosition: position out of range, position: " + position + " cardId:" + cardId);
            throw new IllegalArgumentException("position out of range, position: " + position + " cardId:" + cardId);
        }
        int index = 0;
        for (int i = 0; i < position; i++){
            index += postAoList.get(i).postVos.length;
        }
        /*
         * eg: realList[size7] -> list[2,2,2,1]
         * param:
         *  (0,0) -> 0
         *  (0,1) -> 1
         *  (1,0) -> 2
         *  (1,1) -> 3
         *  (2,0) -> 4
         *  (2,1) -> 5
         *  (3,0) -> 6
         * 没有问题
         */
        int realIndex = index + cardId;
        Log.i(TAG, "param: [position: " + position + ", cardId: " + cardId
                + "] result: [realIndex: " + realIndex + "]");
        return realIndex;
    }

    private void startPostActivityIntent(Long postId, FragmentActivity activity){
        if (postId == null){
            return;
        }
        PostIntentAo postIntentAo = new PostIntentAo();
        postIntentAo.postId = postId;
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra(PostIntentAo.POST_OPEN_INTENT, postIntentAo);

        Log.i("check_netty", "PostClickManager::MessageSender: " + MainApplication.getInstance().getMessageSender());

        activity.startActivity(intent);
//        openPostActivityLauncher.launch(intent);

        startReadPostTime = System.currentTimeMillis();
        // 记录点击（浏览）post事件
        recordPostView(postId);

    }

    // 记录点击（浏览）post事件
    public void recordPostView(Long postId) {
        Long userId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        Long time = System.currentTimeMillis();
        UserClickPostRequest request = new UserClickPostRequest();
        request.receiverId = NettyConstants.SERVER_ID;
        request.postId = postId;
        request.senderId = userId;
        request.timestamp = String.valueOf(time);
        Log.i("check_netty", "recordPostView::MessageSender: " + MainApplication.getInstance().getMessageSender());
        this.socketSender.uploadClickEvent(request);
    }

    // 记录观看时长
    public void recordViewingDuration(long duration, Long postId) {
        Long userId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        Long time = System.currentTimeMillis();
        UserBrowseTimeRequest request = new UserBrowseTimeRequest();
        request.receiverId = NettyConstants.SERVER_ID;
        request.senderId = userId;
        request.timestamp = String.valueOf(time);
        request.postId = postId;
        request.browseDuration = duration;
        Log.i("check_netty", "recordViewingDuration::MessageSender: " + MainApplication.getInstance().getMessageSender());
        this.socketSender.uploadBrowseEvent(request);
    }

    // postInfoUrlAoList -> PostAoList
    public List<PostAo> getPostAoListByResponse(List<PostInfoUrlAo> postInfoAos){
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
        /*
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

    public List<PostAo> getPostAoByPostVo(List<PostVo> postVoList){
        if (postVoList == null || postVoList.isEmpty()){
            return new ArrayList<>();
        }
        List<PostInfoUrlAo> postInfoUrlAos = Optional.of(postVoList)
                .map(list -> {
                    // stream流转换
                    return list.stream()
                            .map(PostInfoUrlAo::getPostInfoUsrAoByVo)
                            .collect(Collectors.toList());
                })
                .orElse(new ArrayList<>());
        return getPostAoListByResponse(postInfoUrlAos);
    }
}
