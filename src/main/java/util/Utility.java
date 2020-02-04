package util;

import java.util.ResourceBundle;

public class Utility {
    public static String getConfig(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        return bundle.getString(key);
    }
}
