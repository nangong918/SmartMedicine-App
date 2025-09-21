package com.czy.appview.view.medicine.appointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.databinding.AppointmentMerchantItemBinding
import com.czy.domain.OnPositionItemClick
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo
import java.util.Optional

class AppointmentMerchantAdapter(
    private val appointmentList: MutableList<RegisterAppointmentDoctorCardAo>,
    private val onPositionItemClick: OnPositionItemClick
) : RecyclerView.Adapter<AppointmentMerchantViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentMerchantViewHolder {
        val context: Context = parent.context ?: throw java.lang.IllegalStateException("Context has been garbage collected")

        val binding: AppointmentMerchantItemBinding = AppointmentMerchantItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return AppointmentMerchantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentMerchantViewHolder, position: Int) {
        val ao = Optional.ofNullable(appointmentList)
            .filter { it.isNotEmpty() }
            .map { it[position] }
            .orElse(RegisterAppointmentDoctorCardAo())

        holder.bind(ao)
        holder.setPositionClick(onPositionItemClick)
    }


    override fun getItemCount(): Int {
        return Optional.ofNullable(appointmentList)
            .map { it.size }
            .orElse(0)
    }

}