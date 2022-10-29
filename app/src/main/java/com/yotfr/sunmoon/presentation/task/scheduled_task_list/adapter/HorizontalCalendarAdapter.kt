package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.databinding.ItemCalendarBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val data: ArrayList<Date>,
    currentDate: Calendar,
    changeMonth: Calendar?
): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {


    private var mListener: OnItemClickListener? = null
    private var index = -1
    private var selectCurrentDate = true
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val selectedDay =
        when {
            changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
            else -> currentDay
        }
    private val selectedMonth =
        when {
            changeMonth != null -> changeMonth[Calendar.MONTH]
            else -> currentMonth
        }
    private val selectedYear =
        when {
            changeMonth != null -> changeMonth[Calendar.YEAR]
            else -> currentYear
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ), mListener!!)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val cal = Calendar.getInstance(Locale.getDefault())
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.getDefault())
        cal.time = data[position]

        val calClone = cal.clone() as Calendar
        calClone.add(Calendar.DAY_OF_MONTH, 1)

        val displayMonth = cal[Calendar.MONTH]
        val displayYear= cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]

        try {
            val dayInWeek = sdf.parse(cal.time.toString())!!
            val sdfWeekDays = SimpleDateFormat("EEE", Locale.getDefault())
            holder.txtDayInWeek.text = sdfWeekDays.format(dayInWeek).toString()
        } catch (ex: ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
        holder.txtDay.text = cal[Calendar.DAY_OF_MONTH].toString()

        if (displayYear >= currentYear)
            if (displayMonth >= currentMonth || displayYear > currentYear)
                if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {
                    holder.card.setOnClickListener {
                        index = position
                        selectCurrentDate = false
                        holder.listener.onItemClick(position)
                        notifyDataSetChanged()
                    }
                    if (index == position)
                        makeItemSelected(holder)
                    else {
                        if (displayDay == selectedDay
                            && displayMonth == selectedMonth
                            && displayYear == selectedYear
                            && selectCurrentDate)
                            makeItemSelected(holder)
                        else
                            makeItemDefault(holder)
                    }
                } else makeItemDisabled(holder)
            else makeItemDisabled(holder)
        else makeItemDisabled(holder)
    }

    inner class ViewHolder(binding:ItemCalendarBinding, val listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        var txtDay = binding.itemTvDay
        var txtDayInWeek = binding.itemTvDayOfWeek
        var card = binding.itemCalendarCard
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun makeItemDisabled(holder: ViewHolder) {
        holder.card.isSelected = false
        holder.txtDay.isSelected = false
        holder.txtDayInWeek.isSelected = false
        holder.card.isEnabled = false
        holder.txtDay.isEnabled = false
        holder.txtDayInWeek.isEnabled = false
    }

    private fun makeItemSelected(holder: ViewHolder) {
        holder.card.isSelected = true
        holder.txtDay.isSelected = true
        holder.txtDayInWeek.isSelected = true
        holder.card.isEnabled = true
        holder.txtDay.isEnabled = true
        holder.txtDayInWeek.isEnabled = true

    }

    private fun makeItemDefault(holder: ViewHolder) {
        holder.card.isSelected = false
        holder.txtDay.isSelected = false
        holder.txtDayInWeek.isSelected = false
        holder.card.isEnabled = true
        holder.txtDay.isEnabled = true
        holder.txtDayInWeek.isEnabled = true
    }
}

