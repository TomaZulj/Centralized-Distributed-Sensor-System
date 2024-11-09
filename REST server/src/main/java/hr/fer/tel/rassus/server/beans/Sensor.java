// src/main/java/hr/fer/tel/rassus/server/beans/Sensor.java
package hr.fer.tel.rassus.server.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Random;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;
    private String ip;
    private int port;

    public Sensor() {
        Random random = new Random();
        this.latitude = 45.75 + (45.85 - 45.75) * random.nextDouble();
        this.longitude = 15.87 + (16.00 - 15.87) * random.nextDouble();
        this.ip = "127.0.0.1";
        this.port = 8080;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
