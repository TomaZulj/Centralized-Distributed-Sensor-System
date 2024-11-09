package hr.fer.tel.rassus.server.services;

import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.repositories.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {

    @Autowired
    private ReadingRepository readingRepository;


    public Reading save(Reading reading) {
        return readingRepository.save(reading);
    }

    public List<Reading> findAll() {
        return readingRepository.findAll();
    }

}
