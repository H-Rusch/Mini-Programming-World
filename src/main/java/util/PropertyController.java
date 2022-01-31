package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class PropertyController {

    private static final Properties properties = new Properties();

    private PropertyController() {
    }

    public static void loadProperties() {
        try (InputStream input = new FileInputStream("simulator.properties")) {
            properties.load(input);

            if (getLanguage().equals("de")) {
                I18nUtil.setLocale(Locale.GERMANY);
            } else if (getLanguage().equals("en")) {
                I18nUtil.setLocale(Locale.US);
            } else {
                I18nUtil.setLocale(Locale.GERMANY);
                new Alert(Alert.AlertType.WARNING, "Angegebene Sprache nicht gefunden. Deutsch als standard festgelegt.",
                        ButtonType.OK).show();
            }
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

    public static String getLanguage() {
        return properties.getProperty("language");
    }
}