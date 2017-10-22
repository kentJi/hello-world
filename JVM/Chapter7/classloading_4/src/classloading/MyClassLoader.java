package classloading;

public class MyClassLoader extends ClassLoader
{
    public MyClassLoader()
    {

    }

    public MyClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    public Class<?> loadClassFromNet(byte[] bytes)
    {
        System.out.println("[ClassLoader]loadClassFromNet and length = " + bytes.length);
        return defineClass(null, bytes, 0, bytes.length);
    }
}
