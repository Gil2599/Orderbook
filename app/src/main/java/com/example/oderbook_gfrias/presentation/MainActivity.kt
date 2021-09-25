package com.example.oderbook_gfrias.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.oderbook_gfrias.data.model.MainInteractor
import com.example.oderbook_gfrias.data.model.MainRepository
import com.example.oderbook_gfrias.data.model.MainViewModel
import com.example.oderbook_gfrias.data.remote.CoinbaseWebSocketListener
import com.example.oderbook_gfrias.data.remote.WebServicesProvider
import com.example.oderbook_gfrias.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val servicesProvider = WebServicesProvider()
        val repository = MainRepository(servicesProvider)

        viewModel = MainViewModel(MainInteractor(repository))

        Log.e("Error", "Main is working")

        viewModel.subscribeToSocketEvents()

    }

}