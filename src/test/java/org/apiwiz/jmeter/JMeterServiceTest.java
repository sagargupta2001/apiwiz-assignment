package org.apiwiz.jmeter;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

@QuarkusTest
public class JMeterServiceTest {

    @Inject
    JMeterService jMeterService;

    @Test
    public void testRunAsyncJMeterPlan() throws Exception {
        String jmxPath = Paths.get("src", "main", "resources", "jmeter", "test-plan-async.jmx").toAbsolutePath().toString();
        String csvOutput = Paths.get("testresults", "jmeter-async-results.csv").toAbsolutePath().toString();
        jMeterService.runTestPlan(jmxPath, csvOutput);
        assertNoErrorsInCsv(csvOutput);
    }
    @Test
    public void testRunSyncJMeterPlan() throws Exception {
        String jmxPath = Paths.get("src", "main", "resources", "jmeter", "test-plan-sync.jmx").toAbsolutePath().toString();
        String csvOutput = Paths.get("testresults", "jmeter-sync-results.csv").toAbsolutePath().toString();

        jMeterService.runTestPlan(jmxPath, csvOutput);

        assertNoErrorsInCsv(csvOutput);
    }


    private void assertNoErrorsInCsv(String csvPath) throws IOException {
        int totalRequests = 0;
        int failedRequests = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header
                }

                String[] cols = line.split(",");

                // Skip lines that don't have at least 8 columns (success is at index 7)
                if (cols.length <= 7) {
                    System.out.println("Skipping malformed row: " + line);
                    continue;
                }

                boolean success = Boolean.parseBoolean(cols[7]);

                totalRequests++;
                if (!success) {
                    failedRequests++;
                }
            }
        }

        System.out.printf("Total: %d | Failed: %d | Error Rate: %.2f%%%n", totalRequests, failedRequests,
                totalRequests > 0 ? 100.0 * failedRequests / totalRequests : 0.0);

        if (failedRequests > 0) {
            throw new AssertionError("Test failed: error rate is " + failedRequests + " out of " + totalRequests);
        }

        System.out.println("Test passed: No errors found in CSV results.");
    }


}
