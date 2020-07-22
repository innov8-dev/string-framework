package dev.innov8.config;

import dev.innov8.exceptions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static dev.innov8.util.BeanUtils.*;
import static dev.innov8.util.StringUtils.mapPropertyNameToAccessorName;

public class AppContext implements AutoCloseable {

    private final String registryLocation;
    private final List<BeanDefinition> registry;
    private final Map<String, Class<?>> singletonBeans;

    public AppContext(String registryLocation) {
        this.registryLocation = registryLocation;
        this.registry = new ArrayList<>();
        this.singletonBeans = new HashMap<>();

        loadBeanDefinitions();
        instantiateSingletons();
    }

    // TODO support any order for bean definition declarations within the XML registry
    // TODO support literal value injection for more than just Strings
    public void loadBeanDefinitions() {

        try {

            File file = new File(registryLocation);
            Document registryDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            registryDoc.normalizeDocument();

            NodeList nodeList = registryDoc.getElementsByTagName("bean");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Element currentBeanElement = (Element) nodeList.item(i);
                if (currentBeanElement.getNodeName().equals("bean")) {

                    String currentBeanName = currentBeanElement.getAttribute("name");
                    String currentBeanClassName = currentBeanElement.getAttribute("class");
                    String currentBeanScope = currentBeanElement.getAttribute("scope");

                    if (!isBeanNameValid(currentBeanName)) {
                        throw new BeanRegistryParsingException("Invalid name attribute provided for bean element at index: " + i);
                    }

                    if (!isBeanNameUnique(currentBeanName)) {
                        throw new BeanRegistryParsingException("Duplicate bean name found for bean element at index: " + 1);
                    }

                    if (!isBeanClassNameValid(currentBeanClassName)) {
                        throw new BeanRegistryParsingException("Invalid class name provided for bean element at index: " + i);
                    }

                    if (isBeanScopeValid(currentBeanScope)) {
                        throw new BeanRegistryParsingException("The bean, " + currentBeanName + ", specifies an invalid scope.");
                    }


                    BeanDefinition beanDefinition = new BeanDefinition(currentBeanName, currentBeanClassName, currentBeanScope);

                    NodeList constructorArgElements = currentBeanElement.getElementsByTagName("constructor-arg");

                    for (int j = 0; j < constructorArgElements.getLength(); j++) {

                        Element constructorArgElement = (Element) constructorArgElements.item(j);
                        String dependencyBeanName = constructorArgElement.getAttribute("ref");

                        if (dependencyBeanName != null & !dependencyBeanName.equals(currentBeanName)) {
                            beanDefinition.addConstructorDependencies(dependencyBeanName);
                        } else {
                            throw new BeanDefinitionCreationException(currentBeanName, new SelfReferencingBeanException(currentBeanName));
                        }

                    }

                    NodeList propertyElements = currentBeanElement.getElementsByTagName("property");

                    for (int j = 0; j < propertyElements.getLength(); j++) {

                        Element propertyElement = (Element) propertyElements.item(j);
                        String propertyName = propertyElement.getAttribute("name");
                        String propertyRef = propertyElement.getAttribute("ref");
                        String propertyValue = propertyElement.getAttribute("value");

                        if (propertyName.trim().equals("")) {
                            throw new BeanRegistryParsingException("The bean, " + currentBeanName + ", has a property with no name!");
                        }

                        if (!propertyRef.trim().equals("")) {
                            beanDefinition.addSetterDependency(propertyName, propertyRef);
                        } else if (!propertyValue.trim().equals("")) {
                            beanDefinition.addPropertyKeyWithValue(propertyName, propertyValue);
                        } else {
                            throw new BeanRegistryParsingException("The bean, " + currentBeanName + ", declares a property with no ref or value attributes.");
                        }
                    }

                    registry.add(beanDefinition);

                }
            }

        } catch (Exception e) {
            throw new BeanRegistryParsingException(e.getMessage());
        }

    }

    protected void instantiateSingletons() {

        for (BeanDefinition beanDefinition : registry) {
            if (beanDefinition.getBeanScope().equals("singleton")) {
                singletonBeans.put(beanDefinition.getBeanName(), createBeanFromBeanDefinition(beanDefinition));
            }
        }

    }

    public <T> T getBean(String beanName, Class<T> requiredType) {

        T bean = (T) singletonBeans.get(beanName);

        if (bean != null) {
            return bean;
        }

        BeanDefinition beanDefinition = registry.stream()
                .filter(beanDef -> beanDef.getBeanName().equals(beanName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanException(beanName));

        return createBeanFromBeanDefinition(beanDefinition);

    }

    protected <T> T createBeanFromBeanDefinition(BeanDefinition beanDefinition) {

        // Create a initialized generic bean reference
        T bean = null;
        try {

            // Determine if the bean has mandatory dependencies
            if (beanDefinition.hasMandatoryDependencies()) {

                List<Class<?>> dependencyClasses = new ArrayList<>();
                List<Object> dependencyBeans = new ArrayList<>();

                // Iterate
                for (String dependencyName : beanDefinition.getConstructorDependencies()) {

                    BeanDefinition dependencyDef = getBeanDefinitionByBeanName(dependencyName);

                    Class<?> dependencyClass = dependencyDef.getBeanClass();
                    dependencyClasses.add(dependencyClass);

                    Object dependencyBean = getBean(dependencyDef.getBeanName(), dependencyClass);
                    dependencyBeans.add(dependencyBean);

                }

                Class<?>[] classArray = new Class<?>[dependencyClasses.size()];
                classArray = dependencyClasses.toArray(classArray);

                Object[] dependencyArray = new Object[dependencyBeans.size()];
                dependencyArray = dependencyBeans.toArray(dependencyArray);

                Constructor<?> constructor = beanDefinition.getBeanClass().getDeclaredConstructor(classArray);
                bean = (T) constructor.newInstance(dependencyArray);

            } else {
                bean = (T) beanDefinition.getBeanClass().newInstance();
            }

            populateProperties(bean, beanDefinition.getSetterDependencies(), beanDefinition.getPropertyValues());

        } catch (Exception e) {
            throw new BeanCreationException(beanDefinition.getBeanName(), e);
        }

        return bean;

    }

    protected <T> void populateProperties(T bean, Map<String, String> dependencies, Map<String, String> propertyValues) throws Exception {

        // Injects the optional dependencies using the provided dependencies map
        for (String propertyName : dependencies.keySet()) {

            // Obtain the appropriate bean definition using the dependencies map and property name
            BeanDefinition dependencyDef = getBeanDefinitionByBeanName(dependencies.get(propertyName));

            // Create a reference to this dependencies class type using the bean definition
            Class<?> dependencyClass = dependencyDef.getBeanClass();

            // Obtain a instance of the dependency using the `getBean(String, Class<T>)` method
            Object dependencyBean = getBean(dependencyDef.getBeanName(), dependencyClass);

            // Create a string that represents the accessor method's name given the property name
            String accessorMethodName = mapPropertyNameToAccessorName(propertyName);

            //Find the accessor method for the property name (assuming standard naming convention)
            Method beanAccessorMethod = bean.getClass().getDeclaredMethod(accessorMethodName, dependencyClass);

            // Invoke the accessor method on the provided bean instance passing in the parameter value
            beanAccessorMethod.invoke(bean, dependencyBean);

        }

        // Injects the literal property values of the bean using the provided property values map
        for (String propertyName : propertyValues.keySet()) {

            // Find the single-parameter accessor method for the property name (assuming standard naming convention)
            Method beanAccessorMethod = Arrays.stream(bean.getClass().getMethods())
                                            .filter(method ->
                                                              method.getName().equals(mapPropertyNameToAccessorName(propertyName))
                                                              &&
                                                              method.getParameterCount() == 1)
                                            .findFirst()
                                            .orElseThrow(() -> new NoSuchPropertyException(propertyName));

            // Determine the type of the accessor method's one parameter
            Class<?> paramClass = beanAccessorMethod.getParameterTypes()[0];

            // Obtain the parameter's value using the provided property values map and property name
            Object paramValue = propertyValues.get(paramClass.cast(propertyName));

            // Invoke the accessor method on the provided bean instance passing in the parameter value
            beanAccessorMethod.invoke(bean, paramValue);

        }

    }

    protected boolean isBeanNameUnique(String beanName) {
        return !registry.stream()
                        .map(BeanDefinition::getBeanName)
                        .collect(Collectors.toList())
                        .contains(beanName);
    }

    protected BeanDefinition getBeanDefinitionByBeanName(String beanName) {
        return registry.stream()
                .filter(beanDef -> beanDef.getBeanName().equals(beanName))
                .findFirst()
                .orElseThrow(() -> new UnresolvableDependencyException(beanName, new NoSuchBeanException(beanName)));
    }

    @Override
    public void close() throws Exception {
        // TODO graceful shutdown (implement AutoCloseable)
    }
}
