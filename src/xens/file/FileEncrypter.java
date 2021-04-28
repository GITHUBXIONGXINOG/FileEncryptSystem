package xens.file;

import xens.Encrypt.*;
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
            String methodName="";
            switch (method){
                case 0: methodName="DES";break;
                case 1: methodName="AES";break;
            }
            //拼接文件名
            newPath += "_encrypted_"+methodName +'.'+ suffix;
            File outFile = new File(newPath);
            //如果当前加密文件存在,删除加密文件
            if (outFile.exists()){
                outFile.delete();
            }
            switch(method){
                case 0:
                    EncryptDES encryptDES = new EncryptDES(key);
                    DESFileOp(EncryptPath,newPath,0,encryptDES);
//                    encryptDES.encrypt(EncryptPath,newPath,key);

                    break;
                case 1:
                    EncryptAES encryptAES = new EncryptAES(key);
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

    public int decrypt(File file,JTextField decryptFilePath, int method, String key){
        String decryptPath = decryptFilePath.getText();

        Date start = new Date();
        print("正在解密: "+ file.getName());
        try{
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

             switch(method){
                case 0:
                    EncryptDES decryptDES = new EncryptDES(key);
//                    decryptDES.decrypt(decryptPath,key);
                    int resDESFileOp =  DESFileOp(decryptPath,newPath,1,decryptDES);
                    if (resDESFileOp==0){
                        return 0;
                    }
                    break;
                case 1:
//                value = encryptAlgMultiple(content, key);
                    EncryptAES encryptAES = new EncryptAES(key);
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

    private int AESFileOp(String encryptPath, String newPath, int method, EncryptAES encryptAES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        InputStream is = new FileInputStream(encryptPath);
        OutputStream out = new FileOutputStream(newPath);
        String saveMD5;
        int r;
        if (method==0) {//加密
            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);
            //写入文件MD5信息
            byte[] enMD5 = encryptAES.encrypt(fileMd5.getBytes());
            out.write(enMD5);
            out.flush();

            byte[] buffer = new byte[1024];
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                byte[] res = encryptAES.encrypt(temp);
                out.write(res);
                out.flush();
            }
        }else {//解密
            byte[] buffer = new byte[1040];
            byte[] md5Buffer = new byte[48];
            is.read(md5Buffer);
            saveMD5 =  new String(encryptAES.decrypt(md5Buffer));
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);

                byte[] res =  encryptAES.decrypt(temp);
                out.write(res );
                out.flush();

            }
            print("解密操作完成");
            print("正在比对文件MD5...");
            print("文件保存MD5: "+ saveMD5);
            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(newPath);
            print("当前解密文件MD5: "+fileMd5);
            if (saveMD5.equals(fileMd5)){
                print("MD5比对成功!文件为原始文件");
            }else {
                print("MD5比对失败!文件被修改!!!");
            }
        }
//            byte[] buffer = new byte[1024];

        return 1;
    }

    private int DESFileOp(String encryptPath, String newPath, int method, EncryptDES encryptDES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
//        InputStream is = new FileInputStream(encryptPath);
//        OutputStream out = new FileOutputStream(newPath);
        String saveMD5;
        int r;
        if (method==0) {//加密
            try {
                print("正在使用DES加密...");
                int encryptIndex = encryptDES.encrypt(encryptPath,newPath);
//                print(encryptDES.getMD5());
                if (encryptIndex==1){
                    print("文件加密完成");
                }
            } catch (Exception e) {
                print("文件加密失败");
                e.printStackTrace();
            }
//            //计算文件MD5
//            String fileMd5 = MD5Util.md5HashCode(encryptPath);
//            //写入文件MD5信息
//            byte[] enMD5 = encryptDES.encrypt(fileMd5.getBytes());
//            out.write(enMD5);
//            out.flush();
//
//            byte[] buffer = new byte[1024];
//            while ((r = is.read(buffer)) > 0) {
//                byte[] temp = new byte[r];
//                System.arraycopy(buffer,0,temp,0,r);
//                byte[] res = encryptAES.encrypt(temp);
//                out.write(res);
//                out.flush();
//            }
        }else {//解密
            print("正在使用DES解密...");
            try {
                int encryptIndex = encryptDES.decrypt(encryptPath,newPath);
                if (encryptIndex==1){
                    print("解密操作完成");
                    print("正在比对文件MD5...");
                    saveMD5 = encryptDES.getSaveMD5();
                    print("文件保存MD5: "+ saveMD5);
                    //计算文件MD5
                    String fileMd5 = MD5Util.md5HashCode(newPath);
                    print("当前解密文件MD5: "+fileMd5);
                    if (saveMD5.equals(fileMd5)){
                        print("MD5比对成功!文件为原始文件");
                    }else {
                        print("MD5比对失败!文件被修改!!!");
                    }
                }
            } catch (Exception e) {
                return 0;
//                e.printStackTrace();
            }

//            byte[] buffer = new byte[1040];
//            byte[] md5Buffer = new byte[48];
//            is.read(md5Buffer);
//            saveMD5 =  new String(encryptAES.decrypt(md5Buffer));
//            while ((r = is.read(buffer)) > 0) {
//                byte[] temp = new byte[r];
//                System.arraycopy(buffer,0,temp,0,r);
//
//                byte[] res =  encryptAES.decrypt(temp);
//                out.write(res );
//                out.flush();
//
//            }


        }
//            byte[] buffer = new byte[1024];

        return 1;
    }


}
