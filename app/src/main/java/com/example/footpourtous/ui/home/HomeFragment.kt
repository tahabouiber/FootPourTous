package com.example.footpourtous.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.footpourtous.R
import com.example.footpourtous.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Définir les valeurs des Spinner
        binding.citySpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.city_array,
            android.R.layout.simple_spinner_item
        )
        binding.gameTypeSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.game_type_array,
            android.R.layout.simple_spinner_item
        )

        // Gestionnaire d'événements pour le bouton de recherche
        binding.searchButton.setOnClickListener {
            val city = binding.citySpinner.selectedItem.toString()
            val gameType = binding.gameTypeSpinner.selectedItem.toString()
            val date = binding.dateEditText.text.toString()

            // Log the button click
            Log.d("HomeFragment", "Search button clicked: city=$city, gameType=$gameType, date=$date")

            // Navigate to HomeResultsFragment with the search criteria
            val bundle = Bundle()
            bundle.putString("city", city)
            bundle.putString("gameType", gameType)
            bundle.putString("date", date)

            findNavController().navigate(R.id.navigation_home_results, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
