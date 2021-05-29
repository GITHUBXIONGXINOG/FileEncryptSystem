package xens.gui.thread;

import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Console;
import xens.file.FileEncrypter;
import xens.file.FileList;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static xens.file.FileEncrypter.TimeFormat;

public class WatchEncryptThread extends Thread {
    JFrame jf;//JFrame框架
    JButton btnEncrypt;//加密按钮
    JButton btnDecrypt;//解密按钮
    JTextField encryptFilePath;//加密地址
    JTextField encryptKey;//加密密钥
    JComboBox<String> encryptMethod;//加密方法
    JTextArea consoleArea;//输出域
    int resEncrypt = 0;//加密结果 0为出错,1为成功

    public WatchEncryptThread(JFrame jf, JButton btnEncrypt, JButton btnDecrypt, JTextField encryptFilePath, JTextField encryptKey, JComboBox<String> encryptMethod, JTextArea consoleArea) {
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
//        new EncryptThread(jf,btnEncrypt,btnDecrypt,encryptFilePath,encryptKey,encryptMethod,consoleArea).start();
//        WatchMonitor.createAll(f, new SimpleWatcher(){
//            @Override
//            public void onModify(WatchEvent<?> event, Path currentPath) {
//                Console.log("EVENT modify");
//                consoleArea.append(event.context().toString());
//                consoleArea.append(currentPath.toString());
//                consoleArea.append("文件修改!");
//                new EncryptThread(jf,btnEncrypt,btnDecrypt,encryptFilePath,encryptKey,encryptMethod,consoleArea).start();
//
//            }
//        }).start();

        //这里只监听文件或目录的修改事件
        WatchMonitor watchMonitor = WatchMonitor.create(f, WatchMonitor.ENTRY_MODIFY);
        watchMonitor.setWatcher(new Watcher(){
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();

                consoleArea.append("\r\n当前创建路径:"+currentPath.toString());
                consoleArea.append("\r\n当前创建文件:"+obj.toString());
//                new EncryptThread(jf,btnEncrypt,btnDecrypt,encryptFilePath,encryptKey,encryptMethod,consoleArea).start();

//                Console.log("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                consoleArea.append("\r\n当前修改路径:"+currentPath.toString());
                consoleArea.append("\r\n当前修改文件:"+obj.toString());
//                Console.log("修改：{}-> {}", currentPath, obj);
                new EncryptThread(jf,btnEncrypt,btnDecrypt,encryptFilePath,encryptKey,encryptMethod,consoleArea).start();

            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                consoleArea.append("\r\n当前删除路径:"+currentPath.toString());
                consoleArea.append("\r\n当前删除文件:"+obj.toString());
//                Console.log("删除：{}-> {}", currentPath, obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                consoleArea.append("\r\n当前重写路径:"+currentPath.toString());
                consoleArea.append("\r\n当前重写文件:"+obj.toString());
//                Console.log("Overflow：{}-> {}", currentPath, obj);
            }
        });

//设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        watchMonitor.setMaxDepth(3);
//启动监听
        watchMonitor.start();




        //设置按钮可用
        btnEncrypt.setEnabled(true);
        btnDecrypt.setEnabled(true);
    }
}
