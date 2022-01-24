package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyController {


    private static Properties properties = new Properties();

    private static boolean tutor;

    public static void loadProperties() {
        try (InputStream input = new FileInputStream("simulator.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTutor() {
        return properties.getProperty("role").equals("tutor");
    }

    public static String getTutorHost() {
        return properties.getProperty("tutorhost");
    }

    public static String getTutorPort() {
        return properties.getProperty("tutorport");
    }

}