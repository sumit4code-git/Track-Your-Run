package com.example.runtracker.ui .fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.runtracker.R
import com.example.runtracker.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runtracker.other.Constants.KEY_NAME
import com.example.runtracker.other.Constants.KEY_WEIGHT
import com.example.runtracker.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment:Fragment(R.layout.fragment_setup) {

    private val TAG = "SetupFragment"

    @Inject
    lateinit var sharedPref:SharedPreferences

//here we are using primitive datatype and so we can't use lateinit var so we are using @set:Inject
    @set:Inject
    var isFirstTimeEntry=true

    @set:Inject
    var name="User"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(savedInstanceState,view)
        callListners()

    }

//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val toolBarText2="Let's Go,$name"
//        Log.d(TAG, "initComponents: "+toolBarText2)
//        requireActivity()?.tvToolbarTitle?.text=toolBarText2
//    }


    private fun initComponents(savedInstanceState: Bundle?,view:View) {
        if(!isFirstTimeEntry){
            val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,savedInstanceState,navOptions
            )
        }
    }
    private fun callListners() {
        tvContinue.setOnClickListener {
            val success=writePersonalDataToSharedPref()
            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
            else{
                Snackbar.make(requireView(),"Please Enter All Details",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
//To Edit Name and weight data in SharedPrefrences
    private fun writePersonalDataToSharedPref():Boolean{
        val name=etName.text.toString()
        val weight=etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
            .apply()

        val toolBarText="let's go,$name"
        requireActivity().tvToolbarTitle.text=toolBarText
        return true


    }
}