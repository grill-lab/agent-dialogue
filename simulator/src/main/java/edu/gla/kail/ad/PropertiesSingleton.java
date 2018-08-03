package edu.gla.kail.ad;

import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.SimulatorConfiguration.SimulatorConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public final class PropertiesSingleton {
    private static PropertiesSingleton _instance;
    private static SimulatorConfig _simulatorConfig;

    public static synchronized SimulatorConfig getSimulatorConfig() throws IOException {
        if (_instance == null) {
            getPropertiesSingleton(null);
        }
        return _simulatorConfig;
    }

    public static synchronized void reloadProperties(URL url) throws IOException {
        _instance = null;
        getPropertiesSingleton(url);
    }

    public static synchronized PropertiesSingleton getPropertiesSingleton(@Nullable URL url) throws
            IOException {
        if (_instance == null) {
            _instance = new PropertiesSingleton();
            if (url == null) {
                // Nasty but works...
                setProperties(new URL("file://" + new File("s/").getAbsolutePath() +
                        "rc/main/resources/config.json"));
            } else {
                setProperties(url);
            }
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
