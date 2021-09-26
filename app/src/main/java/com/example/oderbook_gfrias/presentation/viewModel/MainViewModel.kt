package com.example.oderbook_gfrias.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oderbook_gfrias.data.model.MainInteractor
import com.example.oderbook_gfrias.data.remote.dto.MarketUpdateDto
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.consumeAsFlow

class MainViewModel (private val interactor: MainInteractor): ViewModel() {

    var gson = Gson()

    private var maxSize = 15

    private var _spread = MutableLiveData(0F)
    val spread: LiveData<Float> = _spread

    private var _asks = MutableLiveData<Array<Pair<Float, Float>>>()
    var asks: LiveData<Array<Pair<Float, Float>>> = _asks

    private var _bids = MutableLiveData<Array<Pair<Float, Float>>>()
    var bids: LiveData<Array<Pair<Float, Float>>> = _bids

    private var _bidsHashMap = HashMap<Float, Float>()
    private var _asksHashMap = HashMap<Float, Float>()

    private var _selectedPrice = MutableLiveData(0F)
    val selectedPrice: LiveData<Float> = _selectedPrice

    private var _quantity = MutableLiveData(0F)
    val quantity: LiveData<Float> = _quantity

    private var _total = MutableLiveData(0F)
    val total: LiveData<Float> = _total

    private var _market = MutableLiveData<Pair<String, String>>()
    val market: LiveData<Pair<String, String>> = _market

    private var _update = MutableLiveData<MarketUpdateDto>()

    @ExperimentalCoroutinesApi
    fun subscribeToSocketEvents() {
        viewModelScope.launch {
            try {
                interactor.startSocket().consumeEach {

                    if (it.exception == null) {

                        _update.value = gson.fromJson(it.text, MarketUpdateDto::class.java)

                        filter(_update.value)

                        Log.e("Incoming", _update.value.toString())

                    } else {
                        onSocketError(it.exception)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun onSocketError(ex: Throwable) {
        Log.e("Error occurred :", "${ex.message}")
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        interactor.stopSocket()
        super.onCleared()
    }

    private fun filter(update: MarketUpdateDto?){

        update?.changes?.get(0)?.get(0)?.let { side -> //Check if its a buy or sell order
            if (side == "sell"){
                val price = update.changes[0][1].toFloat()
                val size = update.changes[0][2].toFloat()

                if (size > 0) {
                    if (_asksHashMap.size < maxSize) {
                        _asksHashMap[price] = size
                    } else {
                        for (key in _asksHashMap.toSortedMap(reverseOrder()).keys) {

                            if (price <= key) {
                                _asksHashMap.remove(key)?.let { _asksHashMap.put(price, it) }
                                break
                            }
                        }
                    }
                    if (_asksHashMap.size == maxSize)
                        _asks.value = _asksHashMap.toSortedMap(reverseOrder()).toList().toTypedArray()

                }else
                    if (price in _asksHashMap.keys){
                        _asksHashMap.remove(price)
                    }
                getSpread()
            }

            if (side == "buy"){
                val price = update.changes[0][1].toFloat()
                val size = update.changes[0][2].toFloat()

                if (size > 0) {
                    if (_bidsHashMap.size < maxSize) {
                        _bidsHashMap[price] = size
                    } else {
                        for (key in _bidsHashMap.toSortedMap().keys) {
                            if (price >= key) {
                                _bidsHashMap.remove(key)?.let { _bidsHashMap.put(price, it) }
                                break
                            }
                        }
                    }
                    if (_bidsHashMap.size == maxSize)
                        _bids.value = _bidsHashMap.toSortedMap(reverseOrder()).toList().toTypedArray()


                }else
                    if (price in _bidsHashMap.keys){
                        _bidsHashMap.remove(price)
                    }
                getSpread()

                }
            }

        }

    private fun getSpread(){
        _spread.value = _bidsHashMap.keys.toList().maxOrNull()?.let {
            _asksHashMap.keys.toList().minOrNull()
                ?.minus(it)
        }
   }

    fun setMaxSize(value: Int){
        maxSize = value
    }

    private fun clearData(){
        _asksHashMap.clear()
        _bidsHashMap.clear()

    }

    fun onStop(){
        interactor.stopSocket()
        clearData()

    }

    fun setQuantity(amount: Float){
        _quantity.value = amount
    }

    fun setPrice(price: Float){
        _selectedPrice.value = price
    }

    fun calculateTotal(){
        _total.value = quantity.value?.times(selectedPrice.value!!)
    }

    fun setMarket(market: Pair<String, String>){
        _market.value = market
    }
}



