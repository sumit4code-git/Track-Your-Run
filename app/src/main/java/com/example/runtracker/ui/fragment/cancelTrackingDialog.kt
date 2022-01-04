package com.example.runtracker.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runtracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//Created this fragment because if screen rotates while dialog is on screen dialog appearance goes so to fix it we created dialog in separate fragment
class cancelTrackingDialog : DialogFragment() {

    private var yesListner:(()->Unit)?=null

    fun setYesListner(listner:() ->Unit){
        yesListner=listner
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
           return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Cancel The Run")
                .setMessage("Are You Sure You Want To Delete the Current Run And Delete All its Data?")
                .setPositiveButton("Yes"){_,_ ->
                    yesListner?.let {yes->
                        yes()
                    }
                }
                .setNegativeButton("NO"){dialogInterface,_ ->
                    dialogInterface.cancel()

                }
                .create()
    }
}