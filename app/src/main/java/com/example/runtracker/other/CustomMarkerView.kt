package com.example.runtracker.other

import android.content.Context
import com.example.runtracker.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*
//To setup things when we click
class CustomMarkerView(
    val runs:List<Run>,
    context: Context,
    layoutId:Int,
) :MarkerView(context,layoutId){
//    To update text of popup here with access Entry
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e==null)
            return
        val currentRunId=e.x.toInt()
        val run= runs[currentRunId]

        val calender=Calendar.getInstance().apply {
            timeInMillis=run.timestamp
        }
        val dateFormat= SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text=dateFormat.format(calender.time)
        val avgSpeed="${run.avgSpeedInKMH}km/h"
        tvAvgSpeed.text=avgSpeed

        val distanceInKm="${run.distanceInMeters / 1000f}km"
        tvDistance.text=distanceInKm

        tvDuration.text=TrackingUtility.getFormattedStopWatch(run.timeInMillis)

        val calorieBurnt="${run.caloriesBurned}kcal"
        tvCaloriesBurned.text=calorieBurnt
    }
//Position of boxes to be shown we click
    override fun getOffset(): MPPointF {
//    These values are copy paste from mp chart document
        return MPPointF(-width/2f,-height.toFloat())
    }
}