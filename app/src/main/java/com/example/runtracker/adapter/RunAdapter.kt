package com.example.runtracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runtracker.R
import com.example.runtracker.db.Run
import com.example.runtracker.other.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>(){
    inner class RunViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    val diffCallback=object:DiffUtil.ItemCallback<Run>(){
        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
          return oldItem.hashCode()==newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return  oldItem.id==newItem.id
        }

    }

    val differ= AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Run>)=differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run=differ.currentList.get(position)
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)

            val calendar= Calendar.getInstance().apply {
                timeInMillis=run.timestamp
            }

            val dateFormat=SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text=dateFormat.format(calendar.time)

            val avgSpeed="${run.avgSpeedInKMH}km/h"
            tvAvgSpeed.text=avgSpeed

            val distanceInKm="${run.distanceInMeters / 1000f}km"
            tvDistance.text=distanceInKm

            tvTime.text=TrackingUtility.getFormattedStopWatch(run.timeInMillis)

            val calorieBurnt="${run.caloriesBurned}kcal"
            tvCalories.text=calorieBurnt


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}