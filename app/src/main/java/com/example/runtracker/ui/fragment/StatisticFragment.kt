package com.example.runtracker.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runtracker.R
import com.example.runtracker.ui.viewmodels.MainViewModel
import com.example.runtracker.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
//Whenever we need to inject something into android component(here its fragment) we use above notation
class StatisticFragment:Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()
}