package com.zymosi3.owmdroidclient

import android.util.Log
import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL


public class City(public val name: String, public val id: Int )

public class Weather(public val city: City, public val temp: Double)

public class OwmClientException : RuntimeException {

    constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable)

    constructor (detailMessage: String?) : super(detailMessage)
}

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
     * Gets current weather by latitude and longitude
     * @throws OwmClientException if something went wrong and you should retry your request
     */
    public fun getWeather(lat: Double, lon: Double): Weather {
        Log.d(LOG_TAG, "getWeather, lat = $lat, lon = $lon")

        val urlBuilder = StringBuilder(WEATHER_URL).
                append("?").
                append(LAT).append("=").append(lat).
                append("&").
                append(LON).append("=").append(lon).
                append("&").
                append(API_KEY).append("=").append(apiKey)

        try {
            val url = URL(urlBuilder.toString())
            Log.d(LOG_TAG, "getWeather url = '$url'")
            val connection: HttpURLConnection
            try {
                connection = url.openConnection() as HttpURLConnection
            } catch (e: IOException) {
                throw OwmClientException("Failed to open url connection", e)
            }

            connection.readTimeout = READ_TIMEOUT_MS
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.requestMethod = "GET"
            connection.doInput = true

            try {
                connection.connect()
                val response = connection.responseCode
                if (response != 200)
                    throw OwmClientException("OWM response code " + response)

                val inputStream = connection.inputStream
                try {
                    val content = IOUtils.toString(inputStream, "UTF-8")
                    Log.d(LOG_TAG, "getWeather response content " + content)
                    val jsonObject = JSONObject(content)
                    val city = City(
                            jsonObject.getString("name"),
                            jsonObject.getInt("id"))
                    val temp = jsonObject.getJSONObject("main").getDouble("temp")
                    return Weather(city, temp)
                } finally {
                    inputStream.close()
                }
            } catch (e: IOException) {
                throw OwmClientException("Failed to execute the request", e)
            } finally {
                connection.disconnect()
            }
        } catch (e: MalformedURLException) {
            throw RuntimeException("Unexpected error", e)
        } catch (e: ProtocolException) {
            throw RuntimeException("Unexpected error", e)
        } catch (e: JSONException) {
            throw RuntimeException("Unexpected error", e)
        }

    }
}

