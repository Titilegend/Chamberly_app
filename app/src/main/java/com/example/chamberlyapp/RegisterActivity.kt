package com.example.chamberlyapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chamberlyapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    lateinit var registerBinding:ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = registerBinding.root
        setContentView(view)
        UserCacheManager.initialize(applicationContext)

        registerBinding.continueButton.setOnClickListener {
            val username = registerBinding.displayNameText.text.toString().trim()
           if (username.isNotEmpty()) {
               checkUsernameAvailability(username)
           } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }
        }

    private fun checkUsernameAvailability(username: String) {
        val displayNamesCollection = firestore.collection("Display_names").document(username)


        displayNamesCollection
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null && querySnapshot.exists()) {

                                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                            } else {
                                // Username is unique, show terms and conditions dialog
                                createCustomDialog(username).show()
                            }

                    }

                 else {
                    Log.e("Firestore", "Error checking username availability", task.exception)
                    // Error occurred while checking username availability
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun authenticateUser(username:String) {
        auth.signInAnonymously()
            .addOnCompleteListener { authResult ->
                if(authResult.isSuccessful){
                    val currentUser = auth.currentUser
                    val uid = currentUser?.uid
                    val email = "${uid}@chamberly.net"
                    val timestamp = FieldValue.serverTimestamp()
                   val platform = "android"


                    val userDoc = firestore.collection("Display_names").document(username)
                    val userData = hashMapOf(
                        "display_name" to username,
                        "email" to email,
                        "userId" to uid,
                    )

                    userDoc.set(userData)
                        .addOnSuccessListener {
                            // User document created successfully

                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()


                            if (uid != null) {

                                createAccountDocument(uid,username,email,timestamp)
                                UserCacheManager.getInstance().cacheUserData(uid, username, email, platform)
                            }

                            // Proceed with any additional actions or navigation
                        }
                        .addOnFailureListener {
                            // Failed to create user document
                            Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                        }

                }
                else{
                    Log.e("Firestore", "Error authenticating user", authResult.exception)
                    // Error occurred while checking username availability
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                }



                }
            }






    private fun createCustomDialog(username: String): Dialog {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.terms_title)
                .setItems(R.array.Terms_array) { dialog, which ->
                    // Handle item selection here
                    when (which) {
                        0 -> {
                            // View terms and conditions
                            // Add your code here
                        }

                        1 -> {
                            // View privacy policy
                            // Add your code here
                        }

                        2 -> {

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)

                            checkUsernameAvailability(username)
                            authenticateUser(username)
                        }

                        3->{
                            dialog.dismiss()
                        }
                    }
                }
            return builder.create()


    }
    private fun createAccountDocument(uid:String, displayName: String, email:String,timestamp:FieldValue){
        val accountData = hashMapOf(
            "Display_name" to displayName,
            "Email" to email,
            "UID" to uid,
            "Timestamp" to timestamp,
            "Platform" to "android"
        )
        firestore.collection("accounts").document(uid)
            .set(accountData)
            .addOnCompleteListener {
                Toast.makeText(this, "AccountData created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create accountData", Toast.LENGTH_SHORT).show()
            }
    }
   /* private fun getPlatform(): String {
        return "Android"
    }*/
    override fun onStart() {
        super.onStart()
        //get info about the user
        val user = auth.currentUser
        if (user != null) {
            Toast.makeText(applicationContext, "Welcome to Chamberly", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}