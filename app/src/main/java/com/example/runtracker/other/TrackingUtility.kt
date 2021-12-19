package com.example.runtracker.other

import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

object TrackingUtility {
    fun hasLocationPermission(context: Context)=
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
//    To get Formatted Time
fun getFormattedStopWatch(ms:Long,includeMillis:Boolean=false):String{
    var millisecond=ms;
    var hours=TimeUnit.MILLISECONDS.toHours((millisecond))
    millisecond-=TimeUnit.HOURS.toMillis(hours)

    val minutes=TimeUnit.MILLISECONDS.toMinutes(millisecond)
    millisecond-=TimeUnit.MINUTES.toMillis(minutes)

    val seconds=TimeUnit.MILLISECONDS.toSeconds(millisecond)

    if(!includeMillis){
        return "${if(hours<10) "0" else ""}$hours:"+
                "${if(minutes<10) "0" else ""}$minutes:"+
                "${if(seconds<10) "0" else ""}$seconds"

    }
    millisecond-=TimeUnit.SECONDS.toMillis(seconds)
    millisecond/=10
    return "${if(hours<10) "0" else ""}$hours:"+
            "${if(minutes<10) "0" else ""}$minutes:"+
            "${if(seconds<10) "0" else ""}$seconds:"+
            "${if(millisecond<10) "0" else ""}$millisecond"
}
}