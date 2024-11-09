package hr.fer.tel.rassus.server;

import com.google.gson.annotations.SerializedName;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Reading {

    private double temperature;
    private double humidity;
    private double pressure;

    @SerializedName("co")
    private double CO;

    @SerializedName("no2")
    private double NO2;

    @SerializedName("so2")
    private double SO2;

    public Reading(double temperature, double humidity, double pressure, double CO, double NO2, double SO2) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.CO = CO;
        this.NO2 = NO2;
        this.SO2 = SO2;
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

    public double getCO() {
        return CO;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public double getNO2() {
        return NO2;
    }

    public void setNO2(double NO2) {
        this.NO2 = NO2;
    }

    public double getSO2() {
        return SO2;
    }

    public void setSO2(double SO2) {
        this.SO2 = SO2;
    }

    public static Reading generateReading(long brojAktivnihSekundi) throws IOException, CsvException {
        String csvFilePath = "src/main/resources/readings.csv";
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> allRows = reader.readAll();
            int rowIndex = (int) ((brojAktivnihSekundi % 100) + 1);
            if (rowIndex >= allRows.size()) {
                throw new IndexOutOfBoundsException("Calculated row index is out of bounds.");
            }
            String[] row = allRows.get(rowIndex);
            //System.out.println("Reading from row: " + rowIndex + 1);
            return new Reading(
                    Double.parseDouble(row[0]),
                    Double.parseDouble(row[1]),
                    Double.parseDouble(row[2]),
                    parseGasValue(row[3]),
                    parseGasValue(row[4]),
                    parseGasValue(row[5])
            );
        }
    }

    private static Double parseGasValue(String value) {
        if (value == null || value.isEmpty() || value.equals("0")) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    public Reading calibrate(Reading neighborReading) {
        return new Reading(
                (this.temperature + neighborReading.temperature) / 2,
                (this.humidity + neighborReading.humidity) / 2,
                (this.pressure + neighborReading.pressure) / 2,
                averageGasValue(this.CO, neighborReading.CO),
                averageGasValue(this.NO2, neighborReading.NO2),
                averageGasValue(this.SO2, neighborReading.SO2)
        );
    }

    private Double averageGasValue(Double value1, Double value2) {
        if (value1 == null && value2 == null) {
            return null;
        }
        if (value1 == null || value1 == 0) {
            return value2;
        }
        if (value2 == null || value2 == 0) {
            return value1;
        }
        return (value1 + value2) / 2;
    }

    @Override
    public String toString() {
        return "Reading{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", CO=" + CO +
                ", NO2=" + NO2 +
                ", SO2=" + SO2 +
                '}';
    }
}
