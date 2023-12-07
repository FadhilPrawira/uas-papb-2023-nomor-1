package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the API key from the application context
        val apiKey = applicationContext.getString(R.string.openweathermap_api_key)

        // Create the Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the WeatherService interface
        val weatherService = retrofit.create(WeatherService::class.java)

        // Create the view model
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        // Set up the observer to update the view when the data changes
        viewModel.weather.observe(this, Observer { weather ->
            // Update the view with the new weather data
            updateView(weather)
        })

        // Set up the button click listener
        findViewById<Button>(R.id.button_update).setOnClickListener {
            // Update the weather data
            viewModel.updateWeather(apiKey, "Jakarta")
        }
    }

    private fun updateView(weather: Weather) {
        // Set the city name
        findViewById<TextView>(R.id.text_view_city).text = weather.cityName

        // Set the current temperature
        findViewById<TextView>(R.id.text_view_temperature).text = weather.temperature.toString()

        // Set the weather condition
        findViewById<TextView>(R.id.text_view_condition).text = weather.condition
    }

    fun updateWeather(apiKey: String, cityName: String) {
    // Create the request
    val request = weatherService.getWeather(apiKey, cityName)

    // Execute the request
    request.enqueue(object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
            // If the response is successful
            if (response.isSuccessful) {
                // Get the weather data
                val weather = response.body()

                // Update the view model
                viewModel.weather.value = weather
            } else {
                // Log the error
                Log.e("WeatherApp", "Error getting weather data: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Weather>, t: Throwable) {
            // Log the error
            Log.e("WeatherApp", "Error getting weather data: ${t.message}")
        }

}
