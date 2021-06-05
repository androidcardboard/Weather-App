package com.weather.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.weather.api.WeatherAPIClient
import com.weather.model.ForecastResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class FiveDaysViewModel(application: Application) : AndroidViewModel(application) {

    private val apiClient = WeatherAPIClient()
    private val disposable = CompositeDisposable()

    val forecastData = MutableLiveData<List<ForecastResponse.Forecast>>()
    var forecastDataList : List<ForecastResponse.Forecast> = ArrayList()
    val fiveDaysLoading = MutableLiveData<Boolean>()

    fun getForecastFromGps(latitude: String, longitude: String, units: String) {
        fiveDaysLoading.value = true
        disposable.add(
            apiClient.getForecastFromGps(latitude, longitude, units)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ForecastResponse>() {
                    override fun onSuccess(t: ForecastResponse) {
                        var forecastResponse = t
                        forecastDataList = forecastResponse.list!!
                        forecastData.value = forecastDataList
                        fiveDaysLoading.value = false
                        Log.i("res : ", "res")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("error : ", "error " + e.message + " " + e.printStackTrace())

                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}