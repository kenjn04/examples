package jp.co.sample.hmi.home

import android.app.Application
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.UserManager
import jp.co.sample.hmi.home.repository.HomeRepository
import jp.co.sample.hmi.home.repository.HomeRepositoryImpl
import jp.co.sample.hmi.home.usecase.AddWidgetTask
import jp.co.sample.hmi.home.usecase.DeleteWidgetTask
import jp.co.sample.hmi.home.usecase.GetCurrentWidgetsTask
import jp.co.sample.hmi.home.usecase.GetInstalledWidgetListTask
import jp.co.sample.hmi.home.viewmodel.HomeViewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class HomeApplication : Application() {

    private lateinit var appWidgetManager: AppWidgetManager

    private lateinit var userManager: UserManager

    private lateinit var appWidgetHost: AppWidgetHost

    override fun onCreate() {
        super.onCreate()

        setManagers()

        startKoin(this, listOf(
                this.viewModelModule,
                this.useCaseModule,
                this.repositoryModule
        ))
    }

    private fun setManagers() {
        // TODO: Need to check if this is correct way
        appWidgetManager = AppWidgetManager.getInstance(this)
        userManager = getSystemService(Context.USER_SERVICE) as UserManager
    }

    // Koin dependency injection
    private val viewModelModule = module {
        viewModel { HomeViewModel(androidApplication(), get(), get(), get(), get()) }
    }

    private val useCaseModule = module {
        factory { GetInstalledWidgetListTask(get()) }
        factory { GetCurrentWidgetsTask(get()) }
        factory { AddWidgetTask(get()) }
        factory { DeleteWidgetTask(get()) }
    }

    private val repositoryModule = module {
        single<HomeRepository> { HomeRepositoryImpl.getInstance(appWidgetManager, userManager) }
    }
}