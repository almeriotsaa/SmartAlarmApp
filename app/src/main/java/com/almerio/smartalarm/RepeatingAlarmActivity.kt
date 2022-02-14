package com.almerio.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.almerio.smartalarm.data.Alarm
import com.almerio.smartalarm.fragment.TimeDialogFragment
import com.almerio.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.almerio.smartalarm.helper.timeFormatter
import com.almerio.smartalarm.local.AlarmDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var alarmService: AlarmReceiver? = null
    private val db by lazy { AlarmDB(this) }
    //private var alarmReceiver : AlarmReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                val timePickerDialog = TimeDialogFragment()
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            }
            btnAdd.setOnClickListener {
                val time = tvOnceTime.text.toString()
                val message = edtNote.text.toString()

                if (time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm_repeat),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    alarmService?.setRepeatingAlarm(applicationContext, 0, time, message)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                message,
                                AlarmReceiver.TYPE_REPEATING
                            )
                        )
                        Log.i("AddAlarm", "alarm set on $time with message $message")
                        finish()
                    }
                }
            }
        }
    }
    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }
}