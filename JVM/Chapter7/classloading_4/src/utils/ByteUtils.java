package utils;

public class ByteUtils
{
    public static int byte2Int(byte[] b, int offset, int len)
    {
        int sum = 0;
        int end = offset + len;
        for(int i = offset; i < end; i++)
        {
            int n = ((int)b[i]) & 0xff;
            sum = (sum << 8) + n;
        }
        return sum;
    }

    public static byte[] int2Bytes(int value, int len)
    {
        byte[] b = new byte[len];
        for(int i = 0; i < len; i++)
        {
            int n = value % 256;
            b[len - i - 1] = (byte)(n & 0xff);
            value >>= 8;
        }
        return b;
    }

    public static String byte2String(byte[] b, int start, int len)
    {
        return new String(b, start, len);
    }

    public static byte[] string2Bytes(String str)
    {
        return str.getBytes();
    }

    public static byte[] bytesReplace(byte[] originBytes, int offset, int len, byte[] replaceBytes)
    {
        byte[] newBytes = new byte[originBytes.length - len + replaceBytes.length];
        System.arraycopy(originBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(originBytes, offset + len, newBytes, offset + replaceBytes.length, originBytes.length - offset - len);
        return newBytes;
    }
}
