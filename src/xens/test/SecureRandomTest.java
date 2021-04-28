package xens.test;

import java.security.SecureRandom;

public class SecureRandomTest {
    public static void random(){
        SecureRandom random = new SecureRandom();
        byte[] data = new byte[16];
        String strKey = "123";
        random.setSeed(16);
        random.nextBytes(data);
        System.out.println(data);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            random();
        }
    }
}
