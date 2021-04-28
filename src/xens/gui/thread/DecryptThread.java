package xens.gui.thread;

import xens.file.FileEncrypter;

import javax.swing.*;
import java.io.File;

public class DecryptThread extends Thread {
    JFrame jf;//JFrame框架
    JButton btnEncrypt;//加密按钮
    JButton btnDecrypt;//解密按钮
    JTextField decryptFilePath;//解密地址
    JTextField decryptKey;//解密密钥
    JComboBox<String> decryptMethod;//解密方法
    JTextArea consoleArea;//输出域

    public DecryptThread(JFrame jf,JButton btnEncrypt,JButton btnDecrypt, JTextField decryptFilePath,JTextField decryptKey,JComboBox<String> decryptMethod,JTextArea consoleArea){
        this.jf = jf;
        this.btnEncrypt = btnEncrypt;
        this.btnDecrypt = btnDecrypt;
        this.decryptFilePath = decryptFilePath;
        this.consoleArea = consoleArea;
        this.decryptKey = decryptKey;
        this.decryptMethod = decryptMethod;
        this.setName("Encrypt_Thread");
    }

    //创建线程类
    public void run(){
        //获取文件加密路径
        String EncryptPath = decryptFilePath.getText();
        //创建文件类
        File file = new File(EncryptPath);
        if(!file.exists() || !file.isFile()) {
            JOptionPane.showMessageDialog(jf, "文件路径错误!", "错误",JOptionPane.WARNING_MESSAGE);
            return;
        }
        //创建文件加密实例
        FileEncrypter fileEncrypter = new FileEncrypter(consoleArea);
        //设置加密中禁用按钮
        btnEncrypt.setEnabled(false);
        btnDecrypt.setEnabled(false);
        int resIndex = 0;
        try{
//            fileEncrypter.decrypt(file, EncryptPath,decryptMethod.getSelectedIndex(),decryptKey.getText());
            resIndex = fileEncrypter.decrypt(file,decryptFilePath, decryptMethod.getSelectedIndex(),decryptKey.getText());
            System.out.println(resIndex);
        }catch (Exception e){
            JOptionPane.showMessageDialog(jf,"文件加密出现错误...","加密错误",JOptionPane.WARNING_MESSAGE);
        }
        if (resIndex == 1){//解密成功
            //显示面板
            JOptionPane.showMessageDialog(jf,"文件解密成功!","解密成功",JOptionPane.PLAIN_MESSAGE);
        }else {
            JOptionPane.showMessageDialog(jf,"文件解密出现错误,请检查密码...","解密错误",JOptionPane.WARNING_MESSAGE);
        }

        //设置按钮可用
        btnEncrypt.setEnabled(true);
        btnDecrypt.setEnabled(true);
    }
}
