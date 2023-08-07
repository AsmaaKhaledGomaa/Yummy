package com.asmaa.yummy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.asmaa.yummy.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YummyActivity : AppCompatActivity() {

    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Yummy)
        setContentView(R.layout.activity_yummy)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

    }
}