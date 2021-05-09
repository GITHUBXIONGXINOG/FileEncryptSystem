package xens.Encrypt;

import cn.hutool.core.codec.Base64Decoder;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static xens.test.EccTest.getPrivateKey;


public class EncryptRSA {
    private final static int KEY_SIZE = 1024;
    private static KeyPair keyPair;//密钥对
    private static RSAPublicKey pubKey;//公钥
    private static RSAPrivateKey priKey;//私钥
    private static String priKeyPath;//私钥保存地址
    private static Cipher cipher;//解密
    private int mode;//模式
    public Boolean status = true;//状态

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 初始化实例
     *
     * @param mode     0为加密时的初始,1为解密时的初始
     * @param key      用户输入随机数种子
     * @param path     文件夹路径
     * @param fileName 文件名字
     */
    public EncryptRSA(int mode, String key, String path, String fileName) {
        try {
            this.mode = mode;
            if (mode == 0) {
                //保存密钥对
                keyPair = getKeyPair(key);
                pubKey = (RSAPublicKey) keyPair.getPublic();
                priKey = (RSAPrivateKey) keyPair.getPrivate();
                String publicKeyString = Base64.getEncoder().encodeToString(pubKey.getEncoded());
                System.out.println("公钥:"+publicKeyString);
                // 得到私钥字符串
                String privateKeyString = Base64.getEncoder().encodeToString(priKey.getEncoded());
                //私钥
                System.out.println("私钥:"+privateKeyString);


                priKeyPath = path + "\\" + fileName + ".prikey";
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            } else {//解密
                try {
//                    String realPK = key.replaceAll( "-----END PRIVATE KEY-----", "") .replaceAll( "-----BEGIN PRIVATE KEY-----", "").replaceAll( " \n ", "");
//                    realPK = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIXC84p8fjkbTOLTv4p23Ol5ueMl9n4NVvyT2+/eqXMMtIqIJ8UQxeAiEYvvdJ1sVCgK9Z83INlj6xGOkJkj8Xu7SGunF+Wn7CKSeav4cMxbtBUJvXRBl1TGdggyVoCPWV5qywzGWX5PBvAPBqzxGw01ZvpXf7ZZY18ozVZ7vNszAgMBAAECgYBmtBlq68u+7UdLomofVsSoP96Kmkt43aU8qgrlUoGo5Mh61bAzj31faskuON3BQbwcQs31Qc7nG+ERyfUcoIx443Od1c3IRPihfWtHtjVV+TejXYm74eXK7zAZ9dSc0NxiZd1IZrgIYH/VJU8kfDn7O2oATD6/H4H/d2TBVfPUgQJBAN668Uv2pzHK1Cy4H8IhRMmg+qyzlEMhakE68J1+Zb3Rt2UYIUlX0y4sEo/OaXO19U4y70QnSn3uR+wBFJdVVt0CQQCZvedh/VtYFtqy23zmzH8cym5XRiTMZETEQkpDSiGgznP5PxkNKXoqAuKEfSim1bYyY677QZJeLbWCx5K/lfFPAkBMA+NceKbhUIcPaBu2pqISanWDQZrFOn9IGfSK29ufaBa+UvjxTh8N8A65s8m7qws4kBum1/4NcqhrPIcpiv+lAkBkCjuG+sq2fmCe483ZjRXlkR/NsHn/eft5Tb1vHczWC6FRfap6cxPoRGpLVfne3c+p6E2Tdy6cE9g5mk22VwZnAkAQ93bfTeWte1JvvSLvd6cu3TAZ+GFczkt+v6x4u/hLc8VdGLprzfV07gVR43qxavvGVq2OQ7qCyqp0DI1ps1Q5";
//                    String strKey = key.trim();
//                    System.out.println(strKey.equals(realPK));
                    byte[] decoded = Base64.getDecoder().decode(key.trim());
                    System.out.println(decoded);
                    priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
                    cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, priKey);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                    status = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
    }

    //改变cipher模式
    public void changeCipherMode() throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
    }


    //生成秘钥对
    public static KeyPair getKeyPair(String strKey) throws Exception {
        //初始化,基于RSA生成对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = null;
        //用户没有输入密钥
        if (strKey.length() == 0) {
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            keyPair = keyPairGenerator.generateKeyPair();
        } else {//用户输入密钥
            byte[] decodedKey = strKey.getBytes("UTF-8");
            //创建 SecureRandom 实例,并指定随机数生成器算法
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //将用户的输入密钥数组作为随机数种子
            secureRandom.setSeed(decodedKey);
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        return keyPair;
    }

    /**
     * 加密
     *
     * @param content 传入byte数组
     * @return 返回加密后的byte数组
     */

    public static byte[] encrypt(byte[] content) throws Exception {
        return cipher.doFinal(content);
    }


    /**
     * 解密
     *
     * @param content 传入加密后的byte数组
     * @return 返回解密后的byte数组
     */
    //私钥解密
    public static byte[] decrypt(byte[] content) {
        try {
            return cipher.doFinal(content);
        } catch (Exception e) {
            System.out.println(e);
            byte[] wrong = new byte[1];
            wrong[0] = 0;
            return wrong;
        }
    }

    //保存私钥
    public static int savePriveKey() {
        try {
            FileOutputStream fs = new FileOutputStream(priKeyPath);
            fs.write(Base64.getEncoder().encodeToString(priKey.getEncoded()).getBytes());
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
        return 1;
    }
}
