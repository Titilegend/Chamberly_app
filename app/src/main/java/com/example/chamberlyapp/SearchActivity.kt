package com.example.chamberlyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chamberlyapp.databinding.ActivityRegisterBinding
import com.example.chamberlyapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var searchActivityBinding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchActivityBinding = ActivitySearchBinding.inflate(layoutInflater)
        val view = searchActivityBinding.root
        setContentView(view)
    }
}