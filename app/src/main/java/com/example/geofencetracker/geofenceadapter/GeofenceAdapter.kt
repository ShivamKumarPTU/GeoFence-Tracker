package com.example.geofencetracker.geofenceadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencetracker.DataModel.GeofenceEntity
//import com.example.geofencetracker.databinding.GeofencecardLayoutBinding
import com.example.geofencetracker.databinding.GeofencecardlayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeofenceAdapter( private val onDeleteClick: (GeofenceEntity) -> Unit,
                      private val onEditClick: (GeofenceEntity) -> Unit): ListAdapter<GeofenceEntity, GeofenceAdapter.ViewHolder>(DiffCallback()) {


    inner class ViewHolder(val binding: GeofencecardlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            GeofencecardlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvGeofenceName.text = item.name
        holder.binding.tvLatLng.text = "${item.latitude},${item.longitude}"
        holder.binding.tvRadius.text = "Radius: ${item.radius}m"
        holder.binding.tvCreated.text = formatDate(item.createdAt)
        // delete click
        holder.binding.ivDelete.setOnClickListener {
             onDeleteClick(item)
           // Toast.makeText(holder.itemView.context, "Delete Clicked", Toast.LENGTH_SHORT).show()
        }
        // edit click
        holder.binding.ivEdit.setOnClickListener {
        onEditClick(item)
        //Toast.makeText(holder.itemView.context, "Edit Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(time: Long):String{
        val sdf= SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }

    class DiffCallback: DiffUtil.ItemCallback<GeofenceEntity>(){
        override fun areItemsTheSame(
            oldItem: GeofenceEntity,
            newItem: GeofenceEntity
        ):Boolean{
            return oldItem.geofenceId == newItem.geofenceId
        }

        override fun areContentsTheSame(
            oldItem:GeofenceEntity,
            newItem:GeofenceEntity
        ):Boolean = oldItem == newItem

    }
}
