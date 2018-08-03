package edu.gla.kail.ad;

import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.SimulatorConfiguration.SimulatorConfig;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public final class PropertiesSingleton {
    private static PropertiesSingleton _instance;
    private static SimulatorConfig _simulatorConfig;

    public static synchronized SimulatorConfig getSimulatorConfig() {
        return _simulatorConfig;
    }

    public static synchronized void reloadProperties(URL url) throws IOException {
        _instance = null;
        getPropertiesSingleton(url);
    }

    public static synchronized PropertiesSingleton getPropertiesSingleton(URL url) throws
            IOException {
        if (_instance == null) {
            _instance = new PropertiesSingleton();
            setProperties(url);
        }
        return _instance;
    }

    private static void setProperties(URL url) throws IOException {
        SimulatorConfig.Builder coreConfigBuilder = SimulatorConfig.newBuilder();
        String jsonText = readPropertiesFromUrl(url);
        JsonFormat.parser().merge(jsonText, coreConfigBuilder);
        _simulatorConfig = coreConfigBuilder.build();
    }

    private static String readPropertiesFromUrl(URL url) throws IOException {
        return new Scanner(url.openStream()).useDelimiter("\\Z").next();

    }
}
