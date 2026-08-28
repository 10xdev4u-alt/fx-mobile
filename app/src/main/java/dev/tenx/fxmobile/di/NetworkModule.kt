package dev.tenx.fxmobile.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tenx.fxmobile.data.remote.KiloApi
import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.KiloRepositoryImpl
import dev.tenx.fxmobile.data.remote.PreferencesManager
import dev.tenx.fxmobile.data.remote.TokenProvider
import dev.tenx.fxmobile.data.remote.TokenProviderImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.kilo.ai/api/gateway")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideKiloApi(retrofit: Retrofit): KiloApi {
        return retrofit.create(KiloApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(preferencesManager: PreferencesManager): TokenProvider {
        return TokenProviderImpl(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideKiloRepository(
        api: KiloApi,
        tokenProvider: TokenProvider
    ): KiloRepository {
        return KiloRepositoryImpl(api, tokenProvider)
    }
}
