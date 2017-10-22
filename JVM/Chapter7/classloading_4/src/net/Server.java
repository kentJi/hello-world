package net;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import classloading.*;
import utils.ClassModifier;
import utils.HackSystem;

public class Server
{
    private static final int DEFAULT_PORT = 12345;
    private ServerHandle serverHandle;

    void start()
    {
        if (serverHandle != null)
            serverHandle.stop();

        serverHandle = new ServerHandle(DEFAULT_PORT);
        new Thread(serverHandle, "Server").start();
    }
}

class ServerHandle implements Runnable
{
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean started;

    ServerHandle(int port)
    {
        try
        {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            started = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    void stop()
    {
        started = false;
    }

    @Override
    public void run()
    {
        while(started)
        {
            try
            {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while(it.hasNext())
                {
                    SelectionKey key = it.next();
                    it.remove();
                    try
                    {
                        handleInput(key);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        close();
                        key.cancel();
                        if(key.channel() != null)
                            key.channel().close();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                close();
                System.exit(1);
            }
        }
        close();
    }

    private void handleInput(SelectionKey key) throws Exception
    {
        if (key.isValid())
        {
            if (key.isAcceptable())
            {
                System.out.println("[Server]get new connection");
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable())
            {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readByte = sc.read(buffer);
                if (readByte > 0)
                {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);

                    ClassModifier classModifier = new ClassModifier(bytes);
                    HackSystem.clearBuffer();
                    byte[] modiBytes =classModifier.modifyUTF8Constant("java/lang/System", "utils/HackSystem");

                    Class<?> clazz = new MyClassLoader().loadClassFromNet(modiBytes);
                    Object obj = clazz.newInstance();

                    Method method = clazz.getMethod("main", new Class[] {String[].class});
                    method.invoke(obj, new String[] {null});

//                    System.out.println("[Server]server execute result = " + HackSystem.getBufferString());
                    doWrite(sc, HackSystem.getBufferString());
                }
                else if (readByte < 0)
                {
                    System.out.println("[Server]one connection closed");
                    sc.close();
                    key.channel();
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException
    {
        byte[] bytes = response.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        channel.write(writeBuffer);
    }

    private void close()
    {
        if(selector != null)
        {
            try
            {
                selector.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if(serverChannel != null)
        {
            try
            {
                serverChannel.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
