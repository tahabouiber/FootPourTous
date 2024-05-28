package com.example.footpourtous.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footpourtous.databinding.FragmentDashboardBinding
import com.example.footpourtous.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var reservationsAdapter: ReservationsAdapter
    private val reservationsList: MutableList<Reservation> = mutableListOf()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        initRecyclerView()

        // Load reservations data
        loadReservations()
    }

    private fun initRecyclerView() {
        reservationsAdapter = ReservationsAdapter(reservationsList)
        binding.reservationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservationsAdapter
        }
    }

    private fun loadReservations() {
        Log.d("DashboardFragment1", "loooading")
        val currentUserUid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .whereEqualTo("DocumentId", currentUserUid)
            .get()
            .addOnSuccessListener { documents ->
                reservationsList.clear()
                for (document in documents) {
                    val allReservations = document.reference.collection("reservation")
                    allReservations.get().addOnSuccessListener{ reservationDocs ->
                        for (reservation in reservationDocs) {
                            reservationsList.add(reservation.toObject(Reservation::class.java))
                            Log.d("DashboardFragment2", "reservation added : $reservation")
                        }
                    }
                }
                reservationsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
            }
        Log.d("DashboardFragment3", "loooading finished")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
