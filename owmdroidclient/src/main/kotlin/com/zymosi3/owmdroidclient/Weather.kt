package com.zymosi3.owmdroidclient

import android.util.Log
import org.json.JSONObject
import java.text.DateFormat

/**
 * Weather plain object and helper methods to parse json
 */
public class Weather(
        public val id: Int,
        public val time: Long,
        public val cityId: Int,
        public val cityName: String,
        public val temperature: Double,
        public val pressure: Int,
        public val humidity: Int,
        public val visibility: Int,
        public val windSpeed: Int,
        public val windDegree: Int,
        public val clouds: Int,
        public val description: String,
        public val icon: String
) {

    public val iconRes: Int
        get() = when(icon) {
            "01d" -> R.drawable.i01d
            "01n" -> R.drawable.i01n
            "02d" -> R.drawable.i02d
            "02n" -> R.drawable.i02n
            "03d" -> R.drawable.i03d
            "03n" -> R.drawable.i03n
            "04d" -> R.drawable.i04d
            "04n" -> R.drawable.i04n
            "09d" -> R.drawable.i09d
            "09n" -> R.drawable.i09n
            "10d" -> R.drawable.i10d
            "10n" -> R.drawable.i10n
            "11d" -> R.drawable.i11d
            "11n" -> R.drawable.i11n
            "13d" -> R.drawable.i13d
            "13n" -> R.drawable.i13n
            "50d" -> R.drawable.i50d
            "50n" -> R.drawable.i50n
            else -> R.drawable.i01d
        }

    public val celsiusTemperature: Double
        get() = temperature - 273.15

    public val fahrenheitTemperature: Double
        get() = (temperature * (9.0 / 5.0)) - 459.67

    override fun toString(): String {
        return "Weather{id=$id, time=$time, cityId=$cityId, cityName=$cityName, " +
                "temperature=$temperature, pressure=$pressure, humidity=$humidity, " +
                "visibility=$visibility, windSpeed=$windSpeed, windDegree=$windDegree, " +
                "clouds=$clouds, description=$description, icon=$icon}"
    }

    public fun toString(timeFormat: DateFormat): String {
        return "Weather{id=$id, time=${timeFormat.format(time * 1000)}, cityId=$cityId, cityName=$cityName, " +
                "temperature=$temperature, pressure=$pressure, humidity=$humidity, " +
                "visibility=$visibility, windSpeed=$windSpeed, windDegree=$windDegree, " +
                "clouds=$clouds, description=$description, icon=$icon}"
    }
}

public var defaultWeather: Weather? = null

private fun <R> withDefault(block: () -> R, default: R): R {
    try {
        return block()
    } catch (e: Exception) {
        Log.w("withDefault fun", "Exception caught, using default value " + default, e)
        return default
    }
}

public val JSONObject.id: Int
    get() = withDefault(
            {(getJSONArray("weather").get(0) as JSONObject).getInt("id")},
            defaultWeather?.id as Int
    )

public val JSONObject.time: Long
    get() = withDefault(
            {getLong("dt")},
            defaultWeather?.time as Long
    )

public val JSONObject.cityId: Int
    get() = withDefault(
            {getInt("id")},
            defaultWeather?.cityId as Int
    )

public val JSONObject.cityName: String
    get() = withDefault(
            {getString("name")},
            defaultWeather?.cityName as String
    )

public  val JSONObject.temperature: Double
    get() = withDefault(
            {getJSONObject("main").getDouble("temp")},
            defaultWeather?.temperature as Double
    )

public val JSONObject.pressure: Int
    get() = withDefault(
            {getJSONObject("main").getInt("pressure")},
            defaultWeather?.pressure as Int
    )

public val JSONObject.humidity: Int
    get() = withDefault(
            {getJSONObject("main").getInt("humidity")},
            defaultWeather?.humidity as Int
    )

public val JSONObject.visibility: Int
    get() = withDefault(
            {getInt("visibility")},
            defaultWeather?.visibility as Int
    )

public val JSONObject.windSpeed: Int
    get() = withDefault(
            {getJSONObject("wind").getInt("speed")},
            defaultWeather?.windSpeed as Int
    )

public val JSONObject.windDegree: Int
    get() = withDefault(
            {getJSONObject("wind").getInt("deg")},
            defaultWeather?.windDegree as Int
    )

public val JSONObject.clouds: Int
    get() = withDefault(
            {getJSONObject("clouds").getInt("all")},
            defaultWeather?.clouds as Int
    )

public val JSONObject.description: String
    get() = withDefault(
            {(getJSONArray("weather").get(0) as JSONObject).getString("description")},
            defaultWeather?.description as String
    )

public val JSONObject.icon: String
    get() = withDefault(
            {(getJSONArray("weather").get(0) as JSONObject).getString("icon")},
            defaultWeather?.icon as String
    )

public val JSONObject.weather: Weather
    get() = Weather(id, time, cityId, cityName, temperature, pressure, humidity, visibility,
            windSpeed, windDegree, clouds, description, icon)