package dev.innov8.util;

public class StringUtils {

    public static String mapPropertyNameToAccessorName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

}
