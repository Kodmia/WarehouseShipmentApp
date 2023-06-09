package ru.dikoresearch.warehouse.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Configuration
import dagger.*
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.dikoresearch.warehouse.App
import ru.dikoresearch.warehouse.MainActivity
import ru.dikoresearch.warehouse.data.repository.WarehouseRepositoryImpl
import ru.dikoresearch.warehouse.domain.repository.WarehouseRepository
import ru.dikoresearch.warehouse.data.repository.WarehouseService
import ru.dikoresearch.warehouse.domain.workers.WorkersFactory
import ru.dikoresearch.warehouse.presentation.camera.CameraViewModel
import ru.dikoresearch.warehouse.presentation.login.LogInViewModel
import ru.dikoresearch.warehouse.presentation.orderdetails.OrderDetailsViewModel
import ru.dikoresearch.warehouse.presentation.orderslist.OrdersListViewModel
import ru.dikoresearch.warehouse.presentation.utils.BASE_URL
import ru.dikoresearch.warehouse.presentation.utils.SHARED_PREFERENCES_NAME
import ru.dikoresearch.warehouse.presentation.utils.token
import javax.inject.Singleton

@Component(modules = [AppModule::class, AppBindsModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: Fragment)
    fun inject(app: App)

    val viewModelFactory: MultiViewModelFactory

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }

}

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sharedPreferences: SharedPreferences
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/json")
                    .addHeader("Authorization", "Bearer ${sharedPreferences.token()}")
                    .build()
                return@addInterceptor chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("$BASE_URL/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWarehouseService(
        retrofit: Retrofit
    ): WarehouseService {
        return retrofit.create(WarehouseService::class.java)
    }
}

@Module(includes = [NetworkModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWarehouseRepository(
        warehouseService: WarehouseService,
        sharedPreferences: SharedPreferences
    ): WarehouseRepository {
        return WarehouseRepositoryImpl(
            warehouseService = warehouseService,
            sharedPreferences = sharedPreferences
        )
    }

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workersFactory: WorkersFactory
    ): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workersFactory)
            .build()
    }

}

@Module
interface AppBindsModule{
    @Binds
    @[IntoMap ViewModelKey(LogInViewModel::class)]
    fun provideLoginViewModel(logInViewModel: LogInViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(OrdersListViewModel::class)]
    fun provideOrdersListViewModel(ordersListViewModel: OrdersListViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(OrderDetailsViewModel::class)]
    fun provideOrderDetailsViewModel(orderDetailsViewModel: OrderDetailsViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(CameraViewModel::class)]
    fun provideCameraViewModel(cameraViewModel: CameraViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: MultiViewModelFactory): ViewModelProvider.Factory
}