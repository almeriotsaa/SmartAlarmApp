package com.almerio.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.almerio.smartalarm.data.Alarm
import com.almerio.smartalarm.fragment.DateDilaogFragment
import com.almerio.smartalarm.fragment.TimeDialogFragment
import com.almerio.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.almerio.smartalarm.helper.timeFormatter
import com.almerio.smartalarm.local.AlarmDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DateDilaogFragment.DialogDateSetListener,
    TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private val db by lazy { AlarmDB(this) }
    private var alarmService: AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()


        initView()
    }

    private fun initView() {
        binding.apply {

            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDilaogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }
            btnAdd.setOnClickListener {
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date == "Date" && time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    alarmService?.setOneTimeAlarm(applicationContext, 0, date, time, message)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                message,
                                AlarmReceiver.TYPE_ONE_TIME
                            )
                        )
                        Log.i("AddAlarm", "alarm set on: $date $time with message $message")
                        finish()
                    }
                }
            }
        }
    }
    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        // untuk menentukan calendar sekarang jadi tanggal yang telah dipilih di datepicker
        calendar.set(year, month, dayOfMonth)
        val dateFormatted = SimpleDateFormat("dd-MM-yy", Locale.getDefault())

        binding.tvOnceDate.text = dateFormatted.format(calendar.time)
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }
}