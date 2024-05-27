package com.example.footpourtous.ui.compte


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.footpourtous.R
import com.example.footpourtous.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CompteFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var firstNameTv: TextView
    private lateinit var lastNameTv: TextView
    private lateinit var emailTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_compte, container, false)

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find views
        firstNameTv = view.findViewById(R.id.firstNameTv)
        lastNameTv = view.findViewById(R.id.lastNameTv)
        emailTv = view.findViewById(R.id.emailTv)

        // Load user information
        loadUserInfo()

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun loadUserInfo() {
        val user = firebaseAuth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val email = document.getString("email")

                    // Update UI with user information
                    firstNameTv.text = firstName
                    lastNameTv.text = lastName
                    emailTv.text = email
                } else {
                    Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}

