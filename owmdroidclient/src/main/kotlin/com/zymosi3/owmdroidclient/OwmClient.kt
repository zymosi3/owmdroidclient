package com.zymosi3.owmdroidclient

import android.util.Log
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


public class City(public val name: String, public val id: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other == null || javaClass  != javaClass ) return false;

        val city = other as City;

        return id == city.id && name == city.name
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id;
        return result;
    }

    override fun toString(): String {
        return "City{name='$name' , id=$id}"
    }
}

public class Weather(
        public val city: City,
        public val temp: Double,
        public val time: Long
) {

    override fun toString(): String {
        return "Weather{city='$city' , temp=$temp, time=$time}"
    }
}

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
     * @throws RuntimeException if caught unexpected error
     */
    public fun getWeather(lat: Double, lon: Double): Weather {
        Log.d(LOG_TAG, "getWeather, lat = $lat, lon = $lon")

        val urlBuilder = StringBuilder(WEATHER_URL).
                append("?").appendParam(LAT, lat).append("&").appendParam(LON, lon).appendApiKey()

        try {
            val url = URL(urlBuilder.toString())
            Log.d(LOG_TAG, "getWeather url = '$url'")
            val httpGet = url.httpGet()
            httpGet.connect()
            try {
                val response = httpGet.responseCode
                if (response != HttpURLConnection.HTTP_OK)
                    throw OwmClientException("OWM response code " + response)

                val content = httpGet.content()
                Log.d(LOG_TAG, "getWeather response content " + content)
                val jsonObject = JSONObject(content)
                return jsonObject.weather()
            } finally {
                httpGet.disconnect()
            }
        } catch (e: IOException) {
            throw OwmClientException("Failed to execute the request", e)
        } catch (t: Throwable) {
            throw RuntimeException("Unexpected error", t)
        }
    }

    private fun JSONObject.city(): City {
        return City(getString("name"), getInt("id"))
    }

    private fun JSONObject.temp(): Double {
        return getJSONObject("main").getDouble("temp")
    }

    private fun JSONObject.time(): Long {
        return getLong("dt")
    }

    private fun JSONObject.weather(): Weather {
        return Weather(city(), temp(), time())
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
}
