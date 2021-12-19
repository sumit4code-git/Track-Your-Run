package com.example.runtracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runtracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runtracker.other.Constants.ACTION_STOP_SERVICE
import com.example.runtracker.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runtracker.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runtracker.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runtracker.other.Constants.NOTIFICATION_CHANNEL_Name
import com.example.runtracker.other.Constants.NOTIFICATION_ID
import com.example.runtracker.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.runtracker.other.TrackingUtility
import com.example.runtracker.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias polyline=MutableList<LatLng>
typealias polylines=MutableList<polyline>
@AndroidEntryPoint
class TrackingService : LifecycleService() {
    var isFirstRun=true
    var serviceKilled=false
    @Inject
    lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
    private val timeRunInSecond=MutableLiveData<Long>()
    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder

    lateinit var  curNotificationBuilder:NotificationCompat.Builder
    companion object{
        val timeRunInMillis=MutableLiveData<Long>()
        val isTracking= MutableLiveData<Boolean>()
        val pathPoints= MutableLiveData<polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder=baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient= FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationtracking(it)
        })
    }
//    To kill service When Requested
private fun killService(){
    serviceKilled=true
    isFirstRun=true
    pauseService()
    stopForeground(true)
    stopSelf()
}
//    everytime this class is called will run this method
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
        when(it.action){
            ACTION_START_OR_RESUME_SERVICE->{
                if(isFirstRun){
                    startForegroundService()
                    isFirstRun=false
                }
                else {
                    Timber.d("Resuming Service")
                    startTimer()
                }
            }
            ACTION_PAUSE_SERVICE->{
                Timber.d("Pause Service")
                pauseService()
            }
            ACTION_STOP_SERVICE->{
                Timber.d("Stop Service")
                killService()
            }

        }
        }
        return super.onStartCommand(intent, flags, startId)
    }
//    For creating notification channel and notification along with pending intent and action for main activity
    private fun startForegroundService(){
     startTimer()
    isTracking.postValue(true)
        val notificationManger=getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManger)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())
//    To update text in  notification Continuously
       timeRunInSecond.observe(this, Observer {
           if (!serviceKilled){
           val notification = curNotificationBuilder
               .setContentText(TrackingUtility.getFormattedStopWatch(it * 1000))
           notificationManger.notify(NOTIFICATION_ID, notification.build())
       }
    })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManger:NotificationManager) {
        val channel=NotificationChannel(NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_Name,
            IMPORTANCE_LOW
        )
      notificationManger.createNotificationChannel(channel)
    }
//    Stopwatch time Implementation
   private var isTimerEnabled=false
    private var lapTime=0L
    private  var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimestamp=0L
    private fun startTimer(){
        addEmptyPolylines()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
//                difference btw now and time started
                lapTime=System.currentTimeMillis()-timeStarted
//                post new lap Time
                timeRunInMillis.postValue(timeRun+lapTime)
                if(timeRunInMillis.value!!>=lastSecondTimestamp+1000L){
                    timeRunInSecond.postValue((timeRunInSecond.value!!+1))
                    lastSecondTimestamp+=1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun+=lapTime
        }
    }

//    Location methods
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSecond.postValue(0L)
        timeRunInMillis.postValue(0L)
    }
    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    @SuppressLint("MissingPermission")
    private  fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermission(this)){
                val request=LocationRequest().apply {
                    interval=LOCATION_UPDATE_INTERVAL
                    fastestInterval=FASTEST_LOCATION_INTERVAL
                    priority=PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback=object :LocationCallback() {
    override fun onLocationResult(p0: LocationResult) {
        super.onLocationResult(p0)
        if (isTracking.value!!) {
            p0?.locations?.let { locations ->
                for (location in locations) {
                    addPathPoint(location)
                    Timber.d("NEW_LOCATION: ${location.latitude},${location.longitude}")
                }
            }
        }
    }
}


    private fun addPathPoint(loation: Location){
        loation?.let{
            val pos=LatLng(loation.latitude,loation.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private  fun addEmptyPolylines()= pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))


    //To update Notification text continuously
    private  fun updateNotificationtracking(isTracking: Boolean){
        val notificationActionTest=if(isTracking)"Pause" else "Resume"
        val pendingIntent=if(isTracking){
            val pauseIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            val resumeIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//code to remove prev action and update it with new action in same notification
        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(curNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }
        if (!serviceKilled) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionTest, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }

    }
}