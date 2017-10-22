package com.fenixsoft.classloading;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args)
    {
        try {
            InputStream is = new FileInputStream("E:\\code\\Java\\JVM\\Chapter7\\classloading_2\\out\\production\\classloading_2\\com\\fenixsoft\\classloading\\Test.class");
            byte[] b = new byte[is.available()];

            is.read(b);
            is.close();

            String out = JavaClassExecuter.execute(b);
            System.out.println("out = " + out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
