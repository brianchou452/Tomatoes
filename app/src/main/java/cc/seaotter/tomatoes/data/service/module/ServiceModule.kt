package cc.seaotter.tomatoes.data.service.module

import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.data.service.impl.AccountServiceImpl
import cc.seaotter.tomatoes.data.service.impl.DatabaseServiceImpl
import cc.seaotter.tomatoes.data.service.impl.LogServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    abstract fun provideDatabaseService(impl: DatabaseServiceImpl): DatabaseService
}