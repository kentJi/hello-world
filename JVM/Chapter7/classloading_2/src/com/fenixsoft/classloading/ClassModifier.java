package com.fenixsoft.classloading;

public class ClassModifier {
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;

    private static final int CONSTANT_Utf8_info = 1;

    private static final int[] CONSTANT_ITEM_LENGTH = {-1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5};

    private static final int u1 = 1;
    private static final int u2 = 2;

    private byte[] classByte;

    public ClassModifier(byte[] classByte)
    {
        this.classByte = classByte;
    }

    public byte[] modifyUTF8Constant(String oldStr, String newStr)
    {
        int cpc = getConstantPoolCount();
        System.out.println("constant pool count = " + cpc);
        int offset = CONSTANT_POOL_COUNT_INDEX + u2;
        for(int i = 0; i < cpc; i++)
        {
            int tag = ByteUtils.byte2Int(classByte, offset, u1);
            if(tag == CONSTANT_Utf8_info)
            {
                int len = ByteUtils.byte2Int(classByte, offset + u1, u2);
                offset += (u1 + u2);
                String str = ByteUtils.byte2String(classByte, offset, len);
                System.out.println("find utf-8 : " + str);
                if(str.equals(oldStr))
                {
                    byte[] strByte = ByteUtils.string2Bytes(newStr);
                    byte[] strLen = ByteUtils.int2Bytes(strByte.length, u2);
                    classByte = ByteUtils.bytesReplace(classByte, offset - u2, u2, strLen);
                    classByte = ByteUtils.bytesReplace(classByte, offset, len, strByte);
                    return classByte;
                }
                else
                    offset += len;
            }
            else
            {
                offset += CONSTANT_ITEM_LENGTH[tag];
            }
        }
        System.out.println("not find and return here");
        return classByte;
    }

    private int getConstantPoolCount()
    {
        return ByteUtils.byte2Int(classByte, CONSTANT_POOL_COUNT_INDEX, u2);
    }
}
