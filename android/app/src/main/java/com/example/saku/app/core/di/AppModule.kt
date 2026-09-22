package com.example.saku.app.core.di

import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.AuthRepositoryImpl
import com.example.saku.app.core.data.repository.CustomerRepository
import com.example.saku.app.core.data.repository.CustomerRepositoryImpl
import com.example.saku.app.core.data.repository.LoanRepository
import com.example.saku.app.core.data.repository.LoanRepositoryImpl
import com.example.saku.app.core.data.repository.NotificationRepository
import com.example.saku.app.core.data.repository.NotificationRepositoryImpl
import com.example.saku.app.core.data.repository.WilayahRepository
import com.example.saku.app.core.data.repository.WilayahRepositoryImpl
import com.example.saku.app.core.database.AppDatabase
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.features.auth.forgotpassword.ForgotPasswordViewModel
import com.example.saku.app.features.auth.login.LoginViewModel
import com.example.saku.app.features.auth.register.RegisterViewModel
import com.example.saku.app.features.auth.verification.KycPendingViewModel
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.features.loans.apply.LoanApplyViewModel
import com.example.saku.app.features.loans.detail.LoanDetailViewModel
import com.example.saku.app.features.loans.revision.LoanRevisionViewModel
import com.example.saku.app.features.profile.edit.EditProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {
    single { TokenManager.getInstance(androidContext()) }
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().customerDao() }
    single { get<AppDatabase>().loanDao() }
    single { get<AppDatabase>().notificationDao() }
}

val networkModule = module {
    single { ApiClient.getAuthApiService(androidContext()) }
    single { ApiClient.getCustomerApiService(androidContext()) }
    single { ApiClient.getWilayahApiService() }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get()) }
    single<CustomerRepository> { CustomerRepositoryImpl(get(), get(), get()) }
    single<LoanRepository> { LoanRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get()) }
    single<WilayahRepository> { WilayahRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get(), get(), androidApplication()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { KycPendingViewModel(get(), get(), get(), get(), androidApplication()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { LoanApplyViewModel(get(), get()) }
    viewModel { LoanDetailViewModel(get()) }
    viewModel { LoanRevisionViewModel(get()) }
    viewModel { EditProfileViewModel(get(), get()) }
}

val appModules = listOf(
    coreModule,
    networkModule,
    repositoryModule,
    viewModelModule
)