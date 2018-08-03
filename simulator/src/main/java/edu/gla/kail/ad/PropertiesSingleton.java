package edu.gla.kail.ad;

import com.google.protobuf.util.JsonFormat;
import edu.gla.kail.ad.CoreConfiguration.CoreConfig;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public final class PropertiesSingleton {
    private static PropertiesSingleton _instance;
    private static CoreConfig _coreConfig;

    public static synchronized CoreConfig getCoreConfig() {
        return _coreConfig;
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
        CoreConfig.Builder coreConfigBuilder = CoreConfig.newBuilder();
        String jsonText = readPropertiesFromUrl(url);
        JsonFormat.parser().merge(jsonText, coreConfigBuilder);
        _coreConfig = coreConfigBuilder.build();
    }

    private static String readPropertiesFromUrl(URL url) throws IOException {
        return new Scanner(url.openStream()).useDelimiter("\\Z").next();

    }
}
