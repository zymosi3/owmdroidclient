package com.zymosi3.owmdroidclient;

import android.app.Application;
import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGetWeather() throws Exception {
        OwmClient owmClient = new OwmClient("set API key here");
        Weather weather = owmClient.getWeather(54.9450754, 73.3782123);
        assertEquals("Omsk", weather.city.name);
        assertEquals(1496153, weather.city.id);
        assertTrue(weather.temp > 0);
    }
}