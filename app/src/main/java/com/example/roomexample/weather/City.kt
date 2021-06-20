package com.example.test.weather

//define data class as the data source of the spinner or recyclerview
data class City(val eName: String, val cName: String)

data class CityWeather(val name: String, val temperature: Double, val description: String, val iconName: String)