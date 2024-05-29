package com.example.footpourtous.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footpourtous.R
import com.example.footpourtous.models.AvailableHour
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class HomeResultsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var backButton: Button
    private lateinit var title: TextView
    private lateinit var homeResultsAdapter: HomeResultsAdapter
    private val availableHoursList: MutableList<AvailableHour> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation de Firestore
        firestore = FirebaseFirestore.getInstance()

        // Get arguments
        val city = arguments?.getString("city")
        val gameType = arguments?.getString("gameType")
        val date = arguments?.getString("date")

        // Perform search
        if (city != null && gameType != null && date != null) {
            searchAvailableSlots(city, gameType, date)
        }

        title = view.findViewById(R.id.lableTerrain)
        title.text = "Terrains disponibles le $date:"

        backButton = view.findViewById(R.id.backButton)
        // Set up back button
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.availableHoursRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        homeResultsAdapter = HomeResultsAdapter(availableHoursList, requireContext())
        recyclerView.adapter = homeResultsAdapter
    }

    private fun searchAvailableSlots(city: String, gameType: String, date: String) {
        firestore.collection("terrain")
            .whereEqualTo("city", city)
            .whereEqualTo("gameType", gameType)
            .get()
            .addOnSuccessListener { documents ->
                availableHoursList.clear()
                for (document in documents) {
                    val price = document.get("price")
                    val terrainName = document.get("name")
                    val terrainId = document.id
                    val hours: MutableList<String> = mutableListOf()

                    val calendar = Calendar.getInstance()
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

                    val dateParts = date.split("/")
                    val givenDate = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                        set(Calendar.MONTH, dateParts[1].toInt() - 1)
                        set(Calendar.YEAR, dateParts[2].toInt())
                    }

                    if (givenDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        givenDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        givenDate.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                        for (i in (currentHour + 1)..22) {
                            if (i >= 15) {
                                hours.add("$i" + "h")
                            }
                        }
                    } else {
                        for (i in 15..22) {
                            hours.add("$i" + "h")
                        }
                    }

                    document.reference.collection("reservation")
                        .whereEqualTo("date", date).get()
                        .addOnSuccessListener { reservationDocs ->
                            for (reservation in reservationDocs) {
                                val takenHour = reservation.get("time")
                                hours.remove(takenHour)
                                Log.d("HomeResultsFragment", "found reservation at : $takenHour")
                            }
                            for (h in hours) {
                                val avHour = AvailableHour(h, date, terrainId, "$terrainName", "$city", "$price")
                                availableHoursList.add(avHour)
                                Log.d("HomeResultsFragment", "available hour added : $avHour")
                            }
                            homeResultsAdapter.notifyDataSetChanged()
                        }
                }
            }
    }
}
