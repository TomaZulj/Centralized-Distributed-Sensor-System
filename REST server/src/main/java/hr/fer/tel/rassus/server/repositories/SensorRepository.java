package hr.fer.tel.rassus.server.repositories;
import hr.fer.tel.rassus.server.beans.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor save(Sensor sensor);
    List<Sensor> findAll();
}
