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
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
//            byte[] encryptResult = encrypt(content,secretKey);


    }
    static final String ALGORITHM = "AES";
    /**
     * 生成key函数
     * @return 返回key
     */
    public static SecretKey generateKey(String strKey) throws NoSuchAlgorithmException, IOException {


        //要生成什么加密方式的key,生成aes的实例
//        KeyGenerator secretGenerator = KeyGenerator.getInstance(ALGORITHM);

//        SecureRandom secureRandom = new SecureRandom();
        byte[] decodedKey = Base64.getDecoder().decode(strKey);
//        secureRandom.setSeed(decodedKey);
        SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(decodedKey);
//        Integer randNum = secureRandom.nextInt();
        //对key进行初始化
//        secretGenerator.init(secureRandom);
        byte[] byteKey = new byte[16];
        secureRandom.nextBytes(byteKey);

//        int s = secureRandom.nextInt();
//         secureRandom.nextBytes(decodedKey);
        //生成key
        SecretKey secretKey =  new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
        String stringKey=secretKey.toString();

//        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
// rebuild key using SecretKeySpec
//        SecretKey originalKey = new SecretKeySpec(buffer, 0, buffer.length, "AES");
//        String encodedKey = Base64.getEncoder().encodeToString(originalKey.getEncoded());

        return secretKey;
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
    /**
     * 将字节数组转为long<br>
     * 如果input为null,或offset指定的剩余数组长度不足8字节则抛出异常
     * @param input
     * @param offset 起始偏移量
     * @param littleEndian 输入数组是否小端模式
     * @return
     */
    public static long longFrom8Bytes(byte[] input, int offset, boolean littleEndian){
        long value=0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for(int  count=0;count<8;++count){
            int shift=(littleEndian?count:(7-count))<<3;
            value |=((long)0xff<< shift) & ((long)input[offset+count] << shift);
        }
        return value;
    }
    public static long byteArrayToLong(byte[] data) throws IOException {
        ByteArrayInputStream bai = new ByteArrayInputStream(data);
        DataInputStream dis =new DataInputStream(bai);
        return dis.readLong();
    }
    static Long convertKeys(String keys) {
        int[] intKeys = new int[keys.length()];
        char[] chKeys = keys.toCharArray();
        String strKeys = "";
        Long longKey = null;
        for (int i = 0; i < intKeys.length; i++) {
            if (Character.isDigit(chKeys[i])) {
                int num = (int) chKeys[i] - (int) ('0');
//                intKeys[i] = num;
                strKeys+=num;
            } else {
//                intKeys[i] = 10;
                int n = chKeys[i];
                strKeys+= n;
            }
        }
        return longKey;
    }
}
