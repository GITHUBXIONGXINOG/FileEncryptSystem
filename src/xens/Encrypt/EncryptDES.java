package xens.Encrypt;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Key;
import java.security.SecureRandom;

public class EncryptDES {
    SecretKey secretKey;//建立key对象,需要定义密钥算法,编码,编码密钥的格式
    //创建输出域
    JTextPane consoleArea;
    int start,end;
    // 设置 最小进度值、最大进度值、当前进度值
    private static final int MIN_PROGRESS = 0;
    private static final int MAX_PROGRESS = 100;

    private static int currentProgress = MIN_PROGRESS;

    //    创建共有的EncryptDES类,可以被该类的和非该类的任何成员访问
    public EncryptDES(String strKey,JTextPane consoleArea) throws BadLocationException {//传入输入密钥,和输出域
         getKey(strKey);
        this.consoleArea = consoleArea;


    }



    //创建打印类
    void print(int num) throws BadLocationException {
        if (consoleArea != null) {
            SimpleAttributeSet attrset = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrset,16);
//            consoleArea.requestFocus();
//            consoleArea.select(0,consoleArea.getDocument().getLength());
//            consoleArea.setText("");
            //插入内容
            Document docs = consoleArea.getDocument();//获得文本对象
//
//            String paneText = "";
//               paneText = docs.getText(0, docs.getLength());
            docs.insertString(docs.getLength(), "⬛", attrset);//对文本进行追加
//            consoleArea.setText(paneText+num);



//                consoleArea.setCaretPosition(0);
//                consoleArea.select(0,10);
//                paneText = consoleArea.getSelectedText();
//                consoleArea.requestFocus();
//            consoleArea.replaceSelection("String.valueOf(num)");

//                docs.insertString(docs.getLength(), String.valueOf(num), attrset);//对文本进行追加
//                docs.insertString(docs.getLength(), "\r\n", attrset);//对文本进行追加

//             consoleArea.replaceRange(String.valueOf(num),start, consoleArea.getLineEndOffset(2));
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
    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "12345678";
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    //直接读取文件的MD5

    //从文件中获取到的MD5
    String saveMD5="";


    public int encrypt(String EncryptPath,String outPath) throws Exception {
//        start = consoleArea.getLineStartOffset(2)+14;
//        end = consoleArea.getLineEndOffset(2);

//
//        JProgressBar progressBar = new JProgressBar();
//        // 设置进度的 最小值 和 最大值
//        progressBar.setMinimum(MIN_PROGRESS);
//        progressBar.setMaximum(MAX_PROGRESS);
//
//        // 设置当前进度值
//        progressBar.setValue(currentProgress);
//
//        // 绘制百分比文本（进度条中间显示的百分数）
//        progressBar.setStringPainted(true);
//        consoleArea.insertComponent(progressBar);

        Cipher cipher = Cipher.getInstance("DES");
        // cipher.init(Cipher.ENCRYPT_MODE, getKey());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        InputStream is = new FileInputStream(EncryptPath);
        OutputStream out = new FileOutputStream(outPath);
        CipherInputStream cis = new CipherInputStream(is, cipher);


        File f = new File(EncryptPath);
        //获取文件长度
        double fileLen = f.length();
        //分组加密次数
        int allTime = (int) Math.ceil((fileLen/512));
        //触发更改的次数
        int rTime = allTime/10;

        //计算文件MD5
        String fileMd5 = MD5Util.md5HashCode(EncryptPath);
        byte[] byteMD5 = cipher.doFinal(fileMd5.getBytes());
        //写入文件MD5信息
        out.write(byteMD5);

        byte[] buffer = new byte[1024];
        int r;
        int n=0,progress = 0;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
            n++;
                if(rTime!=0&&n%rTime==0){
//                    progressBar.setValue(n/rTime);
                    print(n/rTime);
                }
                if (r<512||rTime==0){
                    print(100);
//                    progressBar.setValue(100);
                }



//            progress =((int)((n++/allTime)*1000))/10.0;
//            progressBar.setValue(10);
//            if ((int)progress == progress){
////                print((int)progress);
//
//
//            }
        }
        cis.close();
        is.close();
        out.close();
        return 1;
    }
    public int decrypt(String decryptPath,String newPath) throws Exception {
//        start = consoleArea.getLineStartOffset(2)+13;
//        end = consoleArea.getLineEndOffset(2);
        Cipher cipher = Cipher.getInstance("DES");//返回实现指定转换的密码对象
        //init使用密钥初始化此密码
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        InputStream is = new FileInputStream(decryptPath);//流读取文件
        OutputStream out = new FileOutputStream(newPath);//流输出文件
        CipherOutputStream cos = new CipherOutputStream(out, cipher);//由一个OutputStream和一个密码组成,对数据进行加密后写入
        File f = new File(decryptPath);
        //获取文件长度
        double fileLen = f.length();

        //获取文件保存的MD5信息
        byte[] byteMD5 = new byte[40];
        is.read(byteMD5);
        byte[] byre5 = cipher.doFinal(byteMD5);
        saveMD5 = new String(cipher.doFinal(byteMD5));
        byte[] buffer = new byte[1024];
        int r;
        double progress,sum=0;
        while ((r = is.read(buffer)) >= 0) {
            cos.write(buffer, 0, r);
            if (r==1024){
                sum += r;
                progress = sum / fileLen;
//                print((int) (progress*100));
            }else {
//                print(100);
            }
        }
        cos.close();
        out.close();
        is.close();
        return 1;
    }

    public String getSaveMD5(){
        return saveMD5;
    }
}
