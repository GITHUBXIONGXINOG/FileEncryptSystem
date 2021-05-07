package xens.Encrypt;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class EncrySM4 {
    SecretKey secretKey;
    SymmetricCrypto sm4;
    public EncrySM4(String strKey){
        init(strKey);
    }
    /**
     * 根据参数生成KEY
     * 使用try/catch捕获错误
     */
    public void init(String strKey) {
        try {
            //将用户输入密钥转为byte数组
            byte[] decodedKey = strKey.getBytes("UTF-8");
            //创建 SecureRandom 实例,并指定随机数生成器算法
            SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
            //将用户的输入密钥数组作为随机数种子
            secureRandom.setSeed(decodedKey);
            //生成随机数,并存入byteKey中
            byte[] byteKey = new byte[16];
            secureRandom.nextBytes(byteKey);
            sm4 = SmUtil.sm4(byteKey);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
    }
    public byte[] encrypt(byte[] content){
        return sm4.encrypt(content);
    }
    public byte[] decrypt(byte[] content){
        return sm4.decrypt(content);
    }
}
