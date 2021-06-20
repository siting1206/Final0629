package com.example.roomexample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.roomexample.database.Scene
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.databinding.FragmentAddBinding
import kotlinx.coroutines.newSingleThreadContext

//fragment to add a new scene or edit an existing scene
class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var viewModel: MyViewModel
    private var editMode = false
    private var newScene = Scene("", "", "","", 0,"","")
    private val PICKUPIMAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        //shared viewmodel with the activity
        viewModel = ViewModelProvider(requireActivity(),
            MyViewModelFactory(requireActivity().application)).get(MyViewModel::class.java)

        //retrieve the passed argument (selected scene's id or 0 (add data))
        val args = AddFragmentArgs.fromBundle(requireArguments())
        editMode = (args.rawId != 0L)
        if (editMode) { //go editing the item
            viewModel.getScene(args.rawId)
            loadData()
        }
        else
            viewModel.selectedScene.value = null

        //set an observer to the liveData and hence update the UI
        viewModel.selectedScene.observe(viewLifecycleOwner, Observer {
            //do data binding in the layout
            binding.scene = it
        })

        //get input focus
        binding.nameEdit.requestFocus()

        //configure the spinner to select one city
        val adapter = ArrayAdapter(this.requireContext(), R.layout.my_spinner_layout, viewModel.cityList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cityPicker.adapter = adapter
        //set default selection
        if (newScene.city.isNotEmpty()) binding.cityPicker.setSelection(viewModel.cityList.indexOf(newScene.city))
        //item selection handler
        binding.cityPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = parent?.selectedItem.toString()
                selectedCity?.let {
                  newScene.city = it
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        with (binding.cityPicker) {
//            textSize = 50F
//            minValue = 0
//            maxValue = viewModel.cityList.size - 1
//            displayedValues = viewModel.cityList
//            if (newScene.city.isNotEmpty())
//                value = viewModel.cityList.indexOf(newScene.city)
//            wrapSelectorWheel = true
//            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//            setOnValueChangedListener { picker, oldVal, newVal ->
//                newScene.city = viewModel.cityList[newVal]
//            }
//        }

        //enable the photo pickup
        binding.selButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, PICKUPIMAGE)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun loadData() {
        //restore the old data first
        viewModel.selectedScene.value?.let {
            newScene = it.copy()
        }
    }

    private fun saveData() {
        newScene.name = binding.nameEdit.text.toString()
        newScene.address = binding.addressEdit.text.toString()
        newScene.time = binding.timeEdit.text.toString()
        newScene.food = binding.foodEdit.text.toString()
        newScene.attraction = binding.attractionEdit.text.toString()

        //save data into the database
        if (editMode)
            viewModel.updateScene(newScene)
        else
            viewModel.insertScene(newScene)

        //simulate the press of the back button
        activity?.onBackPressed()
    }

    // get the photo file path returned from the intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKUPIMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let {
                        newScene.photoFile = it.toString()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.file_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        // Hide the keyboard.
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}