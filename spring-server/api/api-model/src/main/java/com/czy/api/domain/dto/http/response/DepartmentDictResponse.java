package com.czy.api.domain.dto.http.response;

import com.czy.api.constant.BaseParentEnum;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 13:35
 */
@Data
public class DepartmentDictResponse {

    public List<BaseParentEnum> departmentDict;

    public DepartmentDictResponse() {
    }

    public DepartmentDictResponse(List<BaseParentEnum> departmentDict) {
        this.departmentDict = departmentDict;
    }
}
