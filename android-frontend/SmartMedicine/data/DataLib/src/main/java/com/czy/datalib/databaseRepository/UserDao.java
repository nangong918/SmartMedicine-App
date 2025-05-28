package com.czy.datalib.databaseRepository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.czy.dal.Do.UserDo;

import java.util.List;

@Dao
public interface UserDao {

    /**
     * 插入用户
     * @param users 用户
     * @return  返回是否成功
     */
    @Insert
    List<Long> insert(UserDo... users);

    /**
     * 删除用户
     * @param users 用户
     * @return 返回是否成功
     */
    @Delete
    Integer delete(UserDo... users);

    /**
     * 删除全部user
     * @return 返回是否成功
     */
    @Query("DELETE FROM user")
    Integer deleteAll();

    /**
     * 根据账号删除用户
     * @param account   账号
     * @Return 操作某个数据，不返回
     */
    @Query("DELETE FROM user WHERE account = :account")
    void deleteByAccount(String account);

    /**
     * 更新用户
     * @param users
     * @return  返回是否成功
     */
    @Update
    Integer update(UserDo... users);

    /**
     * 查询所有用户
     * @return  返回所有用户
     */
    @Query("SELECT * FROM user")
    List<UserDo> queryAll();

    /**
     * 根据账号查询用户 ; 被限制为1个
     * @param account   账号
     * @return  返回
     */
    @Query("SELECT * FROM user WHERE account = :account ORDER BY uid DESC limit 1")
    UserDo queryByName(String account);

    /**
     * 根据uid查询用户
     * @param uid   uid
     * @return  返回
     */
    @Query("SELECT * FROM user WHERE uid = :uid LIMIT 1")
    UserDo queryById(int uid);
}
