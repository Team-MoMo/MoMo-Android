package com.momo.momo_android.home.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.momo.momo_android.R
import com.momo.momo_android.databinding.FragmentHomeBinding
import com.momo.momo_android.diary.ui.DiaryActivity
import com.momo.momo_android.home.data.ResponseDiaryList
import com.momo.momo_android.home.ui.ScrollFragment.Companion.IS_EDITED
import com.momo.momo_android.list.ui.ListActivity
import com.momo.momo_android.network.RequestToServer
import com.momo.momo_android.setting.ui.SettingActivity
import com.momo.momo_android.upload.ui.UploadFeelingActivity
import com.momo.momo_android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private var isDay = true
    private var diaryId = 0
    private var diaryDepth = 0

    private lateinit var currentYear: String
    private lateinit var currentMonth: String
    private lateinit var currentDate: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    // UI 작업 수행
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setCurrentDate()
        setDayNightStatus()
        getServerDiaryData()
        setOnBackPressedCallBack()
    }

    override fun onResume() {
        super.onResume()
        updateEditedData()
        if (!onBackPressedCallback.isEnabled) {
            onBackPressedCallback.isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.isEnabled = false
    }

    private fun setListeners() {
        binding.apply {
            fragmentOnClickListener.let {
                imageButtonMy.setOnClickListener(it)
                buttonUpload.setOnClickListener(it)
                buttonShowFull.setOnClickListener(it)
                imageButtonUpload.setOnClickListener(it)
                imageButtonList.setOnClickListener(it)
            }
        }
    }

    private fun setCurrentDate() {
        getCurrentDate().apply {
            currentYear = this[0]
            currentMonth = this[1]
            currentDate = this[2]
            binding.textViewDate.text =
                "${currentYear}년\n${currentMonth}월 ${currentDate}일 ${this[3]}"
        }
    }

    private fun setOnBackPressedCallBack() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as HomeActivity).showFinishToast()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun updateEditedData() {
        if (IS_EDITED) {
            setCurrentDate()
            setDayNightStatus()
            getServerDiaryData()
            IS_EDITED = false
        }
    }

    private fun setDayNightStatus() {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..18 -> setDayView()
            else -> setNightView()
        }
        setLoadingViewBackground()
    }

    private fun setDayView() {
        isDay = true
        binding.apply {
            constraintLayout.setBackgroundResource(R.drawable.gradient_home_day)
            imageViewSky.setImageResource(R.drawable.day_cloud)
            textViewDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_3))
            imageButtonMy.setImageResource(R.drawable.btn_ic_my_blue)
            textViewEmotion.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_2))
            imageViewLogo.setImageResource(R.drawable.ic_depth)
            textViewDepth.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_1))
            textViewQuotation.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_4))
            textViewAuthor.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_3))
            textViewTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_3))
            textViewPublisher.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_5_publish))
            viewLine.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.line_light_gray))
            textViewDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_4))
            textViewDiaryEmpty.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_4))
            buttonUpload.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_1))
            buttonUpload.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_7)
            buttonShowFull.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_1))
            buttonShowFull.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_7)
            imageViewSea.setImageResource(R.drawable.day_sea)
        }
    }

    private fun setNightView() {
        isDay = false
        binding.apply {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_blue_grey))
            imageViewSky.setImageResource(R.drawable.night_star)
            textViewDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            imageButtonMy.setImageResource(R.drawable.btn_ic_my)
            textViewEmotion.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_4))
            imageViewLogo.setImageResource(R.drawable.ic_depth_white)
            textViewDepth.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            textViewQuotation.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            textViewAuthor.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_4))
            textViewTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_4))
            textViewPublisher.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_5_publish))
            viewLine.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_3))
            textViewDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            textViewDiaryEmpty.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            buttonUpload.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            buttonUpload.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_1)
            buttonShowFull.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_7))
            buttonShowFull.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_1)
            imageViewSea.setImageResource(R.drawable.night_sea)
        }
    }

    private fun getServerDiaryData() {
        val call: Call<ResponseDiaryList> = RequestToServer.service.getHomeDiaryList(
            SharedPreferenceController.getAccessToken(requireContext()),
            SharedPreferenceController.getUserId(requireContext()),
            "filter",
            currentYear.toInt(),
            currentMonth.toInt(),
            currentDate.toInt()
        )
        call.enqueue(object : Callback<ResponseDiaryList> {
            override fun onFailure(call: Call<ResponseDiaryList>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<ResponseDiaryList>,
                response: Response<ResponseDiaryList>
            ) {
                when (response.isSuccessful) {
                    true -> setDiaryView(response.body()!!.data)
                    false -> handleResponseStatusCode(response.code())
                }
            }
        })
    }

    private fun setDiaryView(diaryList: List<ResponseDiaryList.Data>) {
        when (diaryList.size) {
            0 -> {
                DIARY_STATUS = false
                setEmptyVisibility()
            }
            else -> {
                DIARY_STATUS = true
                setDiaryVisibility()
                setDiaryViewData(diaryList[0])
            }
        }
        fadeOutLoadingView()
    }

    private fun setDiaryViewData(diaryData: ResponseDiaryList.Data) {
        diaryId = diaryData.id
        setBookDiaryData(diaryData)
        setEmotionData(diaryData.emotionId, isDay)
        binding.textViewDepth.text = getDepthString(diaryData.depth, requireContext())
    }

    private fun handleResponseStatusCode(responseCode: Int) {
        when (responseCode) {
            400 -> requireContext().showToast("일기 전체 조회 실패 - 필요한 값이 없습니다.")
            500 -> requireContext().showToast("일기 전체 조회 실패 - 서버 내부 에러")
            else -> requireContext().showToast("일기 전체 조회 실패 - 예외 상")
        }
        setEmptyVisibility()
    }

    private fun fadeOutLoadingView() {
        binding.viewLoading.apply {
            alpha = 1f
            animate()
                .alpha(0f)
                .setDuration(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.viewLoading.visibility = View.GONE
                    }
                })
        }
    }

    private fun setLoadingViewBackground() {
        binding.viewLoading.apply {
            when (isDay) {
                true -> setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                false -> setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_blue_grey))
            }
            visibility = View.VISIBLE
        }
    }

    private fun setEmptyVisibility() {
        binding.apply {
            textViewDiaryEmpty.visibility = TextView.VISIBLE
            buttonUpload.visibility = Button.VISIBLE

            imageViewEmotion.visibility = ImageView.GONE
            textViewEmotion.visibility = TextView.GONE
            imageViewLogo.visibility = ImageView.GONE
            textViewDepth.visibility = TextView.GONE
            textViewQuotation.visibility = TextView.GONE
            textViewAuthor.visibility = TextView.GONE
            textViewTitle.visibility = TextView.GONE
            textViewPublisher.visibility = TextView.GONE
            viewLine.visibility = View.GONE
            textViewDiary.visibility = TextView.GONE
            buttonShowFull.visibility = Button.GONE
            imageButtonUpload.visibility = ImageButton.GONE
        }
    }

    private fun setDiaryVisibility() {
        binding.apply {
            textViewDiaryEmpty.visibility = TextView.GONE
            buttonUpload.visibility = Button.GONE

            imageViewEmotion.visibility = ImageView.VISIBLE
            textViewEmotion.visibility = TextView.VISIBLE
            imageViewLogo.visibility = ImageView.VISIBLE
            textViewDepth.visibility = TextView.VISIBLE
            textViewQuotation.visibility = TextView.VISIBLE
            textViewAuthor.visibility = TextView.VISIBLE
            textViewTitle.visibility = TextView.VISIBLE
            textViewPublisher.visibility = TextView.VISIBLE
            viewLine.visibility = View.VISIBLE
            textViewDiary.visibility = TextView.VISIBLE
            buttonShowFull.visibility = Button.VISIBLE
            imageButtonUpload.visibility = ImageButton.VISIBLE
        }
    }

    private fun setEmotionData(emotionId: Int, isDay: Boolean) {
        binding.textViewEmotion.text = getEmotionString(emotionId, requireContext())
        binding.imageViewEmotion.apply {
            setImageResource(getEmotionWhite(emotionId))
            when (isDay) {
                true -> setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_2))
            }
        }
    }

    private fun setBookDiaryData(data: ResponseDiaryList.Data) {
        binding.apply {
            textViewQuotation.text = data.sentence.contents
            textViewAuthor.text = data.sentence.writer
            textViewTitle.text = "<${data.sentence.bookName}>"
            textViewPublisher.text = "(${data.sentence.publisher})"
            textViewDiary.text = data.contents
        }
    }

    private val fragmentOnClickListener = View.OnClickListener {
        binding.apply {
            when (it.id) {
                imageButtonMy.id -> setIntentToSettingActivity()
                buttonShowFull.id -> setIntentToDiaryActivity()
                buttonUpload.id -> setIntentToUploadActivity()
                imageButtonUpload.id -> setIntentToUploadActivity()
                imageButtonList.id -> setIntentToListActivity()
            }
        }
    }

    private fun setIntentToSettingActivity() {
        val intent = Intent(requireContext(), SettingActivity::class.java)
        startActivity(intent)
    }

    private fun setIntentToDiaryActivity() {
        val intent = Intent(requireContext(), DiaryActivity::class.java)
        intent.putExtra("diaryId", diaryId)
        intent.putExtra("diaryDepth", diaryDepth)
        startActivity(intent)
    }

    private fun setIntentToUploadActivity() {
        val intent = Intent(requireContext(), UploadFeelingActivity::class.java)
        intent.putExtra("diaryStatus", DIARY_STATUS)
        startActivity(intent)
    }

    private fun setIntentToListActivity() {
        val intent = Intent(requireContext(), ListActivity::class.java)
        intent.putExtra("year", currentYear)
        intent.putExtra("month", currentMonth)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onBackPressedCallback.remove()
    }

    companion object {
        var DIARY_STATUS = true
    }
}
