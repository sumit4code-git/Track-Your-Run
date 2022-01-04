package com.example.runtracker.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runtracker.R
import com.example.runtracker.other.Constants.KEY_NAME
import com.example.runtracker.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.etName
import kotlinx.android.synthetic.main.fragment_settings.etWeight
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject
@AndroidEntryPoint
class SettingsFragment:Fragment(R.layout.fragment_settings) {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSHaredPrefs()
        callListners(view);
    }
    private fun callListners(view: View) {
        btnApplyChanges.setOnClickListener {
            val success=applyChangesToSharedPrefreances()
            if(success){
               Snackbar.make(view,"Saved Changes",Snackbar.LENGTH_SHORT).show()
            }
            else{
                Snackbar.make(view,"Please Enter All Details", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private  fun loadFieldsFromSHaredPrefs(){
        val name= sharedPreferences.getString(KEY_NAME,"")
        val weights=sharedPreferences.getFloat(KEY_WEIGHT,80f)

        etName.setText(name)
        etWeight.setText(weights.toString())

    }
    private  fun applyChangesToSharedPrefreances():Boolean{
        val nameText=etName.text.toString()
        val weightText=etWeight.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT,weightText.toFloat())
            .apply()
        val toolBarText="Let's Go $nameText"
        requireActivity().tvToolbarTitle.text=toolBarText
        return true
    }
}