package dev.innov8;

import dev.innov8.config.AppContext;
import dev.innov8.services.ExampleService;
import dev.innov8.services.PrototypeService;

public class AppDriver {

    public static void main(String[] args) {

        try(AppContext container = new AppContext("src/main/resources/beans.xml");) {

            ExampleService service1 = container.getBean("exampleService", ExampleService.class);
            service1.test();
            service1.test2();

            ExampleService service2 = container.getBean("exampleService", ExampleService.class);
            service2.test3();

            System.out.println(service1 == service2); // should be true, indicating a single shared instance

            System.out.println("----------------------------------------------------------------------------------");

            PrototypeService prototype1 = container.getBean("prototypeService", PrototypeService.class);
            prototype1.getSomeValue(); // should be "testValue", indicating successful literal value injection

            PrototypeService prototype2 = container.getBean("prototypeService", PrototypeService.class);
            System.out.println(prototype1 == prototype2); // should be false, indicating a new instance per request

            System.out.println(service1.getPrototype() == prototype1);
            System.out.println(service1.getPrototype() == prototype2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
