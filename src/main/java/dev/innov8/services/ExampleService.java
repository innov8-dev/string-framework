package dev.innov8.services;

import dev.innov8.repositories.ExampleRepository;

public class ExampleService {

    private ExampleRepository userRepo;
    private AnotherService anotherService;
    private PrototypeService prototype;

    public ExampleService(ExampleRepository userRepo, AnotherService anotherService) {
        System.out.println("ExampleService is being instantiated.");
        this.userRepo = userRepo;
        this.anotherService = anotherService;
        System.out.println("ExampleService instantiation complete.");
    }

    public PrototypeService getPrototype() {
        return prototype;
    }

    public void setPrototype(PrototypeService prototype) {
        this.prototype = prototype;
    }

    public void test() {
        System.out.println("ExampleService.test() invoked!");
        userRepo.test();
    }

    public void test2() {
        System.out.println("ExampleService.test2() invoked!");
        System.out.println(prototype.getSomeValue());
    }

    public void test3() {
        System.out.println("ExampleService.test3() invoked!");
        System.out.println(anotherService.getLongValue());
    }

}
