package com.example.footpourtous.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.footpourtous.R
import com.google.firebase.firestore.FirebaseFirestore

class HomeResultsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var resultsTextView: TextView
    private lateinit var backButton: Button

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
        resultsTextView = view.findViewById(R.id.availableSlotsTextView)
        backButton = view.findViewById(R.id.backButton)

        // Get arguments
        val city = arguments?.getString("city")
        val gameType = arguments?.getString("gameType")
        val date = arguments?.getString("date")

        // Perform search
        if (city != null && gameType != null && date != null) {
            searchAvailableSlots(city, gameType, date)
        }

        // Set up back button
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun searchAvailableSlots(city: String, gameType: String, date: String) {
        firestore.collection("terrain")
            .whereEqualTo("city", city)
            .whereEqualTo("gameType", gameType)
            .get()
            .addOnSuccessListener { documents ->
                val results = StringBuilder()
                for (document in documents) {
                    val reservations = document.reference.collection("reservation")
                    reservations.whereEqualTo("date", date).get()
                        .addOnSuccessListener { reservationDocs ->
                            results.append("Terrain :  ${document.id}\n")
                            for (reservation in reservationDocs) {
                                results.append("Time :  ${reservation.getString("time")}\n")
                                results.append("User :  ${reservation.getString("userId")}\n")
                            }
                            updateUIWithAvailableSlots(results.toString())
                        }
                }
            }
    }

    private fun updateUIWithAvailableSlots(results: String) {
        resultsTextView.text = results
    }
}