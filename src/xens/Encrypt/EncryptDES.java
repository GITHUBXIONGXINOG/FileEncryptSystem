package xens.Encrypt;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

public class EncryptDES {
    Key key;//建立key对象,需要定义密钥算法,编码,编码密钥的格式
//    创建共有的EncryptDES类,可以被该类的和非该类的任何成员访问
    public EncryptDES(String strKey) {//传入输入密钥
        getKey(strKey);//生成密匙
    }

    /**
     * 根据参数生成KEY
     * 使用try/catch捕获错误
     */
    public void getKey(String strKey) {
        try {
            //密钥生成器KeyGenerator使用getInstance构建,getInstance传入键值AES (128),DES (56),DESede (168),HmacSHA1,HmacSHA256
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            //使用strKey.getBytes()获取输入密钥的字节数组
            //new SecureRandom(byte[] seed)构造一个实现默认随机数算法的安全随机数生成器
            //init(SecureRandom random) 初始化此密钥生成器
            _generator.init(new SecureRandom(strKey.getBytes()));
            this.key = _generator.generateKey();//生成密钥
            _generator = null;//将密钥生成器实例置空
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
    }


    public byte[] encrypt(int content) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        // cipher.init(Cipher.ENCRYPT_MODE, getKey());
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] encrypted =  cipher.doFinal(intToByteArray(content));
        return encrypted;

    }
    /**
     * int到byte[] 由高位到低位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
    public byte[] decrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
       //init使用密钥初始化此密码 DECRYPT_MODE解密模式
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        byte[] encrypted =  cipher.doFinal(content);
        return encrypted;

    }
}
