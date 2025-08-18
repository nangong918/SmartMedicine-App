package com.czy.medicine.controller;

import com.czy.api.constant.BaseParentEnum;
import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.response.DepartmentDictResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/8/18 13:48
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(MedicineConstant.Knowledge_CONTROLLER)
public class KnowledgeController {

    @GetMapping("/getDepartmentDict")
    public BaseResponse<DepartmentDictResponse> getDepartmentDict() {
        DepartmentDictResponse response = new DepartmentDictResponse();

        List<BaseParentEnum> departmentDict = new ArrayList<>();
        for (DepartmentEnum dEnum : DepartmentEnum.values()){
            Optional.ofNullable(dEnum)
                            .map(DepartmentEnum::getBaseParentEnum)
                            .ifPresent(departmentDict::add);
        }

        response.setDepartmentDict(departmentDict);
        return BaseResponse.getResponseEntitySuccess(response);
    }

}
