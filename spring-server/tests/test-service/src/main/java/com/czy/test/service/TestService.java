package com.czy.test.service;

import com.utils.minio.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/8/12 17:47
 */
@RequiredArgsConstructor
@Service
public class TestService {

    private final OssService ossService;

}
