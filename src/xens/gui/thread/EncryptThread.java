package xens.gui.thread;

import xens.file.FileEncrypter;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

public class EncryptThread extends Thread {
    JFrame jf;//JFrame框架
    JButton btnEncrypt;//加密按钮
    JButton btnDecrypt;//解密按钮
    JTextField encryptFilePath;//加密地址
    JTextField encryptKey;//加密密钥
    JComboBox<String> encryptMethod;//加密方法
    JTextArea consoleArea;//输出域
    public EncryptThread(JFrame jf, JButton btnEncrypt, JButton btnDecrypt, JTextField encryptFilePath, JTextField encryptKey, JComboBox<String> encryptMethod, JTextArea consoleArea){
        this.jf = jf;
        this.btnEncrypt = btnEncrypt;
        this.btnDecrypt = btnDecrypt;
        this.encryptFilePath = encryptFilePath;
        this.consoleArea = consoleArea;
        this.encryptKey = encryptKey;
        this.encryptMethod = encryptMethod;
        this.setName("Encrypt_Thread");
    }

    //创建线程类
    public void run(){
        //获取文件加密路径
        String EncryptPath = encryptFilePath.getText();
        //将文件路径转为数组数组
            String[] pathList = EncryptPath.split(",");
            //获取数组长度
            int listLen = pathList.length;
        for (int i = 0; i < listLen; i++) {
            final int index = i;
            final String path = pathList[index].trim();
//            consoleArea.append("总共需要加密"+listLen+"个文件\r\n");

            //创建文件类
            File file = new File(path);
            if(!file.exists() || !file.isFile()) {
                consoleArea.append("文件路径错误:"+path);
                JOptionPane.showMessageDialog(jf, "文件路径错误!", "错误",JOptionPane.WARNING_MESSAGE);
                return;
            }

            //创建文件加密实例
            FileEncrypter fileEncrypter = new FileEncrypter(consoleArea);
            //设置加密中禁用按钮
            btnEncrypt.setEnabled(false);
            btnDecrypt.setEnabled(false);

            try{
                fileEncrypter.encrypt(index,file,path, encryptMethod.getSelectedIndex(),encryptKey.getText());
            }catch (Exception e){
                JOptionPane.showMessageDialog(jf,"文件加密出现错误...","加密错误",JOptionPane.WARNING_MESSAGE);
            }
        }

            //显示面板
            JOptionPane.showMessageDialog(jf,"文件加密成功!","加密成功",JOptionPane.PLAIN_MESSAGE);
            //设置按钮可用
            btnEncrypt.setEnabled(true);
            btnDecrypt.setEnabled(true);



    }
}
