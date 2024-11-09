package hr.fer.tel.rassus.server;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class Sensor {
    private int id;
    private double latitude;
    private double longitude;
    private String ip;
    private int port;

    private static final String REGISTER_URL = "http://localhost:8090/api/sensors/register";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();

    public Sensor(double latitude, double longitude, String ip, int port) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public static int registerSensor(double latitude, double longitude, String ip, int port) throws IOException {
        Sensor sensor = new Sensor(latitude, longitude, ip, port);
        String json = GSON.toJson(sensor);

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(body)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return GSON.fromJson(responseBody, Integer.class);
        }
    }


    @Override
    public String toString() {
        return "Sensor{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
