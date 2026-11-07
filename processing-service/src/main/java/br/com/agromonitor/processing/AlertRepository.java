package br.com.agromonitor.processing;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findTop500ByOrderByTimestampDesc();
}
