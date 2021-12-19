package com.example.runtracker.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runtracker.R
import com.example.runtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runtracker.other.Constants.ACTION_STOP_SERVICE
import com.example.runtracker.other.Constants.MAP_ZOOM
import com.example.runtracker.other.Constants.POLYLINE_COLOUR
import com.example.runtracker.other.Constants.POLYLINE_WIDTH
import com.example.runtracker.other.TrackingUtility
import com.example.runtracker.service.TrackingService
import com.example.runtracker.service.polyline
import com.example.runtracker.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
//Whenever we need to inject something into android component(here its fragment) we use above notation
class TrackingFragment:Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private  var isTracking=false
    private var pathPoints= mutableListOf<polyline>()

    private var map:GoogleMap?=null

    private var currTimeMilllis=0L

    private var menu: Menu?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            ToggleRun()
        }
//        async gets called whenever fragment created
        mapView.getMapAsync {
            map=it
            addALLpolylines()
        }
        SubscribeToObservers()
    }
//    to subscribe to our liveData object
    private fun SubscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPoylines()
            moveCameraToUser()
        })

    TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
        currTimeMilllis=it
        val formattedTime=TrackingUtility.getFormattedStopWatch(currTimeMilllis,true)
        tvTimer.text=formattedTime
    })
    }
//    toggle between run i.e, to start ,stop  between runs.
    private fun ToggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }
//to inflate menu and cancel the run
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currTimeMilllis > 0L){
            this.menu?.getItem(0)?.isVisible=true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking->{
                ShowCancelDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun ShowCancelDialog(){
        val dialog=MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel The Run")
            .setMessage("Are You Sure You Want To Delete the Current Run And Delete All its Data?")
            .setPositiveButton("Yes"){_,_ ->
                stopRun()
            }
            .setNegativeButton("NO"){dialogInterface,_ ->
                dialogInterface.cancel()

            }
            .create()
        dialog.show()
    }
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }
//    observe data from our service and react to those changes
    private  fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            btnToggleRun.text="Start"
            btnFinishRun.visibility=View.VISIBLE
        }else{
            btnToggleRun.text="Stop"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.GONE
        }
    }
//    to move camera towards user whenever new position in list are added
    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }
//    also used to do same create polyline when screen is rotated
    private fun addALLpolylines(){
        for(polyline in pathPoints){
            val polylineOption=PolylineOptions()
                .color(POLYLINE_COLOUR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOption)
        }
    }
//    use to connect two last points
private fun addLatestPoylines(){
    if(pathPoints.isNotEmpty()&& pathPoints.last().size>1){
        val preLastLatlong=pathPoints.last()[pathPoints.last().size-2]
        val lastLatLng=pathPoints.last().last()
        val polylineOption=PolylineOptions()
            .color(POLYLINE_COLOUR)
            .width(POLYLINE_WIDTH)
            .add(preLastLatlong)
            .add(lastLatLng)
        map?.addPolyline(polylineOption)

    }
}
    private fun sendCommandToService(action:String)=
        Intent(requireContext(),TrackingService::class.java).also {
            it.action=action
            requireContext().startService(it)
        }
//Setting up Mapview Lifecycle with fragment
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}