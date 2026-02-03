package com.example.geofencetracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

class Geofence : Fragment() {
 private var _binding : FragmentGeofenceBinding? = null
    private val binding get() = _binding!!
  private lateinit var geofenceAdapter: GeofenceAdapter
  private val geofenceViewModel: GeofenceViewModel by viewModels()
private lateinit var geofencingClient: GeofencingClient

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
        // Initialising Geofencing client
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
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

    // delete Dialog for marker
    private fun showDeleteConfirmationDialog(geofence: GeofenceEntity?) {
        val dialogView= layoutInflater.inflate(
            R.layout.dialog_delete_geofence,
            null
        )
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        tvMessage.text =
            "Are you sure you want to delete \"${geofence?.name}\"?\n" +
                    "This action cannot be undone."

        val dialog =AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        btnDelete.setOnClickListener {
            // 🔥 For now only Toast
            geofence?.let{ nonNullGeofence ->
                // Unregister from GeofencingClient first
                geofencingClient.removeGeofences(listOf(nonNullGeofence.geofenceId)).run {
                    addOnSuccessListener {
                        Log.d(
                            "GeofenceDelete",
                            "Successfully removed from Geofencing Client:${nonNullGeofence.geofenceId}"
                        )
                    }
                    //2. on success , remove from the database via the viewmodel
                    geofenceViewModel.deleteGeofence(nonNullGeofence)
                    Toast.makeText(
                        requireContext(),
                        "Geofence deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    addOnFailureListener {
                        Log.e("GeofenceDelete", "Failed to remove from Geofencing Client" )
                        Toast.makeText(
                            requireContext(),
                            "Error :Failed to delete the active geofence",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


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
