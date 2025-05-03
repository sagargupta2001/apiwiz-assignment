package org.apiwiz.jmeter;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;

@ApplicationScoped
public class JMeterService {

    @ConfigProperty(name = "jmeter.home")
    String jmeterHome;

    public void runTestPlan(String jmxFilePath, String csvReportPath) throws Exception {
        configureJMeter();

        HashTree testPlanTree = loadTestPlan(jmxFilePath);
        attachSummaryResultCollector(testPlanTree, csvReportPath);

        runTest(testPlanTree);

        System.out.println("JMeter test completed.");
    }

    private void configureJMeter() {
        File homeDir = new File(jmeterHome);
        if (!homeDir.exists() || !homeDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid JMeter home: " + jmeterHome);
        }

        String jmeterProperties = new File(homeDir, "bin/jmeter.properties").getAbsolutePath();

        JMeterUtils.setJMeterHome(homeDir.getAbsolutePath());
        JMeterUtils.loadJMeterProperties(jmeterProperties);
        JMeterUtils.setProperty("log_file", new File(homeDir, "bin/jmeter.log").getAbsolutePath());
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();
    }

    private HashTree loadTestPlan(String jmxFilePath) throws Exception {
        File jmxFile = new File(jmxFilePath);
        if (!jmxFile.exists()) {
            throw new IllegalArgumentException("JMX file not found: " + jmxFilePath);
        }

        return SaveService.loadTree(jmxFile);
    }

    private void attachSummaryResultCollector(HashTree testPlanTree, String csvOutputPath) {
        Summariser summariser = new Summariser("summary");

        // Create a configuration to disable saving of each sample (we only want summary)
        SampleSaveConfiguration saveConfig = new SampleSaveConfiguration();
        saveConfig.setTime(true);
        saveConfig.setLatency(true);
        saveConfig.setTimestamp(true);
        saveConfig.setSuccess(true); // <-- You need this
        saveConfig.setLabel(true);
        saveConfig.setCode(true);
        saveConfig.setMessage(true);
        saveConfig.setThreadName(true);
        saveConfig.setDataType(true);
        saveConfig.setBytes(true);
        saveConfig.setSentBytes(true);
        saveConfig.setUrl(true);
        saveConfig.setIdleTime(true);
        saveConfig.setConnectTime(true); // Optional, useful for network latency

        // Optionally keep these false:
        saveConfig.setAssertions(false);
        saveConfig.setResponseData(false);
        saveConfig.setSamplerData(false);
        saveConfig.setResponseHeaders(false);
        saveConfig.setRequestHeaders(false);
        saveConfig.setSubresults(false);
        saveConfig.setEncoding(false);
        saveConfig.setFileName(false);
        saveConfig.setHostname(false);
        saveConfig.setThreadCounts(false);
        saveConfig.setSampleCount(false);

        ResultCollector collector = new ResultCollector(summariser);
        collector.setSaveConfig(saveConfig);
        collector.setFilename(csvOutputPath);

        testPlanTree.add(testPlanTree.getArray()[0], collector);

        System.out.println("JMeter summariser initialized. Summary will be printed to console.");
    }

    private void runTest(HashTree testPlanTree) {
        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.configure(testPlanTree);
        engine.run();
    }
}
