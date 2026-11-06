package br.com.agromonitor.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findTop500ByOrderByTimestampDesc();
}
