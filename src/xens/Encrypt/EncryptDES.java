package xens.Encrypt;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.swing.*;
import java.io.*;
import java.security.Key;
import java.security.SecureRandom;

public class EncryptDES {
    Key key;//建立key对象,需要定义密钥算法,编码,编码密钥的格式
//    创建共有的EncryptDES类,可以被该类的和非该类的任何成员访问
    public EncryptDES() {//传入输入密钥
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
            _generator.init(SecureRandom.getInstance(strKey));
            this.key = _generator.generateKey();//生成密钥
            _generator = null;//将密钥生成器实例置空
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
    }
    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "12345678";
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }



    public void encrypt(String EncryptPath,String outPath,String strKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        // cipher.init(Cipher.ENCRYPT_MODE, getKey());
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(strKey));
        InputStream is = new FileInputStream(EncryptPath);
        OutputStream out = new FileOutputStream(outPath);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();

    }
    public void decrypt(String decryptPath, String strKey) throws Exception {
        //拆分地址
        String[] pathArray = decryptPath.split("\\.");
        //存储地址到newPath
        String newPath = pathArray[0];
        //后缀
        String suffix = pathArray[pathArray.length-1];
        int num = 0;
        int i = 1;
        //当有多个.时,数组个数大于2
        if (pathArray.length>2){
            //赋值为超出2的个数
            num = pathArray.length-2;
            while (i<=num){
                newPath += "."+pathArray[i];
                i++;
            }
        }
        //拼接文件名
        newPath += "_decrypt." + suffix;

        File distFile = new File(newPath);
        //如果当前加密文件存在,删除加密文件
        if (distFile.exists()){
            distFile.delete();
        }
        Cipher cipher = Cipher.getInstance("DES");//返回实现指定转换的密码对象
        //init使用密钥初始化此密码
//        cipher.init(Cipher.DECRYPT_MODE, this.key);//DECRYPT_MODE, 用于将密码初始化为解密模式的常量。
        cipher.init(Cipher.DECRYPT_MODE, generateKey(strKey));

        InputStream is = new FileInputStream(decryptPath);//流读取文件
        OutputStream out = new FileOutputStream(newPath);//流输出文件
        CipherOutputStream cos = new CipherOutputStream(out, cipher);//由一个OutputStream和一个密码组成,对数据进行加密后写入
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) >= 0) {
            System.out.println();
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
    }

}
