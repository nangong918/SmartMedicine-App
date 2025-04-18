package com.czy.message.service;



import com.czy.api.api.message.SessionService;
import com.czy.api.domain.entity.event.Session;
import com.czy.message.mapper.mysql.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/12 22:18
 */
@Slf4j
@RequiredArgsConstructor
@Component
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;

    @Override
    public void add(Session session) {
        sessionMapper.add(session);
    }

    @Override
    public void delete(long id) {
        sessionMapper.delete(id);
    }

    @Override
    public void deleteByUid(String uid) {
        sessionMapper.deleteByUid(uid);
    }

    @Override
    public void updateState(long id, int state) {
        sessionMapper.updateState(id, state);
    }

    @Override
    public Session getById(long id) {
        return sessionMapper.getById(id);
    }

    @Override
    public Session getByUid(String uid) {
        return sessionMapper.getByUid(uid);
    }

    @Override
    public List<Session> findAll() {
        return sessionMapper.findAll();
    }
}
