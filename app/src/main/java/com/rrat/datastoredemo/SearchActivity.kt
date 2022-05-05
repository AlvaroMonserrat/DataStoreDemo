package com.rrat.datastoredemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.core.view.get
import com.rrat.datastoredemo.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = arrayOf("Alvaro", "Paly", "Pablo", "Ana", "Amapola", "Patricio", "Paola", "Daniel", "Daniela", "Doris")

        val userAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, user)

        binding.listSearch.adapter = userAdapter

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.search.clearFocus()
                if(user.contains(query))
                {
                    userAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return false
            }

        })

        binding.listSearch.setOnItemClickListener { adapterView, view, i, l ->
            Log.i("SEARCH", i.toString())
            Log.i("SEARCH", adapterView.adapter.getItem(i).toString())
            binding.search.setQuery(adapterView.adapter.getItem(i).toString(), false)
            binding.search.clearFocus()
        }

    }
}