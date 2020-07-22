package dev.innov8.config;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanDefinition {

    private String beanName;
    private String beanClassName;
    private String beanScope;
    private List<String> constructorDependencies;
    private Map<String, String> setterDependencies;
    private Map<String, String> propertyValues;

    public BeanDefinition(String beanName, String beanClassName, String beanScope) {
        this.beanName = beanName;
        this.beanClassName = beanClassName;
        this.beanScope = beanScope;
        this.constructorDependencies = new ArrayList<>();
        this.setterDependencies = new HashMap<>();
        this.propertyValues = new HashMap<>();
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public Class<?> getBeanClass() throws ClassNotFoundException {
        return Class.forName(beanClassName);
    }

    public String getBeanScope() {
        return beanScope;
    }

    public BeanDefinition setBeanScope(String beanScope) {
        this.beanScope = beanScope;
        return this;
    }

    public List<String> getConstructorDependencies() {
        return constructorDependencies;
    }

    public BeanDefinition setConstructorDependencies(List<String> constructorDependencies) {
        this.constructorDependencies = constructorDependencies;
        return this;
    }

    public void addConstructorDependencies(String... constructorDependencies) {
        this.constructorDependencies.addAll(Arrays.asList(constructorDependencies));
    }

    public boolean hasMandatoryDependencies() {
        return !constructorDependencies.isEmpty();
    }

    public Map<String, String> getSetterDependencies() {
        return setterDependencies;
    }

    public BeanDefinition setSetterDependencies(HashMap<String, String> setterDependencies) {
        this.setterDependencies = setterDependencies;
        return this;
    }

    public void addSetterDependency(String propertyName, String beanReference) {
        setterDependencies.put(propertyName, beanReference);
    }

    public void addSetterDependencies(Map<String, String> setterDependencies) {
        this.setterDependencies = Stream.of(this.setterDependencies, setterDependencies)
                                        .flatMap(map -> map.entrySet().stream())
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue
                                        ));
    }

    public Map<String, String> getPropertyValues() {
        return propertyValues;
    }

    public BeanDefinition setPropertyValues(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;
        return this;
    }

    public void addPropertyKeyWithValue(String property, String value) {
        propertyValues.put(property, value);
    }

    public String getAccessorNameByPropertyName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(beanName, that.beanName) &&
                Objects.equals(beanClassName, that.beanClassName) &&
                Objects.equals(beanScope, that.beanScope) &&
                Objects.equals(constructorDependencies, that.constructorDependencies) &&
                Objects.equals(setterDependencies, that.setterDependencies) &&
                Objects.equals(propertyValues, that.propertyValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, beanClassName, beanScope, constructorDependencies, setterDependencies, propertyValues);
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanName='" + beanName + '\'' +
                ", beanClassName='" + beanClassName + '\'' +
                ", beanScope='" + beanScope + '\'' +
                ", constructorDependencies=" + constructorDependencies +
                ", setterDependencies=" + setterDependencies +
                ", propertyValues=" + propertyValues +
                '}';
    }

}
