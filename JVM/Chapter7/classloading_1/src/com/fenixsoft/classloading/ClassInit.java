package com.fenixsoft.classloading;

public class ClassInit {
    public static int i = 2;
    static
    {
        i = 1;
    }

    public static void main(String[] args)
    {
        System.out.println(ClassInit.i);
    }
}
