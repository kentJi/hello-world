package com.fenixsoft.classloading;

import java.lang.reflect.Method;

public class JavaClassExecuter {
    public static String execute(byte[] classBytes)
    {
        HackSystem.clearBuffer();

        ClassModifier cm = new ClassModifier(classBytes);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System", "com/fenixsoft/classloading/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadByte(modiBytes);
        try
        {
            Method method = clazz.getMethod("main", new Class[] { String[].class });
            method.invoke(null, new String[] {null});
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return HackSystem.getBufferString();
    }
}
