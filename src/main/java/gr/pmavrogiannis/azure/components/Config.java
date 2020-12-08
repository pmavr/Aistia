package gr.pmavrogiannis.azure.components;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String configFilePath = "src/main/resources/config.properties";

    public static String get(String property){
        Properties properties = new Properties();
        try {
            InputStream stream = new FileInputStream(configFilePath);
            properties.load(stream);
        }catch (IOException e){
            e.printStackTrace();
        }
        String propertyValue = properties.getProperty(property);

        return propertyValue.isEmpty() ? "" : propertyValue;
    }
}
