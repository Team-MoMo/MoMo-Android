package com.example.momo_android.util

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.text.Layout
import android.util.Log
import android.util.Xml
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.momo_android.R
import org.xmlpull.v1.XmlPullParser

/* 확장함수 */

/* show Toast */
fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

/* visibility */
fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInVisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

/* custom Toast */
/* 사용할 떄는 customToast(layoutInflater, R.layout.toast_ready, this) 이렇게 사용하면 돼요! */
fun customToast(layoutInflater: LayoutInflater, toastLayout: XmlPullParser, context: Context) {
    val customToast = layoutInflater.inflate(toastLayout, null)
    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
    toast.view = customToast
    toast.show()
}

/*키보드 숨기기*/
fun EditText.showKeyboard() {
    if (requestFocus()) {
        // edittext에 초점이 맞춰지면 키보드 올라옴
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        setSelection(text.length)
    }
}

fun EditText.unshowKeyboard() {
    if (requestFocus()) {
        // edittext에 초점이 맞춰지면 키보드 내려감
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
        setSelection(text.length)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


/* 월을 2자리수로 변환해주는 함수 (6 -> 06)*/
fun getMonth(month: Int): String {
    return if (month in 1..9) {
        "0$month"
    } else {
        "$month"
    }
}

/* 일을 2자리수로 변환해주는 함수 (6 -> 06)*/
fun getDate(date: Int): String {
    return if (date in 1..9) {
        "0$date"
    } else {
        "$date"
    }
}

fun ViewPager2.setCustomCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag =
            (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator?) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator?) { /* Ignored */
        }

        override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}

