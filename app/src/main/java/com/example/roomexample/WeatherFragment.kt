package com.example.roomexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import com.example.test.databinding.FragmentWeatherBinding
import com.example.test.weather.WeatherViewModel


class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get the layout binding object
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)

        //get the viewModel
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        //configure the layout data binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = this



//        configure the spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.cities_ch
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        //item selection handler
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedPosition = parent?.selectedItemPosition
                selectedPosition?.let {
                    if (it != 0) { //not the first item (hint text only)
                        //send an Internet request to the weather website
                        viewModel.sendRetrofitRequest(viewModel.cities_en[it])
                    } else {
                        viewModel.selectedCityWeather.value = null
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return binding.root
    }

}