package com.zymosi3.owmdroidclient

import org.json.JSONObject

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

    override fun toString(): String {
        return "Weather{id=$id, time=$time, cityId=$cityId, cityName=$cityName, " +
                "temperature=$temperature, pressure=$pressure, humidity=$humidity, " +
                "visibility=$visibility, windSpeed=$windSpeed, windDegree=$windDegree, " +
                "clouds=$clouds, description=$description, icon=$icon}"
    }
}

public val JSONObject.id: Int
    get() = getJSONArray("weather").get(0) as JSONObject getInt("id")

public val JSONObject.time: Long
    get() = getLong("dt")

public val JSONObject.cityId: Int
    get() = getInt("id")

public val JSONObject.cityName: String
    get() = getString("name")

public  val JSONObject.temperature: Double
    get() = getJSONObject("main").getDouble("temp")

public val JSONObject.pressure: Int
    get() = getJSONObject("main").getInt("pressure")

public val JSONObject.humidity: Int
    get() = getJSONObject("main").getInt("humidity")

public val JSONObject.visibility: Int
    get() = getInt("visibility")

public val JSONObject.windSpeed: Int
    get() = getJSONObject("wind").getInt("speed")

public val JSONObject.windDegree: Int
    get() = getJSONObject("wind").getInt("deg")

public val JSONObject.clouds: Int
    get() = getJSONObject("clouds").getInt("all")

public val JSONObject.description: String
    get() = getJSONArray("weather").get(0) as JSONObject getString("description")

public val JSONObject.icon: String
    get() = getJSONArray("weather").get(0) as JSONObject getString("icon")

public val JSONObject.weather: Weather
    get() = Weather(id, time, cityId, cityName, temperature, pressure, humidity, visibility,
            windSpeed, windDegree, clouds, description, icon)