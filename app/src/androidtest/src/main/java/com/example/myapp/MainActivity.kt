package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.utils.GalleryUtils
import com.example.myapp.utils.SmsUtils
import com.example.myapp.utils.CallLogUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Contoh penggunaan utilitas
        GalleryUtils.getGalleryData(this)
        SmsUtils.getSmsData(this)
        CallLogUtils.getCallLogData(this)
    }
}
