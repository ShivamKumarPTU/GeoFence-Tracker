package com.example.geofencetracker.historyadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencetracker.DataModel.GeofenceVisitUiModel
import com.example.geofencetracker.databinding.HistroycardlayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter :
    ListAdapter<GeofenceVisitUiModel, HistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: HistroycardlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistroycardlayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.tvGeofenceName.text = item.geofenceName
        holder.binding.tvDate.text = formatDate(item.visitDate)
        holder.binding.tvEntryTime.text = formatTime(item.entryTime)
        holder.binding.tvExitTime.text =
            item.exitTime?.let { formatTime(it) } ?: "--"
        holder.binding.tvDuration.text = formatDuration(item.durationMillis)
    }

    class DiffCallback : DiffUtil.ItemCallback<GeofenceVisitUiModel>() {
        override fun areItemsTheSame(
            oldItem: GeofenceVisitUiModel,
            newItem: GeofenceVisitUiModel
        ): Boolean {
            return oldItem.geofenceId == newItem.geofenceId &&
                    oldItem.entryTime == newItem.entryTime
        }

        override fun areContentsTheSame(
            oldItem: GeofenceVisitUiModel,
            newItem: GeofenceVisitUiModel
        ): Boolean = oldItem == newItem
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    private fun formatDuration(duration: Long): String {
        val hours = duration / (1000 * 60 * 60)
        val minutes = (duration / (1000 * 60)) % 60
        return "${hours}h ${minutes}m"
    }
}
