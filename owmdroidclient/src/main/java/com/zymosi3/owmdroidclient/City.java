package com.zymosi3.owmdroidclient;

/**
 *
 */
public class City {

    public final String cityName;
    public final int cityId;

    public City(String cityName, int cityId) {
        this.cityName = cityName;
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return cityId == city.cityId &&
                !(cityName != null ? !cityName.equals(city.cityName) : city.cityName != null);
    }

    @Override
    public int hashCode() {
        int result = cityName != null ? cityName.hashCode() : 0;
        result = 31 * result + cityId;
        return result;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
