package com.example.footpourtous.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footpourtous.R
import com.example.footpourtous.models.AvailableHour

class HomeResultsAdapter(private val availableHoursList: List<AvailableHour>) :
    RecyclerView.Adapter<HomeResultsAdapter.AvailableHourViewHolder>() {

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

            Log.d("HomeResultsAdapter", "Binding view holder at position $position with data $avHour")
        }

        override fun getItemCount(): Int {
            return availableHoursList.size
        }

        class AvailableHourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val avTimeTextView: TextView = itemView.findViewById(R.id.avTimeTextView)
            val avPriceTextView: TextView = itemView.findViewById(R.id.avPriceTextView)
            val avTerrainTextView: TextView = itemView.findViewById(R.id.avTerrainTextView)
            val avCityTextView: TextView = itemView.findViewById(R.id.avCityTextView)
        }

}