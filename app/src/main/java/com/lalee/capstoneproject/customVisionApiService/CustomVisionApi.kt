package com.lalee.capstoneproject.customVisionApiService

import com.lalee.capstoneproject.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CustomVisionApi {

    companion object {
        private const val BASE_URL = "https://westeurope.api.cognitive.microsoft.com/"

        private const val API_KEY = BuildConfig.CUSTOMVISION_KEY

        fun createAPI(): CustomVisionApiSerivce {

            val httpOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor{
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original : Request = chain.request()
                        val request = original.newBuilder()
                            .header("Prediction-Key", API_KEY)
                            .header("Content-Type", "application/json")
                            .method(original.method, original.body)
                            .build()

                        return chain.proceed(request)
                    }

                })
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val customVisionApi = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return customVisionApi.create(CustomVisionApiSerivce::class.java)
        }

    }
}