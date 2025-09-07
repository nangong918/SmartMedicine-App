package com.czy.smartmedicine.fragment.home.children

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.baseutil.activity.BaseFragment
import com.czy.smartmedicine.R
import com.czy.smartmedicine.databinding.FragmentFollowBinding


class FollowFragment : BaseFragment<FragmentFollowBinding>(
    FragmentFollowBinding::class.java
) {
    override fun getBinding(): FragmentFollowBinding {
        return FragmentFollowBinding.inflate(layoutInflater)
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