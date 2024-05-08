package com.example.currency.di

import com.example.currency.data.local.MongoImpl
import com.example.currency.data.local.PreferencesImpl
import com.example.currency.data.remote.api.CurrencyApiServiceImpl
import com.example.currency.domain.CurrencyApiService
import com.example.currency.domain.MongoRepository
import com.example.currency.domain.PreferencesRepository
import com.example.currency.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<MongoRepository>{ MongoImpl()}
    single <PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    factory {
        HomeViewModel(
            preferences = get(),
            mongoDb = get(),
            api = get()
        )
    }
}

fun initializeKoin(){
    startKoin {
        modules(appModule)
    }
}