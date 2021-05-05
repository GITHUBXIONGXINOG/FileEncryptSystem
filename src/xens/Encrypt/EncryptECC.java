package xens.Encrypt;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
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
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    public EncryptECC(String key){
        try {
            //保存密钥对
             keyPair = getKeyPair(key);
             pubKey = (ECPublicKey) keyPair.getPublic();
             priKey = (ECPrivateKey) keyPair.getPrivate();
            System.out.println("[pubKey]:\n" + getPublicKey(keyPair));
            System.out.println("[priKey]:\n" + getPrivateKey(keyPair));
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
    public byte[] encrypt(byte[] content){

        return content;
    }

    /**
     * 解密
     * @param content 传入加密后的byte数组
     * @return 返回解密后的byte数组
     */
    public byte[] decrypt(byte[] content){

        return content;
    }
}
