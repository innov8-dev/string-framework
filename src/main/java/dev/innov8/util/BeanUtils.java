package dev.innov8.util;

public class BeanUtils {

    public static boolean isBeanNameValid(String beanName) {
        return (beanName != null && !beanName.trim().equals(""));
    }

    public static boolean isBeanClassNameValid(String className) {

        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;

    }

    public static boolean isBeanScopeValid(String beanScope) {
        if (beanScope == null || beanScope.trim().equals("")) return false;
        return !beanScope.toLowerCase().equals("singleton") && !beanScope.toLowerCase().equals("prototype");
    }

}
