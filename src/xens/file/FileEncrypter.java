package xens.file;

import xens.Encrypt.EncryptAES;
import xens.Encrypt.EncryptDES;
import xens.Encrypt.Encrypter;
import xens.Encrypt.EncryptAES;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class FileEncrypter {
    public FileEncrypter() {
    }
    //创建输出域
    JTextArea consoleArea;
    //创建文件加密输出,传入输出内容
    public FileEncrypter(JTextArea consoleArea) {
        this.consoleArea = consoleArea;
    }
    //创建打印类
    void print(String str) {
        System.out.println(str);
        if (consoleArea != null) {
            consoleArea.append(str);
            consoleArea.append("\r\n");
        }
    }

    public int encrypt(File file,JTextField encryptFilePath, int method, String key){
        String EncryptPath = encryptFilePath.getText();
        //创建文件类
//        File file = new File(EncryptPath);

        Date start = new Date();
        print("正在加密: "+ file.getName());
        try{
            //文件名字拼接

            //拆分地址
            String[] pathArray = EncryptPath.split("\\.");
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
            newPath += "_encrypt." + suffix;
            File outFile = new File(newPath);
            //如果当前加密文件存在,删除加密文件
            if (outFile.exists()){
                outFile.delete();
            }
            switch(method){
                case 0:
                    EncryptDES encryptDES = new EncryptDES();
                    encryptDES.encrypt(EncryptPath,newPath,key);

                    break;
                case 1:
//                value = encryptAlgMultiple(content, key);
                    EncryptAES encryptAES = new EncryptAES(EncryptPath,key);
//                    encryptAES.encrypt(EncryptPath,newPath);
//                    Encrypter.encrypt(EncryptPath,)
                    AESFileOp(EncryptPath,newPath,0,encryptAES);
                    break;
                default:
                    break;
            }


            Date end = new Date();
            long duration = (end.getTime() - start.getTime());
            if (duration > 1000000) {
                print("用时" + (duration) / 1000 + "s");
            } else {
                print("用时" + (duration) + "ms");
            }
            return 1;

        }catch (Exception e){
            return 0;//发生错误
        }
    }

    private int AESFileOp(String encryptPath, String newPath, int method, EncryptAES encryptAES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        InputStream is = new FileInputStream(encryptPath);
        OutputStream out = new FileOutputStream(newPath);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) > 0) {
            byte[] temp = new byte[r];
            System.arraycopy(buffer,0,temp,0,r);
//                out.write(aes(temp, Cipher.DECRYPT_MODE,this.secretKey));
            if (method==0){//加密
//                String encoded = Base64.getEncoder().encodeToString(temp);
                byte[] res = encryptAES.encrypt(temp);

                out.write(res);
                out.flush();
            }else {//解密
//                String strTemp = new String(temp,"UTF-8");
//                byte[] decoded = Base64.getDecoder().decode(strTemp);
               byte[] res =  encryptAES.decrypt(temp);
//                byte[] decoded = Base64.getDecoder().decode(res);
                out.write(res );
                out.flush();
            }


        }
        return 1;
    }

    public int decrypt(File file,JTextField decryptFilePath, int method, String key){
        String decryptPath = decryptFilePath.getText();
        //创建文件类
//        File file = new File(EncryptPath);

        //获取加密文件父文件
//        File pFolder = file.getParentFile();
        Date start = new Date();
        print("正在解密: "+ file.getName());
        try{
//            //读取文件
//            RandomAccessFile oRAF = new RandomAccessFile(file,"r");
//            //文件名字拼接
//            File distFile = new File(pFolder,file.getName() + ".encrypt" );
//            //如果当前加密文件存在,删除加密文件
//            if (distFile.exists()){
//                distFile.delete();
//            }
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
            newPath += "_encrypt." + suffix;
            File outFile = new File(newPath);
            //如果当前加密文件存在,删除加密文件
            if (outFile.exists()){
                outFile.delete();
            }

            int value;
            switch(method){
                case 0:
                    EncryptDES decryptDES = new EncryptDES();
                    decryptDES.decrypt(decryptPath,key);

                    break;
                case 1:
//                value = encryptAlgMultiple(content, key);
                    EncryptAES encryptAES = new EncryptAES(newPath,key);
//                    encryptAES.decrypt(decryptPath,newPath);
                    AESFileOp(decryptPath,newPath,1,encryptAES);

                    break;
                case 2:
//                value = encryptAlgMove(content, key);
                    break;
                default:
                    break;
            }
            Date end = new Date();
            long duration = (end.getTime() - start.getTime());
            if (duration > 1000000) {
                print("用时" + (duration) / 1000 + "s");
            } else {
                print("用时" + (duration) + "ms");
            }
            return 1;

        }catch (Exception e){
            return 0;//发生错误
        }
    }


}
