package com.example.footpourtous.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.footpourtous.R
import com.example.footpourtous.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

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

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.dateEditText.setText(dateFormat.format(calendar.time))
        }

        binding.dateEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding.searchButton.setOnClickListener {
            val city = binding.citySpinner.selectedItem.toString()
            val gameType = binding.gameTypeSpinner.selectedItem.toString()
            val date = binding.dateEditText.text.toString()

            // Validate the date
            if (date.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDate = Calendar.getInstance()
            val selectedDate = Calendar.getInstance().apply {
                val dateParts = date.split("/")
                set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                set(Calendar.MONTH, dateParts[1].toInt() - 1)
                set(Calendar.YEAR, dateParts[2].toInt())
            }

            val currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
            if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH) &&
                currentHour >= 22) {
                Toast.makeText(requireContext(), "Il est trop tard pour réserver aujourd'hui", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("HomeFragment", "Search button clicked: city=$city, gameType=$gameType, date=$date")

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
