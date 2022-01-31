package util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

// https://stackoverflow.com/questions/25793841/javafx-bindings-and-localization
public class I18nUtil {

    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.GERMANY);

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
    }

    public static String i18n(String key) {
        return ResourceBundle.getBundle("internationalization/simulator", getLocale()).getString(key);
    }

}
