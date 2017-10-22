package com.fenixsoft.classloading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassLoaderTest {
    static {
        System.out.println("init of ClassLoaderTest");
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException
    {
        System.out.println("at first of main");
//        MyClassLoader cl = new MyClassLoader(ClassLoader.getSystemClassLoader().getParent());
        MyClassLoader cl = new MyClassLoader();
        System.out.println("ready to load class Animal");
        Class<?> clazz = cl.loadClass("com.fenixsoft.classloading.Test");
//        Class<?> clazz = Class.forName("com.fenixsoft.classloading.Test", true, cl);

        Object obj = clazz.newInstance();
        Method method = clazz.getMethod("main", new Class[] {String[].class});
        method.invoke(obj, new String[] {null});
    }
}

class MyClassLoader extends ClassLoader
{
    private String path = "E:\\code\\Java\\JVM\\Chapter7\\classloading_2\\out\\production\\classloading_2\\";

    public MyClassLoader()
    {

    }

    public MyClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException
    {
        byte[] data = loadClassData(name);
        return this.defineClass(name, data, 0, data.length);
    }

    private byte[] loadClassData(String name)
    {
        try
        {
            System.out.println("in loadClassData and name = " + name);
            name = name.replace(".", "\\");
            FileInputStream is = new FileInputStream(new File(path + name + ".class"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = 0;
            while((b = is.read()) != -1)
                baos.write(b);
            is.close();
            baos.close();
            return baos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
