package com.example.footpourtous.ui.home

import android.icu.text.SimpleDateFormat
import android.media.MediaDrm.LogMessage
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.footpourtous.R
import com.example.footpourtous.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date

class HomeFragment : Fragment() {

    // Liaison pour accéder aux vues
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firestore instance
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation de Firestore
        firestore = FirebaseFirestore.getInstance()

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

            // Appel à une fonction pour rechercher les créneaux disponibles
            searchAvailableSlots(city, gameType, date)
        }
    }

    private fun searchAvailableSlots(city: String, gameType: String, date: String) {

        // Référence à la collection "terrains"
        val terrainCollection = firestore.collection("terrains")

        // Requête pour obtenir les terrains de la ville et du type de jeu spécifiés
        val query = terrainCollection
            .whereEqualTo("city", city)
            .whereEqualTo("gameType", gameType)

        // Exécution de la requête
        query.get().addOnSuccessListener { documents ->
            // Liste pour stocker les informations sur les terrains disponibles
            val availableSlotsList = mutableListOf<String>()


            for (document in documents) {
                // Obtention des données du terrain
                val terrainData = document.data

                // Construction de la référence à la sous-collection "reservation" du terrain actuel
                val reservationCollection = terrainCollection.document(document.id)
                    .collection("reservation")

                // Requête pour obtenir les réservations du jour spécifié
                val reservationQuery = reservationCollection
                    .whereEqualTo("time", "$date;")

                // Exécution de la requête des réservations
                reservationQuery.get().addOnSuccessListener { reservationDocuments ->
                    // Liste pour stocker les informations sur les réservations du terrain actuel
                    val reservationInfoList = mutableListOf<String>()

                    for (reservationDocument in reservationDocuments) {
                        // Obtention des données de la réservation
                        val reservationData = reservationDocument.data

                        // Construction d'une chaîne d'informations sur la réservation
                        val reservationInfo = "${reservationData["userId"]} - ${reservationData["time"]}"

                        // Ajout de la chaîne d'informations à la liste des réservations
                        reservationInfoList.add(reservationInfo)
                    }

                    // Construction d'une chaîne d'informations sur le terrain et ses réservations
                    val terrainInfo = "Terrain: ${terrainData["name"]}, City: $city, Game Type: $gameType, Reservations: ${reservationInfoList.joinToString()}"
                    // Ajout de la chaîne d'informations à la liste des terrains disponibles
                    availableSlotsList.add(terrainInfo)

                    // Mise à jour de l'UI avec les terrains disponibles
                    updateUIWithAvailableSlots(availableSlotsList)
                }
            }
        }.addOnFailureListener { exception ->
            // Gestion des erreurs
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }

    private fun updateUIWithAvailableSlots(availableSlotsList: List<String>) {
        // Vérification si des terrains sont disponibles
        if (availableSlotsList.isNotEmpty()) {
            // Construction d'une chaîne contenant les informations sur les terrains disponibles
            val slotsInfo = availableSlotsList.joinToString("\n")

            // Affichage des informations dans une TextView ou tout autre composant de l'UI
            binding.availableSlotsTextView.text = slotsInfo
        } else {
            // Aucun terrain disponible
            binding.availableSlotsTextView.text = "Aucun terrain disponible pour la ville et le type de jeu spécifiés."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
