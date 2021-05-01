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
                    AESFileOp(EncryptPath,newPath,fileName,0,encryptAES);
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
                    EncryptAES encryptAES = new EncryptAES(key);
                    int resAESFileOp = AESFileOp(decryptPath,newPath,fileName,1,encryptAES);
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
                else {//发生错误
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }



        }

        return 1;
    }
    private int AES_CHECK_FLAG=0;
    private String saveMD5="";
    private String saveFileNameLen="";
    private String saveFileName="";

    private int AESFileOp(String encryptPath, String newPath,String fileName, int method, EncryptAES encryptAES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadLocationException {
        InputStream is = new FileInputStream(encryptPath);
        int r=0;
        double progress = 0,sum=0;
        File f = new File(encryptPath);
        //获取文件长度
        double fileLen = f.length();

        if (method==0) {//加密
            print("正在使用AES加密 >>> ");
            OutputStream out = new FileOutputStream(newPath);

            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);
            //写入文件MD5信息
            byte[] enMD5 = encryptAES.encrypt(fileMd5.getBytes());
            out.write(enMD5);
            out.flush();

            //加密文件名
            byte[] encryptFileName =  encryptAES.encrypt(fileName.getBytes());
            //获取加密后的文件名长度
            int fileNameLen = encryptFileName.length;
            //存入名字长度
            byte[] lenByte = encryptAES.encrypt((String.valueOf(fileNameLen)).getBytes());
            out.write(lenByte);
            //写入文件信息
            out.write(encryptFileName);

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
//                    print("\r");
//                    print("文件加密完成");
                }
            }
        }else {//解密
            print("正在使用AES解密 >>>  ");
            AES_DECRYPT(1,f,is,encryptAES,r,sum,progress,fileLen,newPath);
        }
        return 1;
    }
    //AES解密
    private int AES_DECRYPT(int flag, File f, InputStream is, EncryptAES encryptAES, int r, double progress, double sum, double fileLen, String newPath){
        byte[] buffer = new byte[1040];
        byte[] md5Buffer = new byte[48];
        byte[] fileNameLen = new byte[16];
        try {
            //读取保存的md5信息
            is.read(md5Buffer);
            saveMD5 =  new String(encryptAES.decrypt(md5Buffer));

            //读取保存的文件名长度信息
            is.read(fileNameLen);
            saveFileNameLen = new String(encryptAES.decrypt(fileNameLen));
            int intFileNameLen = Integer.parseInt(saveFileNameLen);

            //读取保存的文件名
            byte[] fileName = new byte[intFileNameLen];
            is.read(fileName);
            saveFileName = new String(encryptAES.decrypt(fileName));


            OutputStream out = null;
            //校验
            if (flag==0){
                
            }else {//直接解密
               out= new FileOutputStream(newPath+"\\"+saveFileName);

            }
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
                    if (flag==1){
                        print("\r");
                        print("文件解密完成");
                    }

                }

            }
            is.close();
            out.close();
            if (flag==1){
                print("解密操作完成");
                print("正在比对文件MD5...");
                print("文件保存MD5: "+ saveMD5);
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                print("当前解密文件MD5: "+fileMd5);
                if (saveMD5.equals(fileMd5)){
                    print("MD5比对成功!文件为原始文件");
                    //删除原始文件
                    System.gc();
                    f.delete();
                }else {
                    print("MD5比对失败!文件被修改!!!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



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
