package com.example.chamberlyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chamberlyapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding:ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.createChamber1.setOnClickListener {
            val intent = Intent(this@MainActivity,StartChatActivity::class.java)
            startActivity(intent)
        }
        mainBinding.search.setOnClickListener {
            val intent = Intent(this@MainActivity,SearchActivity::class.java)
            startActivity(intent)
        }
        mainBinding.logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}