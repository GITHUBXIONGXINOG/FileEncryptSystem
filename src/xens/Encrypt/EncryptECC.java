package xens.Encrypt;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

import static xens.test.EccTest.getPrivateKey;
import static xens.test.EccTest.getPublicKey;


public class EncryptECC {
    private final static int KEY_SIZE = 256;//bit
    private final static String SIGNATURE = "SHA256withECDSA";
    private static KeyPair keyPair;//密钥对
    private static ECPublicKey pubKey;//公钥
    private static ECPrivateKey priKey;//私钥
    private static String priKeyPath;//私钥保存地址

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 初始化实例
     * @param key 用户输入随机数种子
     * @param path 文件夹路径
     * @param fileName 文件名字
     */
    public EncryptECC(String key, String path, String fileName){
        try {
            //保存密钥对
             keyPair = getKeyPair(key);
             pubKey = (ECPublicKey) keyPair.getPublic();
             priKey = (ECPrivateKey) keyPair.getPrivate();
            System.out.println("[pubKey]:\n" + getPublicKey(keyPair));
            System.out.println("[priKey]:\n" + getPrivateKey(keyPair));
            priKeyPath = path+"\\"+fileName+"_priKey.txt";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //生成秘钥对
    public static KeyPair getKeyPair(String strKey) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");//BouncyCastle
//         new SecureRandom().setSeed(Long.parseLong(key));
        KeyPair keyPair = null;
        //用户没有输入密钥
        if (strKey.length()==0){
            keyPairGenerator.initialize(KEY_SIZE,  new SecureRandom());
            keyPair = keyPairGenerator.generateKeyPair();
        }else {//用户输入密钥
            byte[] decodedKey = strKey.getBytes("UTF-8");
            //创建 SecureRandom 实例,并指定随机数生成器算法
            SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
            //将用户的输入密钥数组作为随机数种子
            secureRandom.setSeed(decodedKey);

            keyPairGenerator.initialize(KEY_SIZE,  secureRandom);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        return keyPair;
    }

    /**
     * 加密
     * @param content 传入byte数组
     * @return 返回加密后的byte数组
     */

    public static byte[] encrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(content);
    }


    /**
     * 解密
     * @param content 传入加密后的byte数组
     * @return 返回解密后的byte数组
     */
    //私钥解密
    public static byte[] decrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(content);
    }

    //保存私钥
    public static int savePriveKey(){
//        File file = new File(priKeyPath);
        try {
            FileOutputStream fs = new FileOutputStream(priKeyPath);
            fs.write(getPrivateKey(keyPair).getBytes());
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
        return 1;
    }
}
