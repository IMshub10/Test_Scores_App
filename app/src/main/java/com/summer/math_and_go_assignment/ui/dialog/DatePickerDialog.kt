package com.summer.math_and_go_assignment.ui.dialog

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.summer.math_and_go_assignment.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog : DialogFragment() {

    private val TAG = "DatePickerDialog"
    private var selectedDay: Int = Calendar.getInstance()[Calendar.DATE]
    private var selectedMonth: Int = Calendar.getInstance()[Calendar.MONTH]
    private var selectedYear: Int = Calendar.getInstance()[Calendar.YEAR]
    private lateinit var listener: OnDatePickerDateSelected

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireContext()
        )

        val view: View = LayoutInflater.from(requireContext())
            .inflate(R.layout.widget_date_picker, null)

        val datePicker: DatePicker = view.findViewById(R.id.dp_date_picker_dialog_pick)
        val doneButton: Button = view.findViewById(R.id.bt_date_picker_dialog_done)

        doneButton.setOnClickListener {
            val calendar = Calendar.Builder()
                .setDate(selectedYear, selectedMonth, selectedDay)
                .build()

            val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calendar.time)
            try {
                listener.onDateSelected(date)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            dismiss()
        }

        datePicker.setOnDateChangedListener { _, year, month, day ->
            selectedYear = year
            Log.e("MONTH MONTH", "$month")
            selectedMonth = month
            selectedDay = day
        }

        builder.setView(view)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
    }

    fun setOnDateSelectedListener(l: OnDatePickerDateSelected) {
        listener = l
    }

    interface OnDatePickerDateSelected {
        fun onDateSelected(date: String)
    }

    companion object {
        fun newInstance(bundle: Bundle): DatePickerDialog {
            val fragment = DatePickerDialog()
            fragment.arguments = bundle
            return fragment
        }
    }
}