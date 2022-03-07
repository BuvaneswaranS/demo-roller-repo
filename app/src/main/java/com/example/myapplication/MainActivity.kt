package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.random.Random.Default.nextInt

lateinit var  click: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        click= findViewById(R.id.click_image)
        click.setOnClickListener{
            rollDice()
        }
        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))
    }

    private fun rollDice(){
        val image: ImageView = findViewById(R.id.dice_image)
        val result = Random().nextInt(6) + 1
        val notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotification()
        notificationManager.sendNotification(result,this)
        image.setImageResource(R.drawable.dice_1)
        val ani: ObjectAnimator = ObjectAnimator.ofFloat(image,View.ROTATION, -360f, 0f)
        ani.duration = 1000
        ani.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                click.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                click.isEnabled = true
                image.setImageResource(when(result){
                    1 -> R.drawable.dice_1
                    2 -> R.drawable.dice_2
                    3 -> R.drawable.dice_3
                    4 -> R.drawable.dice_4
                    5 -> R.drawable.dice_5
                    6 -> R.drawable.dice_6
                    else -> R.drawable.empty_dice
                })
            }        })
        ani.start()

    }

    private val NOTIFICATION_ID = 0

    fun NotificationManager.sendNotification(value: Int, appContext: Context){

        val contentIntent = Intent(appContext,MainActivity::class.java)
        val pendIntent = PendingIntent.getActivity(appContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val shDiceImage = BitmapFactory.decodeResource(appContext.resources, R.drawable.ic_launcher_foreground)

        val BidPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(shDiceImage)
            .bigLargeIcon(null)

        val builder = NotificationCompat.Builder(appContext,appContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.dice_1)
            .setContentTitle(appContext.getString(R.string.notification_Name))
            .setContentText("Dice Rolled : ${value}")
            .setContentIntent(pendIntent)
            .setStyle(BidPicStyle)
            .setLargeIcon(shDiceImage)
            .setAutoCancel(true)

        notify(NOTIFICATION_ID, builder.build())
    }

    fun NotificationManager.cancelNotification(){
        cancelAll()
    }


    private fun createChannel(chaId : String, ChName: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = NotificationChannel(chaId, ChName, NotificationManager.IMPORTANCE_HIGH)

            notiChannel.enableLights(true)
            notiChannel.enableVibration(true)
            notiChannel.description = "Dice Rolled"
            notiChannel.lightColor = Color.RED

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(notiChannel)
        }
    }


}

