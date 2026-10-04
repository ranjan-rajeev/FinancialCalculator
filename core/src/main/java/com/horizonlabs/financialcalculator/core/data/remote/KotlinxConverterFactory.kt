package com.horizonlabs.financialcalculator.core.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Retrofit converter backed by kotlinx.serialization.
 *
 * Gson cannot instantiate polymorphic sealed hierarchies (dashboard components,
 * input field configs), so JSON decoding goes through kotlinx instead.
 */
class KotlinxConverterFactory(
    private val json: Json
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val serializer = json.serializersModule.serializer(type)

        return Converter { body ->
            val rawBody = body.string()
            if (rawBody.isBlank()) {
                throw IllegalStateException("Empty response body for $type")
            }
            json.decodeFromString(serializer, rawBody)
        }
    }
}
