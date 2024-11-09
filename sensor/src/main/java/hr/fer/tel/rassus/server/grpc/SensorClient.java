package hr.fer.tel.rassus.server.grpc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.exceptions.CsvException;
import hr.fer.tel.rassus.server.Geolocation;
import hr.fer.tel.rassus.server.Reading;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import okhttp3.*;
import sensor.SensorServiceGrpc;
import sensor.Sensor;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static hr.fer.tel.rassus.server.Sensor.registerSensor;

public class SensorClient {
    private final ManagedChannel channel;
    private final SensorServiceGrpc.SensorServiceBlockingStub blockingStub;
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Gson gson = new Gson();

    public SensorClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = SensorServiceGrpc.newBlockingStub(channel);
    }

    public Reading getReading(String sensorId) {
        Sensor.ReadingRequest request = Sensor.ReadingRequest.newBuilder().setSensorId(sensorId).build();

        try{
            Sensor.ReadingResponse response = blockingStub.getReading(request);
            return new Reading(
                    response.getTemperature(),
                    response.getHumidity(),
                    response.getPressure(),
                    response.getCO(),
                    response.getNO2(),
                    response.getSO2()
            );
        } catch(Exception e) {
            System.out.println("Nearest neighbor is not active! ");
            return null;
        }

    }

    public void shutdown() {
        channel.shutdown();
        try {
            if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                channel.shutdownNow();
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Channel did not terminate");
                }
            }
        } catch (InterruptedException e) {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws IOException, CsvException {
        double latitude = Geolocation.getRandomLatitude();
        double longitude = Geolocation.getRandomLongitude();
        String ip = "127.0.0.1";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the port number:");
        int port = scanner.nextInt();

        int sensorId = registerSensor(latitude, longitude, ip, port);
        System.out.println("Sensor registered with ID: " + sensorId);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            try {
                long brojAktivnihSekundi = System.currentTimeMillis() / 1000;

                Reading reading = Reading.generateReading(brojAktivnihSekundi);
                System.out.println("Own reading: " + reading);
                System.out.println("Sensor ID: " + sensorId);
                hr.fer.tel.rassus.server.Sensor nearestNeighbor = findNearestNeighbor(sensorId);
                if (nearestNeighbor != null) {
                    System.out.println("Nearest neighbor: " + nearestNeighbor);
                    System.out.println("Neighbor ID: " + nearestNeighbor.getId());
                    SensorClient client = new SensorClient(nearestNeighbor.getIp(), nearestNeighbor.getPort());
                    Reading neighborReading = client.getReading(Integer.toString(nearestNeighbor.getId()));
                    if(neighborReading == null) {
                        System.out.println("No nearest neighbor found. Storing own reading.");
                        sendCalibratedReading(sensorId, reading);
                    } else{
                        System.out.println("Neighbor reading:" + neighborReading);
                        Reading calibratedReading = reading.calibrate(neighborReading);
                        sendCalibratedReading(sensorId, calibratedReading);
                        System.out.println("Calibrated Reading: " + calibratedReading);
                    }

                    client.shutdown();
                    System.out.println();

                } else {
                    System.out.println("No nearest neighbor found. Storing own reading.");
                    sendCalibratedReading(sensorId, reading);
                    System.out.println();
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static hr.fer.tel.rassus.server.Sensor findNearestNeighbor(Integer sensorId) throws IOException {
        String url = "http://localhost:8090/api/sensors/" + sensorId + "/nearest-neighbor";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 204) {
                return null;
            }
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            hr.fer.tel.rassus.server.Sensor nearestNeighbor = gson.fromJson(responseBody, hr.fer.tel.rassus.server.Sensor.class);
            nearestNeighbor.setId(gson.fromJson(responseBody, JsonObject.class).get("id").getAsInt()); // Set the id attribute
            return nearestNeighbor;
        }
    }

    private static void sendCalibratedReading(int sensorId, Reading calibratedReading) throws IOException {
        String url = "http://localhost:8090/api/readings/" + sensorId;
        String json = gson.toJson(calibratedReading);

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
