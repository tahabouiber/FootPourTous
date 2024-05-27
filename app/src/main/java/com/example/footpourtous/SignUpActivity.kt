package com.example.footpourtous

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.footpourtous.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val firstName = binding.firstNameEt.text.toString()
            val lastName = binding.lastNameEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            val userId = user?.uid
                            val userMap = hashMapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "email" to email
                            )

                            if (userId != null) {
                                firestore.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Les champs vides ne sont pas autorisés !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
