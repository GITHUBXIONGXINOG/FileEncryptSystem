package xens.Encrypt;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class EncryptAES {
    SecretKey secretKey;
    public EncryptAES(String content,String strKey){

            //初始化
        try {
             secretKey = generateKey(strKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//            byte[] encryptResult = encrypt(content,secretKey);


    }
    static final String ALGORITHM = "AES";
    /**
     * 生成key函数
     * @return 返回key
     */
    public static SecretKey generateKey(String strKey) throws NoSuchAlgorithmException {
        //要生成什么加密方式的key,生成aes的实例
        KeyGenerator secretGenerator = KeyGenerator.getInstance(ALGORITHM);
        //随机数生成器 Random
        Random random = new Random();
        Long longKey = Long.parseLong(strKey);
        //设置随机数种子
        random.setSeed(longKey);
        byte[] buffer = new byte[16];
        random.nextBytes(buffer);
        //对key进行初始化
//        secretGenerator.init(random);
        //生成key
//        SecretKey secretKey = secretGenerator.generateKey();

//        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
// rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(buffer, 0, buffer.length, "AES");
        String encodedKey = Base64.getEncoder().encodeToString(originalKey.getEncoded());

        return originalKey;
    }
    final static String charsetName = "UTF-8";
    //使用UTF8,避免中文
    static Charset charset = Charset.forName(charsetName);
    /**
     * 加密函数
     * @param content 传入字符串
     * @return 返回加密后的byte数组
     */
    public  byte[] encrypt(byte[] content) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {

        String encodedKey = Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
        //将content转为字节数组,模式为加密操作,密钥
        return aes(content,Cipher.ENCRYPT_MODE,this.secretKey);

    }

    /**
     * 解密函数
     * @param contentArray 加密后的数组
     * @return 返回解密后的字符串
     */
    public  byte[]  decrypt(byte[] contentArray) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        String encodedKey = Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
        //使用字节数组接收aes返回值
        byte[] result = aes(contentArray,Cipher.DECRYPT_MODE,this.secretKey);
        return result;
    }

    /**
     * aes函数
     * @param contentArray 字节数组
     * @param mode 加密/解密 选择
     * @param secretKey 密钥
     * @return
     */
    private static byte[] aes(byte[] contentArray, int mode, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //执行加解密操作需要Cipher对象, 指定类型为AES
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //对cipher进行初始化
        cipher.init(mode,secretKey);
        //数据小直接用doFinal,数据大时,循环,用update,再用doFinal
        byte[] result = cipher.doFinal(contentArray);
        return result;
    }
//    public static void main(String[] args){
//        String content = "你好中国中国你好";
//        try {
////            SecretKey secretKey = generateKey();
////            byte[] encryptResult = encrypt(content,secretKey);
//            System.out.println("加密后的结果为: "+new String(encryptResult,charsetName));
//
//            String decryptResult = decrypt(encryptResult,secretKey);
//            System.out.println("解密后的结果为: "+decryptResult);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
}
