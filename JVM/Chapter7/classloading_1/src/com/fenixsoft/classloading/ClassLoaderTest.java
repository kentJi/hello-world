package com.fenixsoft.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ClassLoaderTest {

    public void print(String s)
    {
        System.out.println("test " + s);
    }

    public static void main(String[] args) throws Exception
    {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException
            {
                try
                {
                    String filename = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(filename);
                    if(is == null)
                        return super.loadClass(name);

                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                }
                catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Class clazz = myLoader.loadClass("com.fenixsoft.classloading.ClassLoaderTest");
        ClassLoaderTest obj = (ClassLoaderTest) clazz.newInstance();
        obj.print("Hello World");
//        Object obj = clazz.newInstance();
//        Method method = clazz.getMethod("print", String.class);
//        method.invoke(obj, "Hello World");

        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.fenixsoft.classloading.ClassLoaderTest);
        System.out.println(obj.getClass() == com.fenixsoft.classloading.ClassLoaderTest.class);
    }
}
