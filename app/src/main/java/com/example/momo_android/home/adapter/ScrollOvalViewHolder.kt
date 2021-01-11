package com.example.momo_android.home.adapter

import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.momo_android.databinding.ItemScrollOvalBinding
import com.example.momo_android.home.data.ResponseDiaryList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class ScrollOvalViewHolder(
    private val viewBinding: ItemScrollOvalBinding
) : RecyclerView.ViewHolder(viewBinding.root) {

    private var itemDistance by Delegates.notNull<Float>()
    private val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics


    fun onBind(xPosition: Int, diaryData: ResponseDiaryList.Data) {
        getItemAreaWidth()
        setOvalXPosition(9)
        setDiaryData(diaryData)
    }

    fun onEmptyBind() {
        viewBinding.apply {
            imageButtonOval.visibility = ImageButton.INVISIBLE
            textViewDate.visibility = TextView.INVISIBLE
            textViewCategory.visibility = TextView.INVISIBLE
        }
    }

    private fun getItemAreaWidth() {
        // {(디바이스 너비 - 아이템 너비 - 좌우여백) / (한 행당 아이템 최대 개수 - 1)} * 아이템 번호 + 좌측 여백
        val deviceWidthPixels = displayMetrics.widthPixels
        val itemWidthPixels = ITEM_SIZE * displayMetrics.density
        val horizontalMarginPixels = HORIZONTAL_MARGIN * 2 * displayMetrics.density
        itemDistance =
            (deviceWidthPixels.toFloat() - itemWidthPixels - horizontalMarginPixels) / (ITEM_AMOUNT - 1)
    }

    private fun setOvalXPosition(xPosition: Int) {
        val xPosition: Int = Random().nextInt(ITEM_AMOUNT)
        val leftMargin = ((itemDistance * xPosition) + (HORIZONTAL_MARGIN * displayMetrics.density))
        val layoutParams = viewBinding.imageButtonOval.layoutParams as ConstraintLayout.LayoutParams

        layoutParams.marginStart = leftMargin.toInt()
        viewBinding.imageButtonOval.layoutParams = layoutParams
    }

    private fun setDiaryData(diaryData: ResponseDiaryList.Data) {
        convertUpdatedAtToDate(diaryData.updatedAt)
        viewBinding.textViewCategory.text = diaryData.emotion.name
    }

    private fun convertUpdatedAtToDate(updatedAt: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.sss'Z'", Locale.KOREAN)
        val parsedDate = dateFormat.parse(updatedAt)
        val diaryDate = SimpleDateFormat("MM/dd", Locale.KOREA).format(parsedDate!!)
        viewBinding.textViewDate.text = diaryDate
    }

    companion object {
        const val ITEM_SIZE = 80
        const val HORIZONTAL_MARGIN = 36
        const val ITEM_AMOUNT = 10
    }
}
