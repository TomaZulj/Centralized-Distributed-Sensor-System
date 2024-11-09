package hr.fer.tel.rassus.server.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

@Entity
public class Reading {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  private Long sensorId;

  private double temperature;
  private double pressure;
  private double humidity;
  private double co;
  private double no2;
  private double so2;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSensorId() {
    return sensorId;
  }

  public void setSensorId(Long sensorId) {
    this.sensorId = sensorId;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getPressure() {
    return pressure;
  }

  public void setPressure(double pressure) {
    this.pressure = pressure;
  }

  public double getHumidity() {
    return humidity;
  }

  public void setHumidity(double humidity) {
    this.humidity = humidity;
  }

  public double getCo() {
    return co;
  }

  public void setCo(double co) {
    this.co = co;
  }

  public double getNo2() {
    return no2;
  }

  public void setNo2(double no2) {
    this.no2 = no2;
  }

  public double getSo2() {
    return so2;
  }

  public void setSo2(double so2) {
    this.so2 = so2;
  }
}

