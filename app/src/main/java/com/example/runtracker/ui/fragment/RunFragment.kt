package com.example.runtracker.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runtracker.R
import com.example.runtracker.adapter.RunAdapter
import com.example.runtracker.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runtracker.other.SortType
import com.example.runtracker.other.TrackingUtility
import com.example.runtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
//Whenever we need to inject something into android component(here its fragment) we use above notation
class RunFragment:Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

    private val viewModel:MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()
        initComponents()
    }

    private fun initComponents() {
//        To sort According to  initial selection of spinner
        when(viewModel.sortType){
            SortType.DATE->spFilter.setSelection(1)
            SortType.RUNNING_TIME->spFilter.setSelection(1)
            SortType.DISTANCE->spFilter.setSelection(2)
            SortType.CALORIES_BURNT->spFilter.setSelection(4)
            SortType.AVG_SPEED->spFilter.setSelection(3)
        }
        spFilter.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                when(pos){
                    0-> viewModel.sortRuns(SortType.DATE)
                    1-> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2-> viewModel.sortRuns(SortType.DISTANCE)
                    3-> viewModel.sortRuns(SortType.AVG_SPEED)
                    4-> viewModel.sortRuns(SortType.CALORIES_BURNT)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    //Setting RecyclerView To show saved runs in database
    private fun setUpRecyclerView()=rvRuns.apply {
        runAdapter=RunAdapter()
        adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())
    }
//    Here we are requesting permission in all possible ways till line 72
    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return

        }
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "Hey we need that permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                this,
                "Hey we need that permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
            EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}