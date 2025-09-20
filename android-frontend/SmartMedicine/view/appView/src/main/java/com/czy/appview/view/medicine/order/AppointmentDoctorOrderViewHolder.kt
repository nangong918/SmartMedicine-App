package com.czy.appview.view.medicine.order

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.czy.appcore.utils.MoneyUtil
import com.czy.appview.databinding.AppointmentOrderItemBinding
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.image.ImageLoadUtil
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import com.czy.domain.constant.OrderStatusCalculator
import com.czy.domain.constant.UserOrderStatusEnum
import com.czy.domain.constant.medicine.AppointmentMerchantStatusEnum
import com.czy.domain.constant.purchase.OrderStatusEnum
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

        // 剩余数量 (取消)
        binding.tvRemain.visibility = View.GONE
//        binding.tvRemain.text = "剩余名额:" + ao.listVo?.remainCount
        // 费用 BigDecimal
        binding.tvPrice.text = MoneyUtil.formatToCurrency(
            MoneyUtil.stringToBigDecimal(ao.listVo?.cost?: "", 1)
        )

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
        binding.tvOrderTime.text = "下单时间:" + (ao.listVo?.approveDate?:"")

        // 剩余支付时间   todo 后端从redis中获取, 前端倒计时
        binding.tvRemainingPayTime

        // 订单状态: AppointmentMerchantStatus + UserOrderStatus -> 订单状态
        val appointmentMerchantStatus = AppointmentMerchantStatusEnum.getByCode(
            ao.listVo?.merchantStatus?: 0
        )
        val userOrderStatus = UserOrderStatusEnum.getByCode(
            ao.listVo?.customerStatus?: 0
        )
        val orderStatusEnum : OrderStatusEnum = OrderStatusCalculator.calculateOrderStatus(
            appointmentMerchantStatus,
            userOrderStatus
        )
        binding.tvOrderStatus.text = orderStatusEnum.getName()

        when (orderStatusEnum){
            OrderStatusEnum.NULL -> {
                binding.btnHandle1.visibility = View.GONE
                binding.btnHandle2.visibility = View.GONE
            }
            OrderStatusEnum.UNORDERED -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.GONE

                binding.btnHandle1.text = "再次预约"
            }
            OrderStatusEnum.WAITING_AUDIT -> {
                binding.btnHandle1.visibility = View.GONE
                binding.btnHandle2.visibility = View.GONE
            }
            OrderStatusEnum.WAIT_PAY -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "支付"
                binding.btnHandle2.text = "取消"
            }
            OrderStatusEnum.WAIT_USE -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "详情"
                binding.btnHandle2.text = "申请退款"
            }
            OrderStatusEnum.WAIT_COMMENT -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "评价"
                binding.btnHandle2.text = "再次预约"
            }
            OrderStatusEnum.REFUNDING -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "退款详情"
                binding.btnHandle2.text = "取消退款"
            }
            OrderStatusEnum.REFUND_SUCCESS -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "退款详情"
                binding.btnHandle2.text = "再次预约"
            }
            OrderStatusEnum.REFUND_FAILED -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.GONE

                binding.btnHandle1.text = "退款详情"
            }
            OrderStatusEnum.COMPLETED -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "详情"
                binding.btnHandle2.text = "再次预约"
            }
            OrderStatusEnum.CANCELED -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "详情"
                binding.btnHandle2.text = "再次预约"
            }
            OrderStatusEnum.EXPIRED -> {
                binding.btnHandle1.visibility = View.VISIBLE
                binding.btnHandle2.visibility = View.VISIBLE

                binding.btnHandle1.text = "详情"
                binding.btnHandle2.text = "再次预约"
            }
        }
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