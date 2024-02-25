package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetachWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetachWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }
        })

    }

    private fun fetachWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData("jaipur","1de6d36b363b6061966bea8ef8acf260" ,"metric")
        response.enqueue(object : Callback<Weatherapp>{
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val sunrise = responseBody.sys.sunrise
                    val sunset = responseBody.sys.sunset
                    val wind = responseBody.wind.speed
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min



                   // Log.d(TAG,"onResponse: $temperature")
                    binding.temp.text = "$temperature 'C"
                    binding.condwea.text = condition
                    binding.max.text = "Max Temp: $maxTemp 'C"
                    binding.min.text = "Min Temp: $minTemp 'C"
                    binding.humidity.text ="$humidity %"
                    binding.windspeed.text ="$wind m/s"
                    binding.sunrise.text ="${time(sunrise.toLong())}"
                    binding.sunset.text ="${time(sunrise.toLong())}"
                    binding.sea.text ="$sealevel hPa"
                    binding.conditions.text =condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityname.text = "$cityName"

                    changeImages(condition)

                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImages(conditions : String) {
        when(conditions){
            "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloudy)
            }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.mainn)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rainy1)
                binding.lottieAnimationView.setAnimation(R.raw.rainy)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloudy)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunnyback)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}