package com.rrat.datastoredemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.rrat.datastoredemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Create the DataStore instance
    private lateinit var settingPrefs: SettingPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingPrefs = SettingPrefs(this)

        lifecycleScope.launch {
            settingPrefs.saveIdVendedor(500)
        }

        binding.btnReadDataStore.setOnClickListener {
            Toast.makeText(this, "CLICK ON", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                settingPrefs.saveIdVendedor(1000)
            }
        }

        binding.btnSearch.setOnClickListener {
            intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        observeIdVendedor()


    }

    private fun observeIdVendedor()
    {
        settingPrefs.getIdVendedor.asLiveData().observe(this){
                idVendedor->
            binding.tvDataStore.text = idVendedor.toString()
        }
    }

}