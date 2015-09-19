package com.zymosi3.owmdroidclient;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Performs requests to Open Weather Map API
 */
public class OwmClient {

    private static final String LOG_TAG = "OwmClient";

    private static final String API_URL = "http://api.openweathermap.org/data";
    private static final String API_VERSION = "2.5";
    private static final String WEATHER_URL = API_URL + "/" + API_VERSION + "/" + "weather";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String API_KEY = "appid";

    private static final int READ_TIMEOUT_MS = 10000;
    private static final int CONNECT_TIMEOUT_MS = 15000;

    private final String apiKey;

    public OwmClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets current weather by latitude and longitude
     * @throws OwmClientException if something went wrong and you should retry your request
     */
    @SuppressWarnings("unchecked")
    public Weather getWeather(double lat, double lon) throws OwmClientException {
        Log.d(LOG_TAG, "getWeather, lat = " + lat + ", lon = " + lon);

        StringBuilder urlBuilder = new StringBuilder(WEATHER_URL).
                append("?").
                append(LAT).append("=").append(lat).
                append("&").
                append(LON).append("=").append(lon).
                append("&").
                append(API_KEY).append("=").append(apiKey);

        try {
            URL url = new URL(urlBuilder.toString());
            Log.d(LOG_TAG, "getWeather url = '" + url + "'");
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new OwmClientException("Failed to open url connection", e);
            }
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            try {
                connection.connect();
                int response = connection.getResponseCode();
                if (response != 200)
                    throw new OwmClientException("OWM response code " + response);

                try (InputStream inputStream = connection.getInputStream()) {
                    String content = IOUtils.toString(inputStream, "UTF-8");
                    Log.d(LOG_TAG, "getWeather response content " + content);
                    JSONObject jsonObject = new JSONObject(content);
                    City city = new City(
                            jsonObject.getString("name"),
                            jsonObject.getInt("id")
                    );
                    double temp = jsonObject.getJSONObject("main").getDouble("temp");
                    return new Weather(city, temp);
                }
            } catch (IOException e) {
                throw new OwmClientException("Failed to execute the request", e);
            } finally {
                connection.disconnect();
            }
        } catch (MalformedURLException | ProtocolException | JSONException e) {
            throw new RuntimeException("Unexpected error", e);
        }
    }
}
