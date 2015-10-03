package com.zymosi3.owmdroidclient;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.concurrent.atomic.AtomicReference;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final double LAT = 54.9450754;
    private static final double LON = 73.3782123;

    public ApplicationTest() {
        super(Application.class);
    }

    public void testGetWeather() throws Exception {
        OwmClient owmClient = new OwmClient("set API key here");
        Weather weather = owmClient.getWeather(LAT, LON);
        assertWeather(weather);
    }

    public void testGetWeatherAsync() throws Exception {
        OwmClient owmClient = new OwmClient("set API key here");
        final AtomicReference<Weather> weatherRef = new AtomicReference<>();
        owmClient.getWeatherAsync(LAT, LON, new Function2<Weather, Throwable, Unit>() {
            @Override
            public Unit invoke(Weather weather, Throwable t) {
                weatherRef.set(weather);
                return null;
            }
        });

        int timeWaitMs = 1000;
        long startTime = System.currentTimeMillis();
        while (weatherRef.get() == null) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - startTime > timeWaitMs) {
                throw new RuntimeException("Result wait time exceeded");
            }
            Thread.sleep(10);
        }

        assertWeather(weatherRef.get());
    }

    private void assertWeather(Weather weather) {
        assertEquals("Omsk", weather.getCity().getName());
        assertEquals(1496153, weather.getCity().getId());
        assertTrue(weather.getTemp() > 0);
        assertTrue(weather.getTime() > 0);
    }
}