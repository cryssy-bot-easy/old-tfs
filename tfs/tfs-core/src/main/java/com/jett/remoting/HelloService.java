package com.jett.remoting;

/**
 * User: Jett
 * Date: 6/15/12
 */
public class HelloService implements IGreetingService {

    @Override
    public String greet(String name) {
        return "Hello " + name;
    }

}
