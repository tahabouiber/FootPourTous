package com.example.footpourtous.ui.reservations

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footpourtous.R
import com.example.footpourtous.models.Reservation

class ReservationsAdapter(private val reservationsList: List<Reservation>) :
    RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservationsList[position]
        holder.dateTextView.text = reservation.date
        holder.timeTextView.text = reservation.time
        holder.terrainNameTextView.text = reservation.terrainName
        holder.cityTextView.text = reservation.city

        Log.d("ReservationsAdapter", "Binding view holder at position $position with data $reservation")
    }

    override fun getItemCount(): Int {
        return reservationsList.size
    }

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val terrainNameTextView: TextView = itemView.findViewById(R.id.terrainNameTextView)
        val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
    }
}