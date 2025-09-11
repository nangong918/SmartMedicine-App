package com.czy.appview.view.medicine.order

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.databinding.AppointmentOrderItemBinding
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.image.ImageLoadUtil
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentDoctorOrderViewHolder (
    private val binding: AppointmentOrderItemBinding
) : RecyclerView.ViewHolder(
    binding.root)
{

    var ao : AppointmentDoctorOrderListAo? = null

    @SuppressLint("SetTextI18n")
    fun bind(ao : AppointmentDoctorOrderListAo){
        this.ao = ao

        /// 医生视图
        ao.listVo?.doctorVo?.let {
            dvo ->
            // 头像
            ImageLoadUtil.loadImageViewByResource(
                dvo.doctorAvatarFileAo?.fileUrl,
                binding.imvgAvatar
            )
            // 姓名
            binding.tvDoctorName.text = dvo.doctorName?: ""
        }

        /// 医院视图
        ao.listVo?.hospitalAo?.let {
            hAo ->
            binding.tvHospitalName.text = hAo.hospitalVo?.name?: ""
        }

        // 剩余数量
        binding.tvRemain.text = ao.listVo?.cost?: ""
        // 费用 BigDecimal
        binding.tvPrice.text = ao.listVo?.cost?: ""

        // 预约时间区间
        ao.listVo?.let {
            vo ->
            val beginTime : LocalDateTime = DateUtils.getLocalDateTime(
                vo.beginDate,
                DateUtils.yyyyMMddHHmmss
            )
            val endTime : LocalDateTime = DateUtils.getLocalDateTime(
                vo.endDate,
                DateUtils.yyyyMMddHHmmss
            )
            val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val beginTimeStr: String = beginTime.format(
                formatter
            ) ?: "";
            val endTimeStr: String = endTime.format(
                formatter
            ) ?: "";
            binding.tvDuration.text = "$beginTimeStr - $endTimeStr"
        }

        // 用户预约之后审批结果时间
        binding.tvOrderTime.text = ao.listVo?.approveDate?:""

        // 剩余支付时间   todo 后端从redis中获取, 前端倒计时
        binding.tvRemainingPayTime

        // 订单状态: AppointmentMerchantStatus + UserOrderStatus -> 订单状态 todo 从后端代码复制
    }

    fun setOnOrderClick(onOrderClick: OnAppointmentOrderClick) {
        binding.root.setOnClickListener{
            onOrderClick.onBaseCardClick(
                adapterPosition,
                ao?.doctorMerchantId,
                ao?.orderId
            )
        }

        binding.btnHandle1.setOnClickListener{
            onOrderClick.onButton1Click(
                adapterPosition,
                ao?.doctorMerchantId,
                ao?.orderId
            )
        }

        binding.btnHandle2.setOnClickListener{
            onOrderClick.onButton2Click(
                adapterPosition,
                ao?.doctorMerchantId,
                ao?.orderId
            )
        }
    }
}