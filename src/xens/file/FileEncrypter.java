package xens.file;

import xens.Encrypt.Encrypter;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
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

    public int encrypt(File file, int method, String key){
        //获取加密文件父文件
        File pFolder = file.getParentFile();
        Date start = new Date();
        print("正在加密: "+ file.getName());
        try{
            //读取文件
            RandomAccessFile oRAF = new RandomAccessFile(file,"r");
            //文件名字拼接
            File distFile = new File(pFolder,file.getName() + ".encrypt" );
            //如果当前加密文件存在,删除加密文件
            if (distFile.exists()){
                distFile.delete();
            }
            //写加密文件
            RandomAccessFile wFile = new RandomAccessFile(distFile, "rw");
            //逐字节加密
            int content, cnt = 0, round = key.length();
            //读取一字节,并移动指针
            while ((content = oRAF.read())!= -1){
//                wFile.write();
               byte[] res = Encrypter.encrypt(content,method,key);
                wFile.write(res);
            }
            //关闭读写的文件
            wFile.close();
            oRAF.close();
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

    public int decrypt(File file, int method, String key){
        //获取解密文件父文件
        File pFolder = file.getParentFile();
        Date start = new Date();
        print("正在解密: "+ file.getName());
        try{
            //读取文件
            RandomAccessFile oRAF = new RandomAccessFile(file,"r");
            //文件名字拼接
            File distFile = new File(pFolder,file.getName() + ".encrypt" );
            //如果当前加密文件存在,删除加密文件
            if (distFile.exists()){
                distFile.delete();
            }
            //写加密文件
            RandomAccessFile wFile = new RandomAccessFile(distFile, "rw");
            //逐字节加密
            int content, cnt = 0, round = key.length();
            //读取一字节,并移动指针
            while ((content = oRAF.read())!= -1){
//                wFile.write();
                byte[] res = Encrypter.decrypt(content,method,key);
                wFile.write(res);
            }
            //关闭读写的文件
            wFile.close();
            oRAF.close();
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
}
