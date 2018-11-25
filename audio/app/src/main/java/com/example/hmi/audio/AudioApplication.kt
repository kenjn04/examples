package com.example.hmi.audio

import android.app.Application
import com.example.hmi.audio.fabstraction.AudioServiceFA
import com.example.hmi.audio.fabstraction.AudioServiceProxy
import com.example.hmi.audio.repository.AssetDataProvider
import com.example.hmi.audio.repository.MediaSourceRepository
import com.example.hmi.audio.repository.MediaSourceRepositoryImpl
import com.example.hmi.audio.repository.USBDataProvider
import com.example.hmi.audio.usecase.*
import com.example.hmi.audio.viewmodel.MediaViewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class AudioApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(
                this.viewModelModule,
                this.useCaseModule,
                this.FAModule,
                this.repositoryModule
        ))
    }

    // Koin
    private val viewModelModule = module {
        viewModel { MediaViewModel(androidApplication(), get(), get(), get(), get(), get()) }
    }

    private val useCaseModule = module {
        factory { SetSongDataObserverTask(get()) }
        factory { GetSongListTask(get()) }
        factory { PlaySongTask(get()) }
        factory { StopSongTask(get()) }
        factory { SetSongTask(get()) }
    }

    private val FAModule = module {
        single<AudioServiceFA> { AudioServiceFA(androidContext(), get()) }
        single<AudioServiceProxy> { AudioServiceProxy() }
    }

    private val repositoryModule = module {
        single<MediaSourceRepository> { MediaSourceRepositoryImpl.getInstance(get(), get()) }
        single { AssetDataProvider.getInstance(androidContext()) }
        single { USBDataProvider.getInstance() }
    }
}