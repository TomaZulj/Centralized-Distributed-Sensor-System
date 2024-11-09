package hr.fer.tel.rassus.server.controllers;


import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.SensorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/sensors")
public class SensorsController {

    private static final Logger logger = Logger.getLogger(SensorsController.class.getName());

    @Autowired
    private SensorsService sensorService;

    //  TODO 4.1  Registracija

    @PostMapping("/register")
    public ResponseEntity<Long> registerSensor(@RequestBody Sensor sensor) {
        try {
            Sensor savedSensor = sensorService.save(sensor);
            URI location = new URI("/api/sensors/" + savedSensor.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(location);
            return new ResponseEntity<>(savedSensor.getId(), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering sensor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //  TODO 4.4  Popis senzora
    @GetMapping("/getAll")
    public ResponseEntity<Collection<Sensor>> getAllSensors() {
        return ResponseEntity.ok(sensorService.findAll());
    }

    //  TODO 4.2  Najbliži susjed
    @GetMapping("/{id}/nearest-neighbor")
    public ResponseEntity<Sensor> getNearestNeighbor(@PathVariable Long id) {
        Sensor sensor = sensorService.findById(id);
        if (sensor == null) {
            return ResponseEntity.notFound().build();
        }
        Sensor nearestNeighbor = sensorService.findNearestNeighbor(sensor);
        if (nearestNeighbor == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(nearestNeighbor);
    }

}
