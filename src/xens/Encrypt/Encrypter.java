package xens.Encrypt;

public class Encrypter {
    public static  byte[] encrypt(int content, int method, String key) throws Exception {
        byte[] value = new byte[0];
        switch(method){
            case 0:
//                value = encryptAlgAdd(content, key);
                EncryptDES encryptDES = new EncryptDES(key);

                value = encryptDES.encrypt(content);

                break;
            case 1:
//                value = encryptAlgMultiple(content, key);
                break;
            case 2:
//                value = encryptAlgMove(content, key);
                break;
            default:
                break;
        }
        return value;
    }

    public static  byte[] decrypt(int content, int method, String key) throws Exception {
        byte[] value = new byte[0];
        switch(method){
            case 0:
//                value = encryptAlgAdd(content, key);
                EncryptDES encryptDES = new EncryptDES(key);

                value = encryptDES.decrypt(content);

                break;
            case 1:
//                value = encryptAlgMultiple(content, key);
                break;
            case 2:
//                value = encryptAlgMove(content, key);
                break;
            default:
                break;
        }
        return value;
    }
}
