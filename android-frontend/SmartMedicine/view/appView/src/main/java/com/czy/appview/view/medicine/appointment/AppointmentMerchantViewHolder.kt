package com.czy.appview.view.medicine.appointment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.databinding.AppointmentMerchantItemBinding
import com.czy.baseutil.image.ImageLoadUtil
import com.czy.domain.OnPositionItemClick
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo

class AppointmentMerchantViewHolder(
    private val binding: AppointmentMerchantItemBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    @SuppressLint("SetTextI18n")
    fun bind(ao: RegisterAppointmentDoctorCardAo) {
        ao.vo?.let {
            vo ->
            /// 医生视图
            vo.doctorVo?.let {
                dVo ->
                // 头像
                dVo.doctorAvatarFileAo?.let {
                    ImageLoadUtil.loadImageViewByResource(
                        it.fileUrl ?:"",
                        binding.imvgAvatar
                    )
                }
                // info
                binding.tvDoctorName.text = dVo.doctorName ?: ""
            }
            /// 医院视图
            vo.hospitalAo?.hospitalVo?.let {
                hVo ->
                binding.tvHospitalName.text = hVo.name ?: ""
            }

            // 信息1 (doctor职称)
            binding.tvPurchaseInfo1.text = vo.doctorVo?.doctorTitle ?: ""
            // 信息2 (hospital level)
            binding.tvPurchaseInfo2.text = vo.hospitalAo?.hospitalVo?.level ?: ""

            // 费用 BigDecimal
            binding.tvMoney.text = ("费用" + vo.cost)
            // 剩余数量
            binding.tvRemain.text = "剩余数量: " + vo.remainCount?.toString()

            // 预约时间区间
            binding.tvDuration.text = vo.beginDate + " - " + vo.endDate
        }

        binding.btnHandle.text = "预约"
    }

    fun setPositionClick(onPositionItemClick: OnPositionItemClick) {
        binding.btnHandle.setOnClickListener{
            onPositionItemClick.onPositionItemClick(adapterPosition)
        }
    }


}