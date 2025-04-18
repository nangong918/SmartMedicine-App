package com.czy.message.mapper.mysql;



import com.czy.api.domain.entity.event.Session;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/12 22:21
 */
@Mapper
public interface SessionMapper {

    // add(Session session)
    int add(Session session);

    // delete(long id)
    int delete(long id);

    // deleteByUid(String uid)
    int deleteByUid(String uid);

    // updateState(long id, int state)
    int updateState(@Param("id") long id,@Param("state") int state);

    // Session getById(long id)
    Session getById(long id);

    // Session getByUid(String uid)
    Session getByUid(String uid);

    // List<Session> findAll()
    List<Session> findAll();
}
