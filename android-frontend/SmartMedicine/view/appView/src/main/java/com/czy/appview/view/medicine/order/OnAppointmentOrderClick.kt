package com.czy.appview.view.medicine.order

interface OnAppointmentOrderClick {
    fun onBaseCardClick(position: Int, merchantId: Long?, orderId: Long?)
    fun onButton1Click(position: Int, merchantId: Long?, orderId: Long?)
    fun onButton2Click(position: Int, merchantId: Long?, orderId: Long?)
}