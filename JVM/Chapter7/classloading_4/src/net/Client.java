package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client
{
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 12345;
    private ClientHandle clientHandle;

    void start()
    {
        if (clientHandle != null)
            clientHandle.stop();

        clientHandle = new ClientHandle(DEFAULT_HOST, DEFAULT_PORT);
        new Thread(clientHandle, "Client").start();
    }

    boolean sendMsg(String msg) throws Exception
    {
        if (msg.equals("q"))
        {
            clientHandle.stop();
            return false;
        }

        clientHandle.sendMsg(msg);
        return true;
    }

    boolean sendMsg(byte[] msg) throws Exception
    {
        clientHandle.sendMsg(msg);
        return true;
    }

    public void stop()
    {
        clientHandle.stop();
    }
}


class ClientHandle implements Runnable
{
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean started;

    ClientHandle(String ip, int port)
    {
        this.host = ip;
        this.port = port;
        try
        {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            started = true;
        }
        catch (IOException e)
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
        try
        {
            doConnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while (started)
        {
            try
            {
                selector.select(1000);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while (it.hasNext())
                {
                    key = it.next();
                    it.remove();
                    try
                    {
                        handleInput(key);
                    }
                    catch (Exception e)
                    {
                        if (key != null)
                        {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println(1);
            }
        }
        close();
    }

    private void handleInput(SelectionKey key) throws IOException
    {
        if (key.isValid())
        {
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable())
            {
                if (sc.finishConnect())
                    socketChannel.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable())
            {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readByte = sc.read(buffer);
                if (readByte > 0)
                {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    System.out.println("[Client]receive msg from server: " + result);
                }
                else if (readByte < 0)
                {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    void sendMsg(String msg) throws Exception
    {
        doWrite(socketChannel, msg);
    }

    void sendMsg(byte[] bytes) throws Exception
    {
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
    }

    private void doWrite(SocketChannel socketChannel, String request) throws Exception
    {
        byte[] bytes = request.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
    }

    private void doConnect() throws IOException
    {
        if (socketChannel.connect(new InetSocketAddress(host, port)))
            socketChannel.register(selector, SelectionKey.OP_READ);
        else
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    private void close()
    {
        System.out.println("[Client]client closed");
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

        if(socketChannel != null)
        {
            try
            {
                socketChannel.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
