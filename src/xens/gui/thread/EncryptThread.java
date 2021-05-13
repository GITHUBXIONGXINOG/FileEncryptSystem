package xens.gui.thread;

import xens.file.FileEncrypter;
import xens.file.FileList;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static xens.file.FileEncrypter.TimeFormat;

public class EncryptThread extends Thread {
    JFrame jf;//JFrame框架
    JButton btnEncrypt;//加密按钮
    JButton btnDecrypt;//解密按钮
    JTextField encryptFilePath;//加密地址
    JTextField encryptKey;//加密密钥
    JComboBox<String> encryptMethod;//加密方法
    JTextArea consoleArea;//输出域
    int resEncrypt = 0;//加密结果 0为出错,1为成功

    public EncryptThread(JFrame jf, JButton btnEncrypt, JButton btnDecrypt, JTextField encryptFilePath, JTextField encryptKey, JComboBox<String> encryptMethod, JTextArea consoleArea) {
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
    public void run() {
        //获取文件加密路径
        String EncryptPath = encryptFilePath.getText();
        //利用当前路径创建文件对象
        File f = new File(EncryptPath);
        //创建文件名字集合
        List<String> fileNames = new ArrayList<String>();
        //判断是否存在目录
        if (f.isDirectory()){
            //对目录进行遍历,并存入具体文件名到集合中
            FileList.findFileList(new File(EncryptPath),fileNames);
        }else {
            //将文件路径转为数组数组
            fileNames = Arrays.asList(EncryptPath.split(","));
        }
        //获取数组长度
        int  listLen = fileNames.size();

        //成功个数
        int successNum = 0;
        //失败个数
        int failNum = 0;

        Date start = new Date();
        for (int i = 0; i < listLen; i++) {
            final int index = i;
            final String path = fileNames.get(index).trim();

            //创建文件类
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                consoleArea.append("文件路径错误:" + path);
                JOptionPane.showMessageDialog(jf, "文件路径错误!", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //不为ECC或密钥为空时报错
            if (encryptKey.getText().equals("") && encryptMethod.getSelectedIndex() != 3) {
                consoleArea.append("密钥错误:" + path);
                JOptionPane.showMessageDialog(jf, "请检查密钥!", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //创建文件加密实例
            FileEncrypter fileEncrypter = new FileEncrypter(consoleArea);
            //设置加密中禁用按钮
            btnEncrypt.setEnabled(false);
            btnDecrypt.setEnabled(false);
            try {
                resEncrypt = fileEncrypter.encrypt(index, file, path, encryptMethod.getSelectedIndex(), encryptKey.getText());
                if (resEncrypt == 0) {
                    failNum++;
                } else if (resEncrypt == 1) {
                    successNum++;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(jf, "文件加密出现错误...", "加密错误", JOptionPane.WARNING_MESSAGE);
            }
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        }
        if (resEncrypt == 0) {
            consoleArea.append("文件加密失败!");
            JOptionPane.showMessageDialog(jf, "文件加密出现错误...", "加密错误", JOptionPane.WARNING_MESSAGE);
        } else {
            consoleArea.append("文件加密完成!");
            //显示面板
            JOptionPane.showMessageDialog(jf, "文件加密成功!", "加密成功", JOptionPane.PLAIN_MESSAGE);
        }
        Date end = new Date();
        consoleArea.append("\r\n总共加密" + listLen + "个文件,其中成功个数为:" + successNum + ",失败个数为:" + failNum + "。");
        consoleArea.append("\r\n加密总" + TimeFormat(end.getTime() - start.getTime()));
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());

        //设置按钮可用
        btnEncrypt.setEnabled(true);
        btnDecrypt.setEnabled(true);
    }
}
