package com.almerio.smartalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almerio.smartalarm.AlarmReceiver
import com.almerio.smartalarm.R
import com.almerio.smartalarm.data.Alarm
import com.almerio.smartalarm.databinding.RowItemAlarmBinding

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

    var listAlarm: ArrayList<Alarm> = arrayListOf()

    inner class MyViewHolder (val binding: RowItemAlarmBinding):
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]
        holder.binding.apply {
            itemDateAlarm.text = alarm.date
            itemTimeAlarm.text = alarm.Time
            itemNoteAlarm.text = alarm.message
            if (alarm.type == AlarmReceiver.TYPE_ONE_TIME){
                itemImgOneTime.setImageResource(R.drawable.ic_one_time)
            }else{
                itemImgOneTime.setImageResource(R.drawable.ic_repeating)
            }
        }
    }

    override fun getItemCount() = listAlarm.size

    fun setData(data: List<Alarm>){
        val alarmDiffUtil = AlarmDiffUtil(listAlarm, data)
        val diffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        listAlarm.clear()
        listAlarm.addAll(data)
        diffUtilResult.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }

}