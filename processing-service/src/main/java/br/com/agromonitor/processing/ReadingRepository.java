package br.com.agromonitor.processing;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface ReadingRepository extends MongoRepository<Reading, String> {
    List<Reading> findTop500ByOrderByTimestampDesc();
}
