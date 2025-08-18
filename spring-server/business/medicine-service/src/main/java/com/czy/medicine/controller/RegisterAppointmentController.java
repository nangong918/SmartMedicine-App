package com.czy.medicine.controller;

import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.GetRegisterAppointmentListRequest;
import com.czy.api.exception.CommonExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 13225
 * @date 2025/8/18 14:23
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(MedicineConstant.RegisterAppointment_CONTROLLER)
public class RegisterAppointmentController {

    @PostMapping("/getList")
    public BaseResponse<Object> getList(@Validated @RequestBody GetRegisterAppointmentListRequest request){
        // 参数校验 （此处ao中部分参数因为复用没写校验所以需要单独校验逻辑）
        // registerTime是否可用 String -> Date
        Date registerTime = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            registerTime = formatter.parse(request.getRequestAo().getRegisterTime());
        } catch (Exception e){
            log.error("获取挂号列表失败，时间转换异常: {}", request.getRequestAo().getRegisterTime());
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        // todo
        return null;
    }

}
