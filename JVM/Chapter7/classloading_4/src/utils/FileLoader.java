package utils;

import java.io.*;

public class FileLoader
{
    private static final String path = "E:\\code\\Java\\JVM\\Chapter7\\classloading_2\\out\\production\\classloading_2\\";

    public byte[] load(String name) throws IOException
    {
        name = name.replace(".", "\\");
        FileInputStream fin = new FileInputStream(new File(path + name + ".class"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = 0;
        while((b = fin.read()) != -1)
            baos.write(b);
        System.out.println("[Client]read file and bytes length = " + baos.toByteArray().length);
        return baos.toByteArray();
    }

    public void dump(String name, byte[] buffer) throws IOException
    {
        name = name.replace(".", "\\");
        FileOutputStream fout = new FileOutputStream(new File(path + name + ".class"));
        fout.write(buffer);
        fout.flush();
        fout.close();
    }
}
