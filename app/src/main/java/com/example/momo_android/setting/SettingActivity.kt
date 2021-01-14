package com.example.momo_android.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.momo_android.databinding.ActivityListBinding
import com.example.momo_android.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initBackButton()

        isSwitchOn()
    }

    private fun isSwitchOn() {
        binding.switchLock.setOnCheckedChangeListener { _, onSwitch ->
            // 스위치가 켜지면
            if (onSwitch) {
                binding.imagebuttonResetting.visibility = View.VISIBLE
            }
            // 스위치가 꺼지면
            else {
                binding.imagebuttonResetting.visibility = View.INVISIBLE
            }
        }
    }

    private fun initBackButton() {
        binding.imagebuttonSettingBack.setOnClickListener {
            finish()
        }
    }
}