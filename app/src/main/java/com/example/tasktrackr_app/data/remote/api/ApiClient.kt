package com.example.tasktrackr_app.data.remote.api

import android.content.Context
import com.example.tasktrackr_app.data.remote.interceptor.TokenInterceptor
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private class DateSerializer : JsonSerializer<Date> {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return if (src == null) {
                JsonNull.INSTANCE
            } else {
                JsonPrimitive(dateFormat.format(src))
            }
        }
    }

    private class DateDeserializer : JsonDeserializer<Date> {
        private val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
        )

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
            if (json == null || json.isJsonNull) return null

            val dateString = json.asString

            for (format in formats) {
                try {
                    format.timeZone = TimeZone.getTimeZone("UTC")
                    return format.parse(dateString)
                } catch (e: Exception) {
                }
            }

            throw JsonParseException("Unable to parse date: $dateString")
        }
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .setLenient()
            .create()
    }

    private fun okHttpClient(context: Context, authApiNoInterceptor: AuthApi): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val tokenInterceptor = TokenInterceptor(context, authApiNoInterceptor)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    private fun retrofit(context: Context): Retrofit {
        val gson = createGson()

        val defaultClient = OkHttpClient.Builder().build()
        val defaultRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(defaultClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val authApiNoInterceptor = defaultRetrofit.create(AuthApi::class.java)

        val clientWithInterceptor = okHttpClient(context, authApiNoInterceptor)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithInterceptor)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun authApi(context: Context): AuthApi =
        retrofit(context).create(AuthApi::class.java)

    fun userApi(context: Context): UserApi =
        retrofit(context).create(UserApi::class.java)

    fun teamApi(context: Context): TeamApi =
        retrofit(context).create(TeamApi::class.java)

    fun taskApi(context: Context): TaskApi =
        retrofit(context).create(TaskApi::class.java)

    fun observationApi(context: Context): ObservationApi =
        retrofit(context).create(ObservationApi::class.java)

    fun projectApi(context: Context): ProjectApi =
        retrofit(context).create(ProjectApi::class.java)
}
