package xens.file;

import xens.Encrypt.*;
import xens.Encrypt.EncryptAES;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
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
    //创建打印进度类
    void printProgress(int num) throws BadLocationException {
        if (consoleArea != null) {
            consoleArea.replaceRange(num+"%",consoleArea.getLineStartOffset(2)+14, consoleArea.getLineEndOffset(2));
        }
    }

    public int encrypt(File file,JTextField encryptFilePath, int method, String key){
        String EncryptPath = encryptFilePath.getText();

        Date start = new Date();
        print("正在加密: "+ file.getName());
        try{
            //加密文件名字拼接
            //拆分地址
            //存储地址到newPath
            String newPath = EncryptPath;

            String[] fileNameArray = EncryptPath.split("\\\\");
            String fileName = fileNameArray[fileNameArray.length-1];


            String methodName="";
            switch (method){
                case 0: methodName="DES";break;
                case 1: methodName="AES";break;
                case 2: methodName="SM4";break;
            }
            //拼接文件名

            newPath += "_ENCRYPTED_"+methodName;
            File outFile = new File(newPath);
            //如果当前加密文件存在,删除加密文件
            if (outFile.exists()){
                outFile.delete();
            }
            switch(method){
                case 0:
                    EncryptDES encryptDES = new EncryptDES(key,consoleArea);
                    DESFileOp(EncryptPath,newPath,fileName,0,encryptDES);

                    break;
                case 1:
                    EncryptAES encryptAES = new EncryptAES(key);
                    AESFileOp(EncryptPath,newPath,0,encryptAES);
                    break;
                case 2:
                    EncrySM4 encrySM4 = new EncrySM4(key);
                    SM4FileOp(EncryptPath,newPath,0,encrySM4);
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
        //获取父级地址
        String newPath = file.getParent();

        String[] fileNameArray = decryptPath.split("\\\\");
        String fileName = fileNameArray[fileNameArray.length-1];

        File outFile = new File(newPath);

        try{

             switch(method){
                case 0:
                    EncryptDES decryptDES = new EncryptDES(key,consoleArea);
//                    decryptDES.decrypt(decryptPath,key);
                    int resDESFileOp =  DESFileOp(decryptPath,newPath,fileName,1,decryptDES);
                    if (resDESFileOp==0){
                        System.gc();
                        outFile.delete();
                        return 0;
                    }
                    break;
                case 1:
//                value = encryptAlgMultiple(content, key);
                    EncryptAES encryptAES = new EncryptAES(key);
//                    encryptAES.decrypt(decryptPath,newPath);
                    int resAESFileOp = AESFileOp(decryptPath,newPath,1,encryptAES);
                    if (resAESFileOp==0){
                        System.gc();
                        outFile.delete();
                        return 0;
                    }

                    break;
                case 2:
                    EncrySM4 encrySM4 = new EncrySM4(key);
                    SM4FileOp(decryptPath,newPath,1,encrySM4);
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
            System.gc();
            outFile.delete();
            return 0;//发生错误

        }
    }
    private int DESFileOp(String encryptPath, String newPath, String fileName,int method, EncryptDES encryptDES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {

        String saveMD5;
        if (method==0) {//加密
            try {
                print("正在使用DES加密 >>> ");
                int encryptIndex = encryptDES.encrypt(encryptPath,newPath,fileName);
                if (encryptIndex==1){
                    print("\r");
                    print("文件加密完成");
                }
            } catch (Exception e) {
                print("文件加密失败");
                e.printStackTrace();
            }

        }else {//解密
            print("正在使用DES解密 >>> ");
            try {
                int encryptIndex = encryptDES.decrypt(encryptPath,newPath);
                if (encryptIndex==1){
                    print("\r");
                    print("解密操作完成");
                    print("正在比对文件MD5...");
                    saveMD5 = encryptDES.getSaveMD5();
                    print("文件保存MD5: "+ saveMD5);
                    String outFile = encryptDES.getOutFile();
                    //计算文件MD5
                    String fileMd5 = MD5Util.md5HashCode(outFile);
                    print("当前解密文件MD5: "+fileMd5);
                    if (saveMD5.equals(fileMd5)){
                        print("MD5比对成功!文件为原始文件");
                        //删除原始加密文件
                        File file = new File(encryptPath);
                        file.delete();
                    }else {
                        print("MD5比对失败!文件被修改!!!");
                        //删除生成的被修改文件
                        File file = new File(outFile);
                        file.delete();
                    }
                }
            } catch (Exception e) {
                return 0;
            }



        }

        return 1;
    }

    private int AESFileOp(String encryptPath, String newPath, int method, EncryptAES encryptAES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadLocationException {
        InputStream is = new FileInputStream(encryptPath);
        OutputStream out = new FileOutputStream(newPath);
        String saveMD5;
        int r;
        double progress,sum=0;
        File f = new File(encryptPath);
        //获取文件长度
        double fileLen = f.length();

        if (method==0) {//加密
            print("正在使用AES加密 >>> ");
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
                if (r==1024){
                    sum += r;
                    progress = sum / fileLen;
                    printProgress((int) (progress*100));
                }else {
                    printProgress(100);
                    print("\r");
                    print("文件加密完成");
                }

            }
        }else {//解密
            print("正在使用AES解密 >>>  ");
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
                if (r==1040){
                    sum += r;
                    progress = sum / fileLen;
                    printProgress((int) (progress*100));
                }else {
                    printProgress(100);
                    print("\r");
                    print("文件解密完成");
                }

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


    private int SM4FileOp(String encryptPath, String newPath, int method, EncrySM4 encrySM4) throws IOException, BadLocationException {
        InputStream is = new FileInputStream(encryptPath);
        OutputStream out = new FileOutputStream(newPath);
        String saveMD5;
        int r;
        double progress,sum=0;
        File f = new File(encryptPath);
        //获取文件长度
        double fileLen = f.length();
        //加密
        if (method==0){
            print("正在使用SM4加密 >>> ");
            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);
            //写入文件MD5信息
            byte[] enMD5 = encrySM4.encrypt(fileMd5.getBytes());
            out.write(enMD5);
            out.flush();

            byte[] buffer = new byte[1024];
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                byte[] resByte = encrySM4.encrypt(temp);
                out.write(resByte);
                out.flush();
                if (r==1024){
                    sum += r;
                    progress = sum / fileLen;
                    printProgress((int) (progress*100));
                }else {
                    printProgress(100);
                    print("\r");
                    print("文件加密完成");
                }
            }
        }else {//解密
            print("正在使用SM4解密 >>> ");
            byte[] buffer = new byte[1040];
            byte[] md5Buffer = new byte[48];
            is.read(md5Buffer);
            saveMD5 =  new String(encrySM4.decrypt(md5Buffer));
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);

                byte[] resByte =  encrySM4.decrypt(temp);
                out.write(resByte );
                out.flush();
                if (r==1040){
                    sum += r;
                    progress = sum / fileLen;
                    printProgress((int) (progress*100));
                }else {
                    printProgress(100);
                    print("\r");
                    print("文件解密完成");
                }

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
        return 1;
    }
}
