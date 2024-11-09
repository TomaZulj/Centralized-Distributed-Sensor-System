package hr.fer.tel.rassus.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.ReadingService;
import hr.fer.tel.rassus.server.services.SensorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {
    @Autowired
    private SensorsService sensorsService;

    @Autowired
    private ReadingService readingService;

  // TODO 4.3  Spremanje očitanja pojedinog senzora
  @PostMapping("/{sensorId}")
  public ResponseEntity<Void> saveReading(@PathVariable Long sensorId, @RequestBody Reading reading) {
      Sensor sensor = sensorsService.findById(sensorId);
      if (sensor == null) {
          return ResponseEntity.noContent().build();
      }
      reading.setSensorId(sensorId);
      Reading savedReading = readingService.save(reading);
      URI location = URI.create(String.format("/api/readings/%d", savedReading.getId()));
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(location);
      return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  // TODO 4.5  Popis očitanja pojedinog senzora

    @GetMapping("/{sensorId}")
    public ResponseEntity<List<Reading>> getReadingsBySensorId(@PathVariable Long sensorId) {
        Sensor sensor = sensorsService.findById(sensorId);
        if (sensor == null) {
            return ResponseEntity.noContent().build();
        }
        List<Reading> readings = readingService.findAll().stream()
                .filter(reading -> reading.getSensorId().equals(sensorId))
                .collect(Collectors.toList());
        return new ResponseEntity<>(readings, HttpStatus.OK);
    }
}