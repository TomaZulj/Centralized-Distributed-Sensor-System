package hr.fer.tel.rassus.server;

import java.util.Random;

public class Geolocation {
    private static final double MIN_LONGITUDE = 15.87;
    private static final double MAX_LONGITUDE = 16.0;
    private static final double MIN_LATITUDE = 45.75;
    private static final double MAX_LATITUDE = 45.85;
    private static final Random RANDOM = new Random();

    public static double getRandomLongitude() {
        return MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * RANDOM.nextDouble();
    }

    public static double getRandomLatitude() {
        return MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * RANDOM.nextDouble();
    }
}
