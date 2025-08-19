package com.czy.medicine.controller;

import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.AppointmentDoctorRequest;
import com.czy.api.domain.dto.http.request.GetUserAppointmentRecordRequest;
import com.czy.api.domain.dto.http.request.GetRegisterAppointmentListRequest;
import com.czy.api.domain.dto.http.response.GetAllRegisterAppointmentDateResponse;
import com.czy.api.domain.dto.http.response.GetRegisterAppointmentListResponse;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.czy.medicine.service.RegisterAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 14:23
 * 本来需要pay-service，但是为了内存只能将pay-service和purchase-service合并
 * todo 1.虚拟数据导入脚本（1.根据当前时间生成4天，2.每天至少生成5条 3.生成地址定位为：广东-深圳-南山）
 *      2.获取可预约列表，获取所有预约时间：mysql查数据查询测试
 *      3.user预约：mysql插入数据测试（redis缓存 + mysql避免超卖问题）
 *      4.purchase的支付服务；订单生成，超时未支付关闭订单；商户状态/订单状态变化
 *      5.获取user预约订单列表
 *      6.获取订单详情
 *      7.取消预约 + 退款
 *      8.再次预约
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(MedicineConstant.RegisterAppointment_CONTROLLER)
public class RegisterAppointmentController {

    private final RegisterAppointmentService registerAppointmentService;

    /// 查：获取

    // 获取可预约列表
    @PostMapping("/getList")
    public BaseResponse<GetRegisterAppointmentListResponse>
    getList(@Validated @RequestBody GetRegisterAppointmentListRequest request){
        // 参数校验 （此处ao中部分参数因为复用没写校验所以需要单独校验逻辑）
        RegisterAppointmentPageVo pageVo = registerAppointmentService.getPage(
                request.getRequestAo()
        );

        GetRegisterAppointmentListResponse response = new GetRegisterAppointmentListResponse();
        response.setPageVo(pageVo);
        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 获取所有预约时间
    @PostMapping("/getAllDate")
    public BaseResponse<GetAllRegisterAppointmentDateResponse>
    getAllDate(@Validated @RequestBody GetRegisterAppointmentListRequest request) {
        List<RegisterAppointmentDataVo> dataVos = registerAppointmentService.getDataVoList(
                request.getRequestAo()
        );

        GetAllRegisterAppointmentDateResponse response = new GetAllRegisterAppointmentDateResponse();

        response.setDataVos(dataVos);

        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 获取user预约订单列表
    @PostMapping("/getAppointmentRecord")
    public BaseResponse<Object>
    getAppointmentRecord
    (@Validated @RequestBody GetUserAppointmentRecordRequest request){
        return null;
    }


    /// 增：预约
    @PostMapping("/appointment")
    public BaseResponse<Object> appointment
    (@Validated @RequestBody AppointmentDoctorRequest request){
        return null;
    }

    /// 改（删）：取消预约
    @PostMapping("/cancel")
    public BaseResponse<Object> cancelAppointment
    (@Validated @RequestBody AppointmentDoctorRequest request){
        return null;
    }
}
