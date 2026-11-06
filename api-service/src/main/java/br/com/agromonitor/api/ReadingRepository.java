package br.com.agromonitor.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface ReadingRepository extends MongoRepository<Reading, String> {
    List<Reading> findTop500ByOrderByTimestampDesc();
    List<Reading> findTop1BySensorIdOrderByTimestampDesc(String sensorId);
}
