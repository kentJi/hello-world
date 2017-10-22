package com.fenixsoft.classloading;

public class ConstClass {
    static{
        System.out.println("Const class init");
    }

    public static final String HELLOWORLD = "hello world";
}
