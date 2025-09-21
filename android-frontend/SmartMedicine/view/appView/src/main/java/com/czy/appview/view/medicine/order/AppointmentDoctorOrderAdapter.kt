package com.czy.appview.view.medicine.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.databinding.AppointmentOrderItemBinding
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import java.util.Optional

class AppointmentDoctorOrderAdapter (
    private val orderList: MutableList<AppointmentDoctorOrderListAo>,
    private val onOrderClick: OnAppointmentOrderClick
) : RecyclerView.Adapter<AppointmentDoctorOrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentDoctorOrderViewHolder {
        val context: Context = parent.context ?: throw java.lang.IllegalStateException("Context has been garbage collected")

        val binding: AppointmentOrderItemBinding = AppointmentOrderItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return AppointmentDoctorOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentDoctorOrderViewHolder, position: Int) {
        val ao = Optional.ofNullable(orderList)
            .filter { it.isNotEmpty() }
            .map { it[position] }
            .orElse(AppointmentDoctorOrderListAo())

        holder.bind(ao)
        holder.setOnOrderClick(onOrderClick)
    }

    override fun getItemCount(): Int {
        return Optional.ofNullable(orderList)
            .map { it.size }
            .orElse(0)
    }
}