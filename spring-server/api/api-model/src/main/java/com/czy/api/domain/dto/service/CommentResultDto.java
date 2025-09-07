package com.czy.api.domain.dto.service;

import exception.ExceptionEnums;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/7/28 10:50
 */
@Data
public class CommentResultDto {

    private boolean isSuccess = true;

    private ExceptionEnums exceptionEnums = null;
}
