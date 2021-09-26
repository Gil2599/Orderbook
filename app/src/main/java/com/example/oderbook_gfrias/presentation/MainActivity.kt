package com.example.oderbook_gfrias.presentation

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oderbook_gfrias.R
import com.example.oderbook_gfrias.common.Constants
import com.example.oderbook_gfrias.data.model.MainInteractor
import com.example.oderbook_gfrias.data.model.repository.MainRepository
import com.example.oderbook_gfrias.data.remote.WebServicesProvider
import com.example.oderbook_gfrias.databinding.ActivityMainBinding
import com.example.oderbook_gfrias.presentation.recyvlerViewAdapters.MainRecyclerViewAdapter
import com.example.oderbook_gfrias.presentation.viewModel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private var asksAdapter: MainRecyclerViewAdapter?=null
    private var bidsAdapter: MainRecyclerViewAdapter?=null

    private var marketRequest = Constants.BTC_USD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        spinnersInit()

        webSocketInit()

        recyclerViewsInit()


        binding.etQuantity.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.setQuantity(binding.etQuantity.text.toString().toFloat())
                viewModel.calculateTotal()
            }
        }
        binding.etQuantity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                binding.etQuantity.clearFocus()
            }
            false
        }

    }

    private fun spinnersInit(){

        ArrayAdapter.createFromResource(
            this,
            R.array.markets_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMarket.adapter = adapter
        }

        binding.spinnerMarket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> { if (marketRequest != Constants.BTC_USD){
                        marketRequest = Constants.BTC_USD
                        viewModel.onStop()
                        webSocketInit()
                        viewModel.setMarket(Constants.BTC_USD_MARKET)
                        recyclerViewsInit()
                        }
                    }
                    1 -> {marketRequest = Constants.ETH_BTC
                        viewModel.onStop()
                        webSocketInit()
                        viewModel.setMarket(Constants.ETH_BTC_MARKET)
                        recyclerViewsInit()}
                }

            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        val items = Array(30) {it + 1}
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        binding.spinnerRows.adapter = adapter
        binding.spinnerRows.setSelection(14)

        binding.spinnerRows.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setMaxSize(position+1)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                viewModel.setMaxSize(15) //Default Value
            }
        }
    }

    private fun recyclerViewsInit(){

        viewModel.asks.observe(this, {

            asksAdapter = MainRecyclerViewAdapter(this, it as Array<Pair<Float, Float>>, false){ price ->
                viewModel.setPrice(price)
                viewModel.calculateTotal()
            }
            binding.recyclerViewAsks.adapter = asksAdapter

        })
        binding.recyclerViewBids.setBackgroundResource(R.drawable.bid_row_bkg)
        binding.recyclerViewAsks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAsks.hasFixedSize()
        binding.recyclerViewAsks.isNestedScrollingEnabled = false

        viewModel.bids.observe(this, {

            bidsAdapter = MainRecyclerViewAdapter(this, it as Array<Pair<Float, Float>>, true) { price ->
                viewModel.setPrice(price)
                viewModel.calculateTotal()
            }
            binding.recyclerViewBids.adapter = bidsAdapter

        })
        binding.recyclerViewBids.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBids.isNestedScrollingEnabled = false

    }

    private fun webSocketInit(){

        val servicesProvider = WebServicesProvider(marketRequest)
        val repository = MainRepository(servicesProvider)

        viewModel = MainViewModel(MainInteractor(repository))
        viewModel.setMarket(Constants.BTC_USD_MARKET)

        viewModel.setMaxSize(binding.spinnerRows.selectedItemPosition + 1)
        binding.viewModel = viewModel

        lifecycleScope.launch {
            viewModel.subscribeToSocketEvents()
        }
    }

}