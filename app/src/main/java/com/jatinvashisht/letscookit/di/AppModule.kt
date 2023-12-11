package com.jatinvashisht.letscookit.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.data.local.RecipeDatabase
import com.jatinvashisht.letscookit.data.remote.RecipeApi
import com.jatinvashisht.letscookit.data.remote.repository.RecipeRepositoryImpl
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRecipeApi(): RecipeApi = Retrofit
        .Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val requestBody = request.body

                    // Log ukuran data yang dikirim
                    val contentLength = requestBody?.let {
                        try {
                            it.contentLength()
                        } catch (e: Exception) {
                            // Handle exception, jika terjadi kesalahan
                            null
                        }
                    }

                    if (contentLength != null) {
                        Log.d("Network", "Sent data size: $contentLength bytes")
                    } else {
                        Log.d("Network", "Sent data size: unknown (null) bytes")
                    }

//                    Log.d("Network", "Sent data size: $contentLength bytes")
                    Log.d("Network", "Request Body: ${requestBody?.toString()}")

                    chain.proceed(request)
                }
                .build()
        )
        .build()
        .create(RecipeApi::class.java)

    @Provides
    @Singleton
    fun providesRecipeDatabase(app: Application): RecipeDatabase = Room
        .databaseBuilder(app, RecipeDatabase::class.java, RecipeDatabase.DATABASE_NAME)
        .build()

    @Provides
    @Singleton
    fun providesRecipeRepository(recipeApi: RecipeApi, database: RecipeDatabase): RecipeRepository =
        RecipeRepositoryImpl(recipeApi = recipeApi, recipeDatabase = database)


}
