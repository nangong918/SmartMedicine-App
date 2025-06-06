package service;

import com.czy.api.api.user_relationship.UserRelationshipService;
import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.constant.user_relationship.newUserGroup.HandleStatusEnum;
import com.czy.api.domain.ao.relationship.*;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.entity.event.Message;
import com.czy.user.UserServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = UserServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class ServiceTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Autowired
    private UserRelationshipService service;

    private AddUserAo getAddUserAo(int applyStatus){
        AddUserAo addUserAo = new AddUserAo();
        // 26
        addUserAo.applyAccount = "test_sender";
        // 27
        addUserAo.handlerAccount = "test_receiver";
        addUserAo.applyTime = System.currentTimeMillis();
        addUserAo.applyContent = "test_applyContent";
        addUserAo.source = 1;
        addUserAo.applyStatus = applyStatus;
        return addUserAo;
    }

    private HandleAddedMeAo getHandleAddedMeAo(int handleType){
        HandleAddedMeAo handleAddedMeAo = new HandleAddedMeAo();
        // 26
        handleAddedMeAo.applyAccount = "test_sender";
        // 27
        handleAddedMeAo.handlerAccount = "test_receiver";
        handleAddedMeAo.handleTime = System.currentTimeMillis();
        handleAddedMeAo.additionalContent = "test_back_additionalContent";
        handleAddedMeAo.handleType = handleType;
        return handleAddedMeAo;
    }

    // addUserFriend
    // 变量：AddUserAo的applyStatus(0:未申请 1:申请中 2:已处理)
    @Test
    public void addUserFriend() {
        AddUserAo ao = getAddUserAo(ApplyStatusEnum.APPLYING.code);
        boolean result = service.addUserFriend(ao);
        log.info("result:{}", result);
    }

    // handleAddedUser
    @Test
    public void handleAddedUser() {
        HandleAddedMeAo ao = getHandleAddedMeAo(HandleStatusEnum.AGREE.code);
        Message message = service.handleAddedUser(ao);
        log.info("message:{}", message);
    }

    // getFriendList
    @Test
    public void getFriendList() {
        String senderAccount = "test_sender";
        // 获取好友列表
        List<UserViewEntity> userViewEntities = service.getFriendList(senderAccount);
        System.out.println("userViewEntities: ");
        if (!CollectionUtils.isEmpty(userViewEntities)){
            userViewEntities.forEach(item -> {
                System.out.println("item = " + item.toJsonString());
            });
        }
    }

    // getAddMeRequestList
    @Test
    public void getAddMeRequestList() {
        String handlerAccount = "test_receiver";

        List<NewUserItemAo> newUserItemAos = service.getAddMeRequestList(handlerAccount);
        if (!CollectionUtils.isEmpty(newUserItemAos)){
            newUserItemAos.forEach(item -> {
                System.out.println("item = " + item.toJsonString());
            });
        }
    }
    
    // getHandleMyAddUserResponseList
    @Test
    public void getHandleMyAddUserResponseList() {
        String senderAccount = "test_sender";
        List<NewUserItemAo> newUserItemAos = service.getHandleMyAddUserResponseList(senderAccount);
        if (!CollectionUtils.isEmpty(newUserItemAos)){
            newUserItemAos.forEach(item -> {
                System.out.println("item = " + item.toJsonString());
            });
        }
    }
    
    // getMyFriendList
    @Test
    public void getMyFriendList() {
        String senderAccount = "test_sender";
        List<MyFriendItemAo> myFriendItemAos = service.getMyFriendList(senderAccount);
        if (!CollectionUtils.isEmpty(myFriendItemAos)){
            myFriendItemAos.forEach(item -> {
                System.out.println("item = " + item.toJsonString());
            });
        }
    }
    
    // searchFriend
    @Test
    public void searchFriend() {
        String applyAccount = "test_sender";
        // 模糊内容
        String handlerAccount = "eceive";
        List<SearchFriendApplyAo> searchFriendApplyAos = service.searchFriend(applyAccount, handlerAccount);
        if (!CollectionUtils.isEmpty(searchFriendApplyAos)){
            searchFriendApplyAos.forEach(item -> {
                System.out.println("item = " + item.toJsonString());
            });
        }
    }
}
