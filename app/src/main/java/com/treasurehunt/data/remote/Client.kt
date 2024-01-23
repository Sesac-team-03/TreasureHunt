package com.treasurehunt.data.remote

import okhttp3.OkHttpClient

private const val HEADER_USER_AGENT = "User-Agent"
private const val APP_NAME = "TreasureHunt"
object Client {

    fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header(HEADER_USER_AGENT, APP_NAME)
                return@addNetworkInterceptor chain.proceed(builder.build())
            }.build()
    }
}