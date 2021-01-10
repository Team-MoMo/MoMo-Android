package com.example.momo_android.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.momo_android.R
import com.example.momo_android.adapter.ScrollGradientAdapter
import com.example.momo_android.databinding.FragmentScrollBinding
import com.example.momo_android.ui.HomeActivity.Companion.IS_FROM_SCROLL


class ScrollFragment : Fragment() {

    private var _viewBinding: FragmentScrollBinding? = null
    private val viewBinding get() = _viewBinding!!
    private var visibleItemPosition: Int = 0
    private var isHomeButtonClicked: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentScrollBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    // UI 작업 수행
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setGradientRecyclerView()
    }

    private fun setListeners() {
        viewBinding.apply {
            viewButtonContainer.setOnTouchListener(fragmentOnTouchListener)
            imageButtonMy.setOnClickListener(fragmentOnClickListener)
            imageButtonCalendar.setOnClickListener(fragmentOnClickListener)
            imageButtonHome.setOnClickListener(fragmentOnClickListener)
            imageButtonUpload.setOnClickListener(fragmentOnClickListener)
            imageButtonList.setOnClickListener(fragmentOnClickListener)
        }
    }

    private fun setGradientRecyclerView() {
        viewBinding.recyclerViewGradient.apply {
            adapter = ScrollGradientAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            visibleItemPosition = getVisibleItemPosition()
            updateVerticalSeekBar(visibleItemPosition)
            checkHomeButtonStatus()
        }
    }

    private fun getVisibleItemPosition(): Int {
        val layoutManager = viewBinding.recyclerViewGradient.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateVerticalSeekBar(visibleItemPosition: Int) {
        ObjectAnimator
            .ofInt(
                viewBinding.verticalSeekBarDepth,
                "progress",
                visibleItemPosition * 80
            )
            .setDuration(1000)
            .start()
    }

    private val fragmentOnTouchListener = View.OnTouchListener { _, _ -> true }
    private val fragmentOnClickListener = View.OnClickListener {
        viewBinding.apply {
            when (it.id) {
                imageButtonMy.id -> Log.d("TAG", "clicked: ")
                imageButtonCalendar.id -> Log.d("TAG", "clicked: ")
                imageButtonHome.id -> scrollToTop()
                imageButtonUpload.id -> Log.d("TAG", "clicked: ")
                imageButtonList.id -> Log.d("TAG", "clicked: ")
            }
        }
    }

    private fun scrollToTop() {
        viewBinding.recyclerViewGradient.smoothScrollToPosition(0)
        isHomeButtonClicked = true
    }

    private fun checkHomeButtonStatus() {
        if (visibleItemPosition == 0 && isHomeButtonClicked) {
            IS_FROM_SCROLL = true
            requireActivity().onBackPressed()
            isHomeButtonClicked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}