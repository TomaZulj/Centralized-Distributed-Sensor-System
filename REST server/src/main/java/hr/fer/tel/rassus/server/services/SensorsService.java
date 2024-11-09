package hr.fer.tel.rassus.server.services;

import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SensorsService {

    @Autowired
    private SensorRepository sensorRepository;

    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public List<Sensor> findAll() {
        return sensorRepository.findAll();
    }

    public Sensor findById(Long id) {
        return sensorRepository.findById(id).orElse(null);
    }

    public Sensor findNearestNeighbor(Sensor sensor) {
        return findAll().stream()
                .filter(s -> !s.getId().equals(sensor.getId()))
                .min(Comparator.comparingDouble(s -> calculateDistance(sensor.getLatitude(), sensor.getLongitude(), s.getLatitude(), s.getLongitude())))
                .orElse(null);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radijus Zemlje u kilometrima
        double dlon = Math.toRadians(lon2 - lon1);
        double dlat = Math.toRadians(lat2 - lat1);
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
