package com.example.roomexample

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.roomexample.database.SceneDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var sp: SharedPreferences
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setup navigation controller with the up button
        navController = this.findNavController(R.id.navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        //get the shared viewModel associated with the activity
        viewModel = ViewModelProvider(this,
            MyViewModelFactory(this.application)).get(MyViewModel::class.java)

        //check whether the database is created or not
        //database will be initialized once in this project
        //write a mark to a sharedpreference file
        sp = getPreferences(Context.MODE_PRIVATE)
        val databaseState = sp.getBoolean("Created", false)
        if (!databaseState) {
            viewModel.initDB()
            val editor = sp.edit()
            editor.putBoolean("Created", true)
            editor.apply()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}