package com.zymosi3.owmdroidclient;

/**
 *
 */
public class Weather {

    public final City city;
    public final double temp;

    public Weather(City city, double temp) {
        this.city = city;
        this.temp = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        return Double.compare(weather.temp, temp) == 0 &&
                !(city != null ? !city.equals(weather.city) : weather.city != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp1;
        result = city != null ? city.hashCode() : 0;
        temp1 = Double.doubleToLongBits(temp);
        result = 31 * result + (int) (temp1 ^ (temp1 >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city=" + city +
                ", temp=" + temp +
                '}';
    }
}
