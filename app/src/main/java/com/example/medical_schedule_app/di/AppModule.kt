package com.example.medical_schedule_app.di

import android.content.Context
import com.example.medical_schedule_app.data.api.AdminApiService
import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.api.DoctorApiService
import com.example.medical_schedule_app.data.api.ReceptionistApiService
import com.example.medical_schedule_app.data.repositories.AdminRepository
import com.example.medical_schedule_app.data.repositories.DoctorRepository
import com.example.medical_schedule_app.data.repositories.ProfileRepository
import com.example.medical_schedule_app.data.repositories.ReceptionistRepository
import com.example.medical_schedule_app.data.repositories.UserRepository
import com.example.medical_schedule_app.utils.ActualSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideActualSessionManager(@ApplicationContext context: Context): ActualSessionManager {
        return ActualSessionManager(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideDoctorRepository(doctorApiService: DoctorApiService): DoctorRepository {
        return DoctorRepository(doctorApiService)
    }

    @Provides
    @Singleton
    fun provideReceptionistRepository(receptionistApiService: ReceptionistApiService): ReceptionistRepository {
        return ReceptionistRepository(receptionistApiService)
    }
    @Provides
    @Singleton
    fun provideAdminRepository(api: AdminApiService): AdminRepository {
        return AdminRepository(api)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(apiService: ApiService): ProfileRepository {
        return ProfileRepository(apiService)
    }

}