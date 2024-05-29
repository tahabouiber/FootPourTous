package com.example.footpourtous.ui.home

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.footpourtous.R
import com.example.footpourtous.models.AvailableHour
import com.example.footpourtous.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeResultsAdapter(
    private val availableHoursList: List<AvailableHour>,
    private val context: Context
) : RecyclerView.Adapter<HomeResultsAdapter.AvailableHourViewHolder>() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableHourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_available_hour, parent, false)
        return AvailableHourViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableHourViewHolder, position: Int) {
        val avHour = availableHoursList[position]
        holder.avTimeTextView.text = avHour.time
        holder.avPriceTextView.text = avHour.price
        holder.avTerrainTextView.text = avHour.terrainName
        holder.avCityTextView.text = avHour.city

        holder.reserveButton.setOnClickListener {
            showConfirmationDialog(avHour)
        }
    }

    override fun getItemCount(): Int {
        return availableHoursList.size
    }

    private fun showConfirmationDialog(avHour: AvailableHour) {
        AlertDialog.Builder(context)
            .setTitle("Confirmer la Reservation")
            .setMessage("Voulez-vous reserver cette heure?")
            .setPositiveButton("Oui") { dialog, _ ->
                createReservation(avHour)
                dialog.dismiss()
            }
            .setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun createReservation(avHour: AvailableHour) {
        val currentUser = firebaseAuth.currentUser ?: return
        val currentUserUid = currentUser.uid

        val reservation = Reservation(
            time = avHour.time,
            date = avHour.date,
            terrainId = avHour.terrainId,
            userId = currentUserUid,
            terrainName = avHour.terrainName,
            city = avHour.city,
            price = avHour.price
        )

        firestore.runBatch { batch ->
            val userReservationRef = firestore.collection("users")
                .document(currentUserUid)
                .collection("reservation")
                .document()
            batch.set(userReservationRef, reservation)

            val terrainReservationRef = firestore.collection("terrain")
                .document(avHour.terrainId)
                .collection("reservation")
                .document()
            batch.set(terrainReservationRef, reservation)
        }.addOnSuccessListener {
            Toast.makeText(context, "Reservation réussie!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e("HomeResultsAdapter", "Failed to create reservation: ${e.message}")
            Toast.makeText(context, "Reservation failed.", Toast.LENGTH_SHORT).show()
        }
    }

    class AvailableHourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avTimeTextView: TextView = itemView.findViewById(R.id.avTimeTextView)
        val avPriceTextView: TextView = itemView.findViewById(R.id.avPriceTextView)
        val avTerrainTextView: TextView = itemView.findViewById(R.id.avTerrainTextView)
        val avCityTextView: TextView = itemView.findViewById(R.id.avCityTextView)
        val reserveButton: Button = itemView.findViewById(R.id.reserveButton)
    }
}
