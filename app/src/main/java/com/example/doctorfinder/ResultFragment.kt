package com.example.doctorfinder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import kotlin.random.Random

class ResultFragment : Fragment() {

    private val channelId = "example_channel_id"
    private val notificationId = 101
    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val receivedText = arguments?.getString("EXTRA_TEXT")
        val receivedNumber = arguments?.getString("EXTRA_NUMBER")

        val nameArray = resources.getStringArray(R.array.name_list)
        val randomName = nameArray[Random.nextInt(nameArray.size)]

        val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
        val result = "Hello: $receivedText\nYour Doctor is : $randomName"
        resultTextView.text = result

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
            } else {
                // Permission granted, show notification
                createNotificationChannel()
                showNotification(result)
            }
        } else {
            createNotificationChannel()
            showNotification(result)
        }

        return view
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Find Doctor"
            val descriptionText = "Doctor Service Request"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(message: String?) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("New Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setLights(Color.RED, 1000, 1000)
            .setVibrate(longArrayOf(0, 500, 1000, 500))

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, builder.build())
            } else {
                // Handle case where vibration permission is not granted
                builder.setVibrate(null)
                notify(notificationId, builder.build())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val receivedText = arguments?.getString("EXTRA_TEXT")
                val receivedNumber = arguments?.getString("EXTRA_NUMBER")
                val result = "Text: $receivedText\nNumber: $receivedNumber"
                showNotification(result)
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}
