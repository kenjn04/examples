package com.example.hmi.audio

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Intent
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.audio.AudioRepositoryImpl
import com.example.hmi.audio.repository.mediasource.dataprovider.AssetDataProvider
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepositoryImpl
import com.example.hmi.audio.repository.mediasource.dataprovider.USBDataProvider
import com.example.hmi.audio.usecase.*
import com.example.hmi.audio.viewmodel.MediaViewModel
import com.example.hmi.audio.widget.AudioAppWidget
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

        requestUpdateWidget()

    }

    fun requestUpdateWidget() {
        val intent = Intent(baseContext, AudioAppWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        sendBroadcast(intent)
    }

    // Koin dependency injection
    private val viewModelModule = module {
        viewModel { MediaViewModel(androidApplication(), get(), get(), get(), get(), get()) }
    }

    private val useCaseModule = module {
        factory { InitialDataObserveTask(get(), get(), get()) }
        factory { SongListObtainTask(get()) }
        factory { SongOperationTask(get(), get(), get()) }
        factory { SongToPlaySetTask(get(), get()) }
        factory { RepeatModeIncrementTask(get()) }
    }

    private val FAModule = module {
        single<AudioFAbstraction> { AudioFAbstraction.getInstance(androidContext()) }
    }

    private val repositoryModule = module {
        single<AudioRepository> { AudioRepositoryImpl.getInstance() }
        single<MediaSourceRepository> { MediaSourceRepositoryImpl.getInstance(get(), get()) }
        single { AssetDataProvider.getInstance(androidContext()) }
        single { USBDataProvider.getInstance() }
    }
}