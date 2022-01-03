package com.example.androidflowdemoapp.di.module

import com.example.androidflowdemoapp.network.ApiService
import com.example.androidflowdemoapp.repository.CommentsRepository
import com.example.androidflowdemoapp.utils.AppConfig
import com.example.androidflowdemoapp.viewmodel.CommentsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentModule {

    @Singleton
    @Provides
    fun provideCommentsViewModel(repository: CommentsRepository): CommentsViewModel {
        return CommentsViewModel(repository)
    }

    @Singleton
    @Provides
    fun provideCommentRepository(
        api: ApiService,
    ): CommentsRepository {
        return CommentsRepository(
            api
        )
    }

    @Singleton
    @Provides
    fun provideApi(): ApiService {
        return AppConfig.ApiService()
    }

}