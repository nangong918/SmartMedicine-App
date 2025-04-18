package com.czy.api.api.message;






import com.czy.api.domain.entity.event.Session;

import java.util.List;

/**
 * 存储连接信息，便于查看用户的链接信息
 */
public interface SessionService {

	void add(Session session);

	void delete(long id);

	void deleteByUid(String uid);

	void updateState(long id, int state);

	// Session getById(long id)
	Session getById(long id);

	// Session getById(long id)
	Session getByUid(String uid);

	List<Session> findAll();
}
