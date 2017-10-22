package net;

import utils.FileLoader;

public class NetTest
{
    public static void main(String[] args) throws Exception
    {
        new Server().start();
        Thread.sleep(1000);
        Client client1 = new Client();
        client1.start();

        byte[] fileBytes = new FileLoader().load("com.fenixsoft.classloading.Test");
        client1.sendMsg(fileBytes);
        client1.stop();
    }
}
