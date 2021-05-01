package xens.Encrypt;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptDES {
    SecretKey secretKey;//建立key对象,需要定义密钥算法,编码,编码密钥的格式
    //创建输出域
    JTextArea consoleArea;
    int start,end;

    //    创建共有的EncryptDES类,可以被该类的和非该类的任何成员访问
    public EncryptDES(String strKey,JTextArea consoleArea) throws BadLocationException {//传入输入密钥,和输出域
         getKey(strKey);
        this.consoleArea = consoleArea;
    }


    //校验标志
    private int checkFLag = 0;
    //n的次数
    private int nNum = -1;
    //创建打印类
    void print(int num) throws BadLocationException {
        if (consoleArea != null) {
            if (checkFLag==0){
                consoleArea.replaceRange(num+"%",start, consoleArea.getLineEndOffset(2));
            }else{
                if (num==0){
                    consoleArea.append("\r\n正在校验文件 >>> 0%");
                }else {
                    consoleArea.replaceRange(num+"%",consoleArea.getLineStartOffset(3)+10, consoleArea.getLineEndOffset(3));
                }

            }
        }
    }

    /**
     * 根据参数生成KEY
     * 使用try/catch捕获错误
     */
    public void getKey(String strKey) {
        try {
            //将用户输入密钥转为byte数组
            byte[] decodedKey = strKey.getBytes("UTF-8");
            //创建 SecureRandom 实例,并指定随机数生成器算法
            SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
            //将用户的输入密钥数组作为随机数种子
            secureRandom.setSeed(decodedKey);
            //生成随机数,并存入byteKey中
            byte[] byteKey = new byte[8];
            secureRandom.nextBytes(byteKey);
            //将byte转为SecretKey
            secretKey =  new SecretKeySpec(byteKey, 0, byteKey.length, "DES");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
    }


    //从文件中获取到的MD5
    String saveMD5="";
    //从文件中获取到的保存文件名
    String saveFileName = "";
    //最后输出文件名
    String outFilePath = "";
    /**
     *
     * @param EncryptPath 加密地址
     * @param outPath 加密输出地址
     * @param fileName 文件名
     * @return 返回1为加密成功
     * @throws Exception
     */
    public int encrypt(String EncryptPath,String outPath,String fileName) throws Exception {
        start = consoleArea.getLineStartOffset(2)+14;
        end = consoleArea.getLineEndOffset(2);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        InputStream is = new FileInputStream(EncryptPath);
        OutputStream out = new FileOutputStream(outPath);
        CipherInputStream cis = new CipherInputStream(is, cipher);


        File f = new File(EncryptPath);
        //获取文件长度
        double fileLen = f.length();
        //分组加密次数
        int allTime = (int) Math.ceil((fileLen/512));
        int nTime = allTime/100;
        //计算文件MD5
        String fileMd5 = MD5Util.md5HashCode(EncryptPath);
        byte[] byteMD5 = cipher.doFinal(fileMd5.getBytes());
        //写入文件MD5信息
        out.write(byteMD5);

        //加密文件名
        byte[] encryptFileName =  cipher.doFinal(fileName.getBytes());
        //获取加密后的文件名长度
        int fileNameLen = encryptFileName.length;
        //存入名字长度
        byte[] lenByte = cipher.doFinal((String.valueOf(fileNameLen)).getBytes());
        out.write(lenByte);
        //写入文件信息
        out.write(encryptFileName);

        byte[] buffer = new byte[1024];
        int r;
        int n=0;
        nNum = -1;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
            n++;
            if (nTime!=0&&n%nTime==0){
                if (nNum!= n/nTime){
                    print(n/nTime);
                }
            }
            if (nTime==0||r<512){
                print(100);
            }
        }
        cis.close();
        is.close();
        out.close();
        //对文件进行校验
        if (checkFile(outPath)==0){
            return 0;
        }
        //文件校验成功,删除原始文件
        File encryptPath = new File(EncryptPath);
        encryptPath.delete();
        return 1;
    }
    public int decrypt(String decryptPath,String newPath){
        try {
            start = consoleArea.getLineStartOffset(2)+13;
            end = consoleArea.getLineEndOffset(2);
            Cipher cipher = Cipher.getInstance("DES");//返回实现指定转换的密码对象
            //init使用密钥初始化此密码
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            InputStream is = new FileInputStream(decryptPath);//流读取文件

            File f = new File(decryptPath);
            //获取文件长度
            double fileLen = f.length();
            //分组加密次数
            int allTime = (int) (fileLen/1024);
            int nTime = (int) Math.ceil(allTime/100);

            //获取文件保存的MD5信息
            byte[] byteMD5 = new byte[40];
            is.read(byteMD5);
            saveMD5 = new String(cipher.doFinal(byteMD5));

            //获取文件名长度
            byte[] readByteFileNameLen = new byte[8];
            is.read(readByteFileNameLen);
            String strFileNameLen =  new String(cipher.doFinal(readByteFileNameLen));
            int fileNameLen = Integer.parseInt(strFileNameLen);
            //获取文件保存的文件名
            byte[] readByteFileName = new byte[fileNameLen];
            is.read(readByteFileName);

            saveFileName = new String(cipher.doFinal(readByteFileName));
           //直接解密
            if (checkFLag==0){
                newPath += "\\"+saveFileName;

            }
            outFilePath = newPath;
            OutputStream out = new FileOutputStream(newPath);//流输出文件
            CipherOutputStream cos = new CipherOutputStream(out, cipher);//由一个OutputStream和一个密码组成,对数据进行加密后写入
            byte[] buffer = new byte[1024];
            int r;
            int n=-1;
            nNum = 0;
             while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
                n++;
                if (n==0&&nTime==0){
                    print(0);
                }
                if (nTime!=0&&n%nTime==0){
                    print(nNum);
                    if (nNum<100){
                        nNum++;
                    }
                }
                if (nTime==0||r<512){
                    print(100);
                }
            }



            cos.close();
            out.close();
            is.close();
        } catch (Exception e) {
            consoleArea.append("文件解密失败");
           return 0;
        }

        return 1;
    }
    public int checkFile(String outPath){
        String tempPath = outPath+"_temp";
        checkFLag = 1;
        try {
            if(decrypt(outPath,tempPath)==1){
                //计算文件MD5
                String fileMd5 = MD5Util.md5HashCode(tempPath);
                if (saveMD5.equals(fileMd5)){
                    consoleArea.append("\r\n文件校验成功");
                    File tempFile = new File(tempPath);
                    tempFile.delete();
                }else {
                    consoleArea.append("文件校验失败");
                    return 0;
                }
            }

        } catch (Exception e) {
            return 0;
        }

        return 1;
    }
    public String getSaveMD5(){
        return saveMD5;
    }
    public String getOutFile(){
        return outFilePath;
    }
}
