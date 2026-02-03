package com.example.geofencetracker

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofencetracker.DataModel.GeofenceEntity
import com.example.geofencetracker.DataModel.GeofenceVisitUiModel
import com.example.geofencetracker.databinding.FragmentGeofenceBinding
import com.example.geofencetracker.geofenceadapter.GeofenceAdapter
import com.example.geofencetracker.viewmodel.GeofenceViewModel

class Geofence : Fragment() {
 private var _binding : FragmentGeofenceBinding? = null
    private val binding get() = _binding!!
  private lateinit var geofenceAdapter: GeofenceAdapter
  private val geofenceViewModel: GeofenceViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGeofenceBinding.inflate(inflater,container,false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        // later this will come from Room/viewmodel

        geofenceAdapter = GeofenceAdapter(
            onEditClick = { geofence ->
                showEditGeofenceDialog(geofence)
            },
            onDeleteClick = { geofence ->
                showDeleteConfirmationDialog(geofence)
            }
        )

        binding.geofenceRecyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = geofenceAdapter
         //   setHasFixedSize(true)
         //   adapter.notifyDataSetChanged()
        }
              observeGeofences()
        }

    private fun showEditGeofenceDialog(geofence: GeofenceEntity) {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_edit_geofence,
            null
        )

        val etName = dialogView.findViewById<EditText>(R.id.etGeofenceName)
        val etRadius = dialogView.findViewById<EditText>(R.id.etRadius)
        val tvLat = dialogView.findViewById<TextView>(R.id.tvLatitude)
        val tvLng = dialogView.findViewById<TextView>(R.id.tvLongitude)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // Prefill existing data
        etName.setText(geofence.name)
        etRadius.setText(geofence.radius.toString())
        etRadius.hint = "10–50 meters"

        tvLat.text = "Latitude: ${geofence.latitude}"
        tvLng.text = "Longitude: ${geofence.longitude}"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newRadius = etRadius.text.toString().toIntOrNull()

            if (newName.isEmpty()) {
                etName.error = "Name required"
                return@setOnClickListener
            }

            if (newRadius == null || newRadius !in 10..50) {
                etRadius.error = "Radius must be 10–50 meters"
                return@setOnClickListener
            }

            val updatedGeofence = geofence.copy(
                name = newName,
                radius = newRadius
            )

            geofenceViewModel.updateGeofence(updatedGeofence)

            Toast.makeText(
                requireContext(),
                "Geofence updated",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showDeleteConfirmationDialog(geofence: GeofenceEntity) {
        val dialogView= layoutInflater.inflate(
            R.layout.dialog_delete_geofence,
            null
        )
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        tvMessage.text =
            "Are you sure you want to delete \"${geofence.name}\"?\n" +
                    "This action cannot be undone."

        val dialog =AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    btnDelete.setOnClickListener {
        // 🔥 For now only Toast
        geofenceViewModel.deleteGeofence(geofence)

        Toast.makeText(
            requireContext(),
            "Geofence deleted",
            Toast.LENGTH_SHORT
        ).show()

        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    }
    private fun observeGeofences(){
        geofenceViewModel.geofences.observe(viewLifecycleOwner){ list->
            geofenceAdapter.submitList(list)
            handleEmptyState(list)
        }
    }
    private fun handleEmptyState(list: List<GeofenceEntity>) {
        if (list.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.geofenceRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.geofenceRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
