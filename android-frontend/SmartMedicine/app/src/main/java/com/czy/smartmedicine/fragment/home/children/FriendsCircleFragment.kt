package com.czy.smartmedicine.fragment.home.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.baseutil.activity.BaseFragment
import com.czy.smartmedicine.databinding.FragmentFriendsCircleBinding


class FriendsCircleFragment : BaseFragment<FragmentFriendsCircleBinding>(
    FragmentFriendsCircleBinding::class.java
) {
    override fun getBinding(): FragmentFriendsCircleBinding {
        return FragmentFriendsCircleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}