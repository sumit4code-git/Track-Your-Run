package com.example.runtracker.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.other.CustomMarkerView
import com.example.runtracker.other.TrackingUtility
import com.example.runtracker.ui.viewmodels.MainViewModel
import com.example.runtracker.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.lang.Math.round

@AndroidEntryPoint
//Whenever we need to inject something into android component(here its fragment) we use above notation
class StatisticFragment:Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
        BarChartSetUp()
    }
//    Bar charts Ui setups
    private fun BarChartSetUp(){
        barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM/*To make x axis bottom bcz initially its up*/
            setDrawLabels(false)/*To disable label on x axis*/
            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
    barChart.axisLeft.apply {
        axisLineColor=Color.WHITE
        textColor=Color.WHITE
        setDrawGridLines(false)
    }
    barChart.axisRight.apply {
        axisLineColor=Color.WHITE
        textColor=Color.WHITE
        setDrawGridLines(false)
    }
    barChart.apply {
        description.text="Avg Speed Over Time"
        description.textColor=Color.YELLOW
        legend.isEnabled=false
    }
    }

    private fun subscribeToObserver(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTime=TrackingUtility.getFormattedStopWatch(it)
                tvTotalTime.text=totalTime
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km=it/1000f
                val totalDistance= round(km*10f)/10f
                val totalDistanceString="${totalDistance}km"
                tvTotalDistance.text=totalDistanceString
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed= round(it*10f) /10f
                val avgSpeeedText="${avgSpeed} km/h"
                tvAverageSpeed.text=avgSpeeedText
            }
        })

        viewModel.totalCaloriesBurnt.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCalorieBurnt="${it}kcal"
                tvTotalCalories.text=totalCalorieBurnt
            }
        })
//        To pass data in Barchart list
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataset = BarDataSet(allAvgSpeed, "Avg Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                barChart.data = BarData(barDataset)
                barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }
}