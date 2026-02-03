package com.example.geofencetracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofencetracker.DataModel.GeofenceVisitUiModel
import com.example.geofencetracker.databinding.FragmentGeofenceBinding
import com.example.geofencetracker.databinding.FragmentHistoryBinding
import com.example.geofencetracker.geofenceadapter.GeofenceAdapter
import com.example.geofencetracker.historyadapter.HistoryAdapter
import com.example.geofencetracker.receiver.GeofenceReceiver
import com.example.geofencetracker.viewmodel.GeofenceHistoryViewModel


class History : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: GeofenceHistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeHistory()
        binding.radioButton.setOnClickListener {
            triggerDummyGeofenceNotification()
        }

    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeHistory() {

        // ================= REAL CODE (COMMENTED) =================

        historyViewModel.historyUiList.observe(viewLifecycleOwner) { list ->
            historyAdapter.submitList(list)
            handleEmptyState(list)
        }
        // =========================================================
/*

        // 🔥 DUMMY DATA FOR DEMO
        val dummyHistory = listOf(
            createDummy("Home", "09:00", "10:30"),
            createDummy("Office", "11:00", "18:00"),
            createDummy("Gym", "18:30", "19:45"),
            createDummy("Cafe", "20:00", "20:40"),
            createDummy("Market", "08:00", "08:20"),
            createDummy("College", "09:30", "15:30"),
            createDummy("Mall", "16:00", "17:15"),
            createDummy("Park", "06:00", "06:45"),
            createDummy("Hospital", "12:00", "12:30"),
            createDummy("Station", "21:00", "21:20")
        )

        historyAdapter.submitList(dummyHistory)
        handleEmptyState(dummyHistory)

         */
    }

    private fun createDummy(
        name: String,
        entry: String,
        exit: String
    ) = GeofenceVisitUiModel(
        geofenceId = name,
        geofenceName = name,
        entryTime = System.currentTimeMillis(),
        exitTime = System.currentTimeMillis() + 3600000,
        durationMillis = 3600000,
        visitDate = System.currentTimeMillis()
    )


    private fun handleEmptyState(list: List<GeofenceVisitUiModel>) {
        if (list.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.historyRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.historyRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun triggerDummyGeofenceNotification() {
        val intent = Intent(requireContext(), GeofenceReceiver::class.java)
        requireContext().sendBroadcast(intent)
    }

}


