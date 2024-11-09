package hr.fer.tel.rassus.server.grpc;

import com.opencsv.exceptions.CsvException;
import hr.fer.tel.rassus.server.Reading;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import sensor.SensorServiceGrpc;
import sensor.Sensor;

import java.io.IOException;
import java.util.Scanner;

public class SensorServer {
    private Server server;
    private static long startTime;

    public void start(int port) throws IOException {
        startTime = System.currentTimeMillis() / 1000;
        server = ServerBuilder.forPort(port)
                .addService(new SensorServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            SensorServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private static class SensorServiceImpl extends SensorServiceGrpc.SensorServiceImplBase {
        @Override
        public void getReading(Sensor.ReadingRequest request, StreamObserver<Sensor.ReadingResponse> responseObserver) {
            Reading reading;
            try {
                long brojAktivnihSekundi = (System.currentTimeMillis() / 1000) - startTime;
                reading = Reading.generateReading(brojAktivnihSekundi);
                System.out.println("Generated reading: " + reading);
            } catch (IOException | CsvException e) {
                throw new RuntimeException(e);
            }

            Sensor.ReadingResponse response = Sensor.ReadingResponse.newBuilder()
                    .setTemperature(reading.getTemperature())
                    .setHumidity(reading.getHumidity())
                    .setPressure(reading.getPressure())
                    .setCO(reading.getCO())
                    .setNO2(reading.getNO2())
                    .setSO2(reading.getSO2())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter port number: ");
        int port = scanner.nextInt();

        final SensorServer server = new SensorServer();
        server.start(port);
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
