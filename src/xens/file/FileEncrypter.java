package xens.file;

import xens.Encrypt.*;
import xens.Encrypt.EncryptAES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class FileEncrypter {

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
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());

    }
    //输出域行开始
    private int start;
    //输出域行结束
    private int end;
    //创建打印进度类
    void printProgress(int num) throws BadLocationException {
        if (consoleArea != null) {
            String strNum = num+"%";
            while (strNum.length()<4){
                // **0%   *10% 100%
                strNum = " "+strNum;
            }
            if (AES_CHECK_FLAG==0){
//                consoleArea.replaceRange(num+"%",consoleArea.getLineStartOffset(2)+14, consoleArea.getLineEndOffset(2));
                consoleArea.replaceRange(strNum,start, end);

            }else {//校验输出
//                consoleArea.replaceRange(num+"%",consoleArea.getLineStartOffset(3)+11, consoleArea.getLineEndOffset(3));
                consoleArea.replaceRange(strNum,start, end);

            }
        }
    }

    private int index;
    private String parentPath="";
    /**
     *
     * @param index 第几个文件
     * @param file 当前要加密的文件File
     * @param encryptFilePath 当前要加密的文件地址
     * @param method 加密方法
     * @param key 加密密钥
     * @return
     */
    public int encrypt(int index, File file, String encryptFilePath, int method, String key){
        this.index = index;
        Date start = new Date();
        print("正在加密: "+ file.getName());
        try{
            //加密文件名字拼接
            //拆分地址
            //存储地址到newPath
            String newPath = encryptFilePath;
            //获取父级路径
            parentPath = file.getParent();
            String[] fileNameArray = encryptFilePath.split("\\\\");
            String fileName = fileNameArray[fileNameArray.length-1];


            String methodName="";
            switch (method){
                case 0: methodName="DES";break;
                case 1: methodName="AES";break;
                case 2: methodName="SM4";break;
                case 3: methodName="ECC";break;

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
                    EncryptDES encryptDES = new EncryptDES(key,consoleArea,index);
                    DESFileOp(encryptFilePath,newPath,fileName,0,encryptDES);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    break;
                case 1:
                    EncryptAES encryptAES = new EncryptAES(key);
                    AESFileOp(encryptFilePath,newPath,fileName,0,encryptAES);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    break;
                case 2:
                    EncrySM4 encrySM4 = new EncrySM4(key);
                    SM4FileOp(encryptFilePath,newPath,fileName,0,encrySM4);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    break;
                case 3:
                    EncryptECC encryptECC = new EncryptECC(0,key,parentPath,fileName);
                    ECCFileOp(encryptFilePath,newPath,fileName,0,encryptECC);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    break;
            }


            Date end = new Date();
            print(TimeFormat(end.getTime() - start.getTime()));
            print("--------------------------------------------------------------------------");
            return 1;

        }catch (Exception e){
            return 0;//发生错误
        }
    }

    public int decrypt(File file,String decryptFilePath, int method, String key){
//        String decryptPath = decryptFilePath.getText();
        Date start = new Date();
        print("正在解密: "+ file.getName());
        //获取父级地址
        String newPath = file.getParent();

        String[] fileNameArray = decryptFilePath.split("\\\\");
        String fileName = fileNameArray[fileNameArray.length-1];

        File outFile = new File(newPath);

        try{

             switch(method){
                case 0:
                    EncryptDES decryptDES = new EncryptDES(key,consoleArea,index);
//                    decryptDES.decrypt(decryptPath,key);
                    int resDESFileOp =  DESFileOp(decryptFilePath,newPath,fileName,1,decryptDES);
                    if (resDESFileOp==0){
                        System.gc();
                        outFile.delete();
                        return 0;
                    }
                    break;
                case 1:
                    EncryptAES encryptAES = new EncryptAES(key);
                    int resAESFileOp = AESFileOp(decryptFilePath,newPath,fileName,1,encryptAES);
                    if (resAESFileOp==0){
                        System.gc();
                        outFile.delete();
                        print("文件解密失败×");
                        return 0;
                    }

                    break;
                case 2:
                    EncrySM4 encrySM4 = new EncrySM4(key);
                    int resSM4FileOp = SM4FileOp(decryptFilePath,newPath,fileName,1,encrySM4);
                    if (resSM4FileOp==0){
                        System.gc();
                        outFile.delete();
                        print("文件解密失败×");
                        return 0;
                    }
                    break;
                 case 3:
                     EncryptECC encryptECC = new EncryptECC(1,key,parentPath,fileName);
                     ECCFileOp(decryptFilePath,newPath,fileName,1,encryptECC);
                     consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                     break;
            }
            Date end = new Date();
            print(TimeFormat(end.getTime() - start.getTime()));
            print("--------------------------------------------------------------------------");

            return 1;

        }catch (Exception e){
            System.gc();
            outFile.delete();
            return 0;//发生错误

        }
    }

    /**
     * 时间格式化
     * @param num 传入long格式的时间
     * @return 返回格式化后的时间
     */
    public  static String TimeFormat(long num){
            if (num<=1000){
                return ("用时" + (num) + "ms");
            }else if(num>1000&&num<=60000){
                return ("用时" + (num) / 1000 + "s");
            }else if (num>60000&&num<=360000){
                return ("用时" + (num) / 60000 + "m");
            }else if (num>360000){
                return ("用时" + (num) / 360000 + "h");
            }
        return "";
    }
    //DES文件操作
    private int DESFileOp(String encryptPath, String newPath, String fileName,int method, EncryptDES encryptDES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {

        String saveMD5;
        if (method==0) {//加密
            try {
                print("正在使用DES加密 >>>   \r\n");
                int encryptIndex = encryptDES.encrypt(encryptPath,newPath,fileName);
                if (encryptIndex==1){
                    print("\r");
                    print("文件加密完成√");
                }
            } catch (Exception e) {
                print("文件加密失败×");
                System.out.println(e);
                return 0;
            }

        }else {//解密
            print("正在使用DES解密 >>> ");
            try {
                int encryptIndex = encryptDES.decrypt(encryptPath,newPath);
                if (encryptIndex==1){
                    print("解密操作完成√");
                    print("正在比对文件MD5...");
                    saveMD5 = encryptDES.getSaveMD5();
                    print("文件保存MD5: "+ saveMD5);
                    String outFile = encryptDES.getOutFile();
                    //计算文件MD5
                    String fileMd5 = MD5Util.md5HashCode(outFile);
                    print("当前解密文件MD5: "+fileMd5);
                    if (saveMD5.equals(fileMd5)){
                        print("MD5比对成功!文件为原始文件√");
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

    //AES文件操作
    private int AESFileOp(String encryptPath, String newPath,String fileName, int method, EncryptAES encryptAES) throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadLocationException {
        if (method==0) {//加密
            print("正在使用AES加密 >>>    ");
            end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
            start = end - 4;
            AES_ENCRYPT(encryptPath,newPath,fileName,encryptAES);
            AES_CHECK_FLAG = 1;
            //文件校验成功
            if (AES_DECRYPT(0,newPath,newPath,encryptAES)==1){
                File file = new File(encryptPath);
                file.delete();
            }
        }else {//解密
            print("正在使用AES解密 >>>    ");
            end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
            start = end - 4;
            return AES_DECRYPT(1,encryptPath,newPath,encryptAES);

        }
        return 1;
    }
    //n的次数
    private int nNum = -1;
    //AES加密
    private int AES_ENCRYPT(String encryptPath, String newPath, String fileName, EncryptAES encryptAES){
        try {
            int r=0;
            InputStream is = new FileInputStream(encryptPath);
            OutputStream out = new FileOutputStream(newPath);
            double progress = 0,sum=0;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;
            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);

            //MD5信息加密信息
            byte[] enMD5 = encryptAES.encrypt(fileMd5.getBytes());
            //MD5的长度
            String len = String.valueOf(enMD5.length);
            //16位
            byte[] byteMD5Len = encryptAES.encrypt(len.getBytes());
            //写入MD5长度
            out.write(byteMD5Len);
            out.flush();
            //写入MD5信息
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
            nNum = -1;
            int n=0;
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                //使用aes加密
                byte[] res = encryptAES.encrypt(temp);
                out.write(res);
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;


            }
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }
    //AES解密
    private int AES_DECRYPT(int flag, String encryptPath, String newPath, EncryptAES encryptAES){
        byte[] buffer = new byte[1040];
        byte[] fileNameLen = new byte[16];
        int r=0;
        try {
            InputStream is = new FileInputStream(encryptPath);
            OutputStream out = null;
            double progress = 0,sum=0;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组解密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;

            //读取保存的md5长度
            byte[] md5LenBuffer = new byte[16];
            is.read(md5LenBuffer);

            int md5len = Integer.parseInt(new String(encryptAES.decrypt(md5LenBuffer)));
            byte[] md5Buffer = new byte[md5len];

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



            //校验
            if (flag==0){
                print("正在校验文件 >>>  0%");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
                out = new FileOutputStream(newPath+"_TEMP");
            }else {//直接解密
               out= new FileOutputStream(newPath+"\\"+saveFileName);

            }
            nNum = -1;
            int n=0;
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);

                byte[] res =  encryptAES.decrypt(temp);
                out.write(res );
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;
            }
            is.close();
            out.close();
            printProgress(100);
            if (flag==1){
                print("解密操作完成√");
                print("正在比对文件MD5...");
                print("文件保存MD5: "+ saveMD5);
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                print("当前解密文件MD5: "+fileMd5);
                if (saveMD5.equals(fileMd5)){
                    print("MD5比对成功!文件为原始文件√");
                    //删除原始文件
                    System.gc();
                    f.delete();
                }else {
                    print("MD5比对失败!文件被修改!!!");
                }
            }else {//校验文件
                //计算文件MD5
                String tempFileName = newPath+"_TEMP";
                File tempFile = new File(tempFileName);
                String fileMd5 = MD5Util.md5HashCode(tempFileName);
                if (saveMD5.equals(fileMd5)){
                    print("文件校验成功√");

                    //删除原始文件
                    System.gc();
                    tempFile.delete();
                    return 1;
                }else {
                    print("\r\n文件校验失败!请重试");
                    return 0;
                }
            }

        } catch (Exception e) {
           return 0;
        }



        return 1;
    }

    private int SM4_FLAG = 0;
    //SM4文件操作
    private int SM4FileOp(String encryptPath, String newPath, String fileName,int method, EncrySM4 encrySM4)  {
        try {
            //加密
            if (method==0){
                print("正在使用SM4加密 >>>     ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
                //文件校验
                if (SM4_ENCRYPT(encryptPath,newPath,fileName,encrySM4)==1){
                    print("文件加密成功√");
                    File file = new File(encryptPath);
                    while (file.exists()){
                        System.gc();
                        file.delete();
                    }

                }
            }else {//解密
                print("正在使用SM4解密 >>>    ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
                return SM4_DECRYPT(encryptPath,newPath,encrySM4);
            }
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }
    //SM4加密
    private int SM4_ENCRYPT(String encryptPath, String newPath, String fileName, EncrySM4 encrySM4){
        try {
            FileInputStream is = new FileInputStream(encryptPath);
            OutputStream out = new FileOutputStream(newPath);
            String saveMD5;
            int r;
            double progress,sum=0;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;

            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);
            //写入文件MD5信息
            byte[] enMD5 = encrySM4.encrypt(fileMd5.getBytes());
            //MD5的长度
            String len = String.valueOf(enMD5.length);
            //16位
            byte[] byteMD5Len = encrySM4.encrypt(len.getBytes());
            //写入MD5长度
            out.write(byteMD5Len);
            out.flush();
            out.write(enMD5);
            out.flush();

            //加密文件名
            byte[] encryptFileName =  encrySM4.encrypt(fileName.getBytes());
            //获取加密后的文件名长度
            int fileNameLen = encryptFileName.length;
            //存入名字长度
            byte[] lenByte = encrySM4.encrypt((String.valueOf(fileNameLen)).getBytes());
            out.write(lenByte);
            //写入文件信息
            out.write(encryptFileName);



            byte[] buffer = new byte[1024];
            int n = 0;
            while ((r = is.read(buffer)) > 0) {


                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                //使用aes加密
                byte[] res = encrySM4.encrypt(temp);
                out.write(res);
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;

            }
            SM4_FLAG = 1;
            SM4_DECRYPT(newPath,f.getParent(),encrySM4);
        }catch (Exception e){
            return 0;
        }
        return 1;
    }
    //SM4解密
    private int SM4_DECRYPT(String encryptPath, String newPath, EncrySM4 encrySM4){
        try{
            FileInputStream is = new FileInputStream(encryptPath);
            OutputStream out = null;
            String saveMD5;
            int r;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;
            //读取保存的md5长度
            byte[] md5LenBuffer = new byte[16];
            is.read(md5LenBuffer);

            int md5len = Integer.parseInt(new String(encrySM4.decrypt(md5LenBuffer)));
            byte[] md5Buffer = new byte[md5len];
            byte[] buffer = new byte[1040];


            is.read(md5Buffer);
            saveMD5 =  new String(encrySM4.decrypt(md5Buffer));
            byte[] fileNameLen = new byte[16];
            //读取保存的文件名长度信息
            is.read(fileNameLen);
            saveFileNameLen = new String(encrySM4.decrypt(fileNameLen));
            int intFileNameLen = Integer.parseInt(saveFileNameLen);

            //读取保存的文件名
            byte[] fileName = new byte[intFileNameLen];
            is.read(fileName);
            saveFileName = new String(encrySM4.decrypt(fileName));

            if (SM4_FLAG==0){//直接解压
                out = new FileOutputStream(newPath+"\\"+saveFileName);
            }else {//校验
                out = new FileOutputStream(encryptPath+"_TEMP");
                print("正在校验文件 >>>    ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
            }
            int n = 0;
            nNum = -1;
            while ((r = is.read(buffer)) > 0) {

                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                //使用aes解密
                byte[] res = encrySM4.decrypt(temp);
                out.write(res);
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;

            }
            is.close();
            out.close();
            printProgress(100);

            if (SM4_FLAG==0){
                print("解密操作完成√");
                print("正在比对文件MD5...");
                print("文件保存MD5: "+ saveMD5);
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                print("当前解密文件MD5: "+fileMd5);
                if (saveMD5.equals(fileMd5)){
                    print("MD5比对成功!文件为原始文件√");
                    f.delete();
                }else {
                    print("MD5比对失败!文件被修改!!!");
                }
            }else {//校验
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                if (saveMD5.equals(fileMd5)){
                    print("文件校验成功√");
                    File file = new File(encryptPath+"_TEMP");
                    //删除原始文件
                    System.gc();
                    file.delete();
                    return 1;
                }else {
//                    print("MD5比对失败!文件被修改!!!");
                    print("\r\n文件校验失败!请重试");


                }
            }

        }catch (Exception e){
            return 0;
        }
        return 1;
    }
    private int ECC_FLAG = 0;
    //ECC文件操作
    private int ECCFileOp(String encryptPath, String newPath, String fileName, int method, EncryptECC encryptECC)  {
        try {
            //加密
            if (method==0){
                print("正在使用ECC加密 >>>     ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
                //文件校验
                if (ECC_ENCRYPT(encryptPath,newPath,fileName,encryptECC)==1){
                    print("文件加密成功√");
                    //保存私钥
                    encryptECC.savePriveKey();
                    File file = new File(encryptPath);
                    while (file.exists()){
                        System.gc();
                        file.delete();
                    }

                    return 1;
                }
            }else {//解密
                print("正在使用ECC解密 >>>    ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
                return ECC_DECRYPT(encryptPath,newPath,encryptECC);
            }
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }

    /**
     * ECC加密
     * @param encryptPath 加密文件地址
     * @param newPath   输出文件地址
     * @param fileName 文件名字
     * @param encryptECC ecc实例对象
     * @return
     */
    private int ECC_ENCRYPT(String encryptPath, String newPath, String fileName, EncryptECC encryptECC){
        try {
            FileInputStream is = new FileInputStream(encryptPath);
            OutputStream out = new FileOutputStream(newPath);
            String saveMD5;
            int r;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;

            //计算文件MD5
            String fileMd5 = MD5Util.md5HashCode(encryptPath);
            //获取文件MD5信息的加密
            byte[] enMD5 = encryptECC.encrypt(fileMd5.getBytes());
            //MD5加密的长度
            String len = String.valueOf(enMD5.length);
            //88位
            byte[] byteMD5Len = encryptECC.encrypt(len.getBytes());
            //写入MD5长度
            out.write(byteMD5Len);
            out.flush();
            //写入MD5信息
            out.write(enMD5);
            out.flush();

            //加密文件名
            byte[] encryptFileName =  encryptECC.encrypt(fileName.getBytes());
            //获取加密后的文件名长度
            int fileNameLen = encryptFileName.length;
            //存入名字长度88
            byte[] lenByte = encryptECC.encrypt((String.valueOf(fileNameLen)).getBytes());
            out.write(lenByte);
            //写入文件信息
            out.write(encryptFileName);



            byte[] buffer = new byte[1024];
            int n = 0;
            while ((r = is.read(buffer)) > 0) {


                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                //使用aes加密
                byte[] res = encryptECC.encrypt(temp);
                out.write(res);
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;

            }
            ECC_FLAG = 1;
            printProgress(100);
            ECC_DECRYPT(newPath,f.getParent(),encryptECC);
        }catch (Exception e){
            System.out.println(e);
            return 0;
        }
        return 1;
    }

    //ECC解密
    private int ECC_DECRYPT(String encryptPath, String newPath, EncryptECC encryptECC){
        try{
            FileInputStream is = new FileInputStream(encryptPath);
            OutputStream out = null;
            String saveMD5;
            int r;
            File f = new File(encryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) Math.ceil((fileLen/1024));
            int nTime = allTime/100;
            //读取保存的md5长度
            byte[] md5LenBuffer = new byte[88];
            is.read(md5LenBuffer);

            int md5len = Integer.parseInt(new String(encryptECC.decrypt(md5LenBuffer)));
            byte[] md5Buffer = new byte[md5len];
            byte[] buffer = new byte[1109];


            is.read(md5Buffer);
            saveMD5 =  new String(encryptECC.decrypt(md5Buffer));
            byte[] fileNameLen = new byte[88];
            //读取保存的文件名长度信息
            is.read(fileNameLen);
            saveFileNameLen = new String(encryptECC.decrypt(fileNameLen));
            int intFileNameLen = Integer.parseInt(saveFileNameLen);

            //读取保存的文件名
            byte[] fileName = new byte[intFileNameLen];
            is.read(fileName);
            saveFileName = new String(encryptECC.decrypt(fileName));

            if (ECC_FLAG==0){//直接解压
                out = new FileOutputStream(newPath+"\\"+saveFileName);
            }else {//校验
                out = new FileOutputStream(encryptPath+"_TEMP");
                print("正在校验文件 >>>    ");
                end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-2)-2;
                start = end - 4;
            }
            int n = 0;
            nNum = -1;
            while ((r = is.read(buffer)) > 0) {
                byte[] temp = new byte[r];
                System.arraycopy(buffer,0,temp,0,r);
                //使用aes解密
                byte[] res = encryptECC.decrypt(temp);
                out.write(res);
                out.flush();
                if (nTime!=0&&n%nTime==0){
                    if (nTime!=n/nTime){
                        nNum++;
                        if (nNum<=100){
                            printProgress(nNum);
                        }
                    }
                }
                if (nTime==0||r<512){
                    printProgress(100);
                }
                n++;
            }
            is.close();
            out.close();
            printProgress(100);
            if (ECC_FLAG==0){
                print("解密操作完成√");
                print("正在比对文件MD5...");
                print("文件保存MD5: "+ saveMD5);
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                print("当前解密文件MD5: "+fileMd5);
                if (saveMD5.equals(fileMd5)){
                    print("MD5比对成功!文件为原始文件√");
                    f.delete();
                }else {
                    print("MD5比对失败!文件被修改!!!");
                }
            }else {//校验
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(newPath+"\\"+saveFileName);
                if (saveMD5.equals(fileMd5)){
                    print("文件校验成功√");
                    File file = new File(encryptPath+"_TEMP");
                    //删除原始文件
                    System.gc();
                    file.delete();
                    return 1;
                }else {
                    print("\r\n文件校验失败!请重试");


                }
            }

        }catch (Exception e){
            System.out.println(e);
            return 0;
        }
        return 1;
    }
}

//}
