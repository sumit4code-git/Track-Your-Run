package com.example.runtracker.repositories

import com.example.runtracker.db.Run
import com.example.runtracker.db.RunDAO
import javax.inject.Inject
//to provide function of  database so that function we provided in DAO object
//for our view Model later On so we'll need dao object here which we get by injecting
class MainRepository @Inject constructor(
    val runDao:RunDAO
){
    suspend fun insertRun(run:Run)=runDao.insertRun(run)

    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)

    fun getAllRunsSortedByDate()=runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance()=runDao.getAllRunsSortedByDistance()

     fun getAllRunsSortedByTimeInMillis()=runDao.getAllRunsSortedByTimeInMillis()

     fun getAllRunsSortedByAvgSpeed()=runDao.getAllRunsSortedByAvgSpeed()

     fun getAllRunsSortedByCaloriesBurned()=runDao.getAllRunsSortedByCaloriesBurned()

     fun getTotalAvgSpeed()=runDao.getTotalAvgSpeed()

    fun getTotalDistance()=runDao.getTotalDistance()

    fun getTotalCaloriesBurned()=runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()
}