package com.example.runtracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runtracker.db.RunningDatabase
import com.example.runtracker.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runtracker.other.Constants.KEY_NAME
import com.example.runtracker.other.Constants.KEY_WEIGHT
import com.example.runtracker.other.Constants.RUNNING_DATABASE_NAME
import com.example.runtracker.other.Constants.SHARED_PREFRENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app:Context)= Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()
    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase)=db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPrefences(@ApplicationContext app:Context)=
     app.getSharedPreferences(SHARED_PREFRENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPrefrences:SharedPreferences)=sharedPrefrences.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPrefrences:SharedPreferences)=sharedPrefrences.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeEntry(sharedPrefrences:SharedPreferences)=
        sharedPrefrences.getBoolean(
        KEY_FIRST_TIME_TOGGLE,true)
}