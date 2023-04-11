package com.ziro.bullet.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager


abstract class BaseFragment(@LayoutRes private val layout: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDependencies()
        setupObservers()
        setupView()
    }

    abstract fun setupDependencies()

    abstract fun setupObservers()

    abstract fun setupView()

    override fun onDestroyView() {
        super.onDestroyView()
    }

    open fun getLinearLayoutManger(orientation: Int): LinearLayoutManager =
        LinearLayoutManager(requireContext(), orientation, false)

}