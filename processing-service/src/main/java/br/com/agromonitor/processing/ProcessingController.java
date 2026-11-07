package br.com.agromonitor.processing;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/internal")
public class ProcessingController {
    private final ReadingRepository readings;
    private final AlertRepository alerts;
    public ProcessingController(ReadingRepository r, AlertRepository a) {
        readings = r;
        alerts = a;
    }
    @PostMapping("/readings")
    public ResponseEntity<?> ingest(
        @RequestBody
    Reading reading) {
        if (reading.getSensorId() == null || reading.getType() == null || reading.getTimestamp() == null || !Double.isFinite(reading.getValue())) return ResponseEntity.badRequest().body(Map.of("error", "Leitura inválida"));
        if (reading.getFarmId() == null) reading.setFarmId("fazenda-demo");
        Reading saved = readings.save(reading);
        if ("CRITICAL".equals(reading.getEdgeStatus())) alerts.save(new Alert(reading));
        return ResponseEntity.accepted().body(Map.of("id", saved.getId(), "status", "stored"));
    }
    @PostMapping("/benchmark")
    public Map<String, Object> benchmark(
        @RequestParam(defaultValue="100000")
    int items,
        @RequestParam(defaultValue="4")
    int threads) throws Exception {
        int n = Math.max(1000, Math.min(items, 2_000_000));
        int t = Math.max(1, Math.min(threads, 32));
        long start = System.nanoTime();
        long seq = 0;
        for (int i = 1; i <= n; i++) seq += work(i);
        long seqNs = System.nanoTime() - start;
        ExecutorService pool = Executors.newFixedThreadPool(t);
        List<Future<Long>> futures = new ArrayList<>();
        int chunk = (n + t - 1) / t;
        start = System.nanoTime();
        for (int k = 0; k < t; k++) {
            final int from = k * chunk + 1, to = Math.min(n, (k + 1) * chunk);
            if (from <= to) futures.add(pool.submit(() -> {
                long sum = 0;
                for (int i = from; i <= to; i++) sum += work(i);
                return sum;
            }
            ));
        }
        long par = 0;
        for (Future<Long> f : futures) par += f.get();
        long parNs = System.nanoTime() - start;
        pool.shutdown();
        return Map.of("items", n, "threads", t, "sequentialMs", seqNs / 1_000_000.0, "parallelMs", parNs / 1_000_000.0, "speedup", parNs == 0 ? 0 : (double) seqNs / parNs, "checksumEqual", seq == par, "note", "Benchmark CPU sintético; repita e use média/mediana.");
    }
    private long work(int x) {
        long v = 0;
        for (int j = 1; j <= 120; j++) v += (long)(x * j) % 997;
        return v;
    }
}
