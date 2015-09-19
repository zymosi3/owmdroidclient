package com.zymosi3.owmdroidclient

/**
 *
 */
public class City(val name: String, val id: Int )

public class Weather(val city: City, val temp: Double)
