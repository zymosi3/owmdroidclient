package com.zymosi3.owmdroidclient

import android.os.AsyncTask
import android.util.Log
import com.zymosi3.util.logThreadName
import com.zymosi3.util.retry
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Performs requests to Open Weather Map API
 */
public class OwmClient(private val apiKey: String) {

    companion object {

        private val LOG_TAG = "OwmClient"

        private val API_URL = "http://api.openweathermap.org/data"
        private val API_VERSION = "2.5"
        private val WEATHER_URL = "$API_URL/$API_VERSION/weather"
        private val LAT = "lat"
        private val LON = "lon"
        private val API_KEY = "appid"

        private val READ_TIMEOUT_MS = 10000
        private val CONNECT_TIMEOUT_MS = 15000
    }

    /**
     * Gets current weather by latitude and longitude.
     * If some field is absent in owm response (this can happen) the of this field from default will be used.
     * @throws OwmClientException if something went wrong and you should retry your request
     * @throws RuntimeException if caught unexpected error
     */
    public fun getWeather(lat: Double, lon: Double, default: Weather): Weather {
        Log.d(LOG_TAG, "${logThreadName()} getWeather, lat = $lat, lon = $lon")

        defaultWeather = default

        val urlBuilder = StringBuilder(WEATHER_URL).
                append("?").appendParam(LAT, lat).append("&").appendParam(LON, lon).appendApiKey()

        try {
            val url = URL(urlBuilder.toString())
            val httpGet = url.httpGet()
            httpGet.connect()
            try {
                val response = httpGet.responseCode
                if (response != HttpURLConnection.HTTP_OK)
                    throw OwmClientException("OWM response code " + response)

                val content = httpGet.content()
                Log.d(LOG_TAG, "${logThreadName()} getWeather response content " + content)
                val jsonObject = JSONObject(content)
                return jsonObject.weather
            } finally {
                httpGet.disconnect()
            }
        } catch (e: IOException) {
            throw OwmClientException("Failed to execute the request", e)
        } catch (t: Throwable) {
            throw RuntimeException("Unexpected error", t)
        }
    }

    /**
     * Gets current weather by latitude and longitude in separate thread.
     * When weather received the listener will be called in worker thread.
     */
    public fun getWeatherAsync(lat: Double, lon: Double, default: Weather, listener: (w: Weather?, t: Throwable?) -> Unit) {
        GetWeatherAsync(lat, lon, default, listener).execute()
    }

    /**
     * Gets current weather by latitude and longitude in separate thread.
     * When weather received the listener will be called in worker thread.
     * If any error occurs the worker will sleep for retrySleepMs and retry to get weather.
     * If error occurs more than retryN times exception will be rethrown.
     */
    public fun getWeatherAsync(
            lat: Double,
            lon: Double,
            default: Weather,
            retryN: Int,
            retrySleepMs: Int,
            listener: (w: Weather?, t: Throwable?) -> Unit
    ) {
        GetWeatherAsync(lat, lon, default, retryN, retrySleepMs, listener).execute()
    }

    private fun URL.httpGet(): HttpURLConnection {
        val connection = openConnection() as HttpURLConnection
        connection.readTimeout = READ_TIMEOUT_MS
        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.requestMethod = "GET"
        connection.doInput = true
        return connection
    }

    private fun HttpURLConnection.content(): String {
        return inputStream.use { inputStream ->
            IOUtils.toString(inputStream, "UTF-8")
        }
    }

    private fun StringBuilder.appendParam(name: String, value: Any): StringBuilder {
        return append(name).append("=").append(value);
    }

    private fun StringBuilder.appendApiKey(): StringBuilder {
        return append("&").appendParam(API_KEY, apiKey);
    }

    private inner class GetWeatherAsync(
            val lat: Double,
            val lon: Double,
            val default: Weather,
            val retryN: Int,
            val retrySleepMs: Int,
            val listener: (w: Weather?, t: Throwable?) -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        constructor(lat: Double, lon: Double, default: Weather, listener: (w: Weather?, t: Throwable?) -> Unit) :
        this(lat, lon, default, 1, 0, listener)

        override fun doInBackground(vararg params: Unit?): Unit? {
            try {
                retry(
                        retryN,
                        retrySleepMs,
                        fun(tryN: Int) {
                            Log.d(LOG_TAG, "${logThreadName()} Get Weather in background, try #$tryN.")
                            listener(getWeather(lat, lon, default), null)
                        }
                )
            } catch (e: Exception) {
                listener(null, e)
            }
            return null
        }
    }
}

public class OwmClientException : RuntimeException {

    constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable)

    constructor (detailMessage: String?) : super(detailMessage)
}
