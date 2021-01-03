package com.example.momo_android.list

import android.graphics.drawable.Drawable

data class ListData (
        val emotionImg : Drawable?,
        val emotionText : String,
        val date : String,
        val day : String,
        val depth : String,
        val sentence : String,
        val writer : String,
        val bookTitle : String,
        val publisher : String,
        var diaryContent : String
    )