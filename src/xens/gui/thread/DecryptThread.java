package xens.gui.thread;

import xens.file.FileEncrypter;
import xens.file.FileList;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static xens.file.FileEncrypter.TimeFormat;

public class DecryptThread extends Thread {
    JFrame jf;//JFrame框架
    JButton btnEncrypt;//加密按钮
    JButton btnDecrypt;//解密按钮
    JTextField decryptFilePath;//解密地址
    String decryptKey;//解密密钥
    JComboBox<String> decryptMethod;//解密方法
    JTextArea consoleArea;//输出域
    String priKeyPath;//密钥文件地址

    public DecryptThread(JFrame jf, JButton btnEncrypt, JButton btnDecrypt, JTextField decryptFilePath, JTextField decryptKey, JComboBox<String> decryptMethod, JTextArea consoleArea) {
        this.jf = jf;
        this.btnEncrypt = btnEncrypt;
        this.btnDecrypt = btnDecrypt;
        this.decryptFilePath = decryptFilePath;
        this.consoleArea = consoleArea;
        this.decryptKey = decryptKey.getText();
        this.decryptMethod = decryptMethod;
        this.setName("Encrypt_Thread");

    }

    //创建线程类
    public void run() {
        readPriKey();
        //获取文件加密路径
        String EncryptPath = decryptFilePath.getText();
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
        int listLen = fileNames.size();

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
                JOptionPane.showMessageDialog(jf, "文件路径错误!", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //密钥为空时报错
            if (decryptKey.equals("")) {
                consoleArea.append("密钥错误:" + path);
                JOptionPane.showMessageDialog(jf, "请检查密钥!", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //创建文件加密实例
            FileEncrypter fileEncrypter = new FileEncrypter(consoleArea);
            //设置加密中禁用按钮
            btnEncrypt.setEnabled(false);
            btnDecrypt.setEnabled(false);
            int resIndex = 0;
            try {
                resIndex = fileEncrypter.decrypt(file, path, decryptMethod.getSelectedIndex(), decryptKey);
                if (resIndex == 0) {
                    failNum++;
                } else if (resIndex == 1) {
                    successNum++;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(jf, "文件加密出现错误...", "加密错误", JOptionPane.WARNING_MESSAGE);
            }
            //只有一个
            if (listLen == 1) {
                if (resIndex == 1) {//解密成功
                    consoleArea.append("文件解密完成!");
                    if(priKeyPath!=null){
                        File file1 = new File(priKeyPath);
                        if (file1.exists() && file1.isFile()) {
                            file1.delete();
                        }
                    }

                    //显示面板
                    JOptionPane.showMessageDialog(jf, "文件解密成功!", "解密成功", JOptionPane.PLAIN_MESSAGE);
                } else {
                    consoleArea.append("--------------------------------------------------------------------------");
                    consoleArea.append("文件解密失败!!!");
                    JOptionPane.showMessageDialog(jf, "文件解密出现错误,请检查加密方式和密码...", "解密错误", JOptionPane.WARNING_MESSAGE);
                }
            } else {//有多个文件
                if (resIndex == 1) {//解密成功
                    consoleArea.append("文件解密完成!");
                    if (index == listLen - 1) {
                        JOptionPane.showMessageDialog(jf, "文件解密成功!", "解密成功", JOptionPane.PLAIN_MESSAGE);
                    }
                } else {
                    consoleArea.append("--------------------------------------------------------------------------");
                    consoleArea.append("文件解密失败!!!");
                    if (index == listLen - 1) {
                        JOptionPane.showMessageDialog(jf, "文件解密出现错误,请检查加密方式和密码...", "解密错误", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
        Date end = new Date();
        consoleArea.append("\r\n总共加密" + listLen + "个文件,其中成功个数为:" + successNum + ",失败个数为:" + failNum + "。");

        consoleArea.append("\r\n解密总" + TimeFormat(end.getTime() - start.getTime()));

        //设置按钮可用
        btnEncrypt.setEnabled(true);
        btnDecrypt.setEnabled(true);
    }

    private void readPriKey() {
        String path = decryptKey;
        //创建文件类
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            try {
                FileInputStream is = new FileInputStream(path);
                byte[] priKey = new byte[1024];
                is.read(priKey);
                this.decryptKey = new String(priKey);
                priKeyPath = path;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
