package xens.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class MainFrame extends JFrame implements ActionListener{
    //使用serialVersionUID序列化实体类来判断版本一致,默认是1L
    private static final long serialVersionUID = 1L;
    //创建加密按钮
    JButton btnEncrypt = new JButton("加密");
    //创建解密按钮
    JButton btnDecrypt = new JButton("解密");
    //创建加密文件选择按钮
    JButton btnDecryptFileChooser = new JButton("...");
    //创建解密文件选择按钮
    JButton btnEncryptFileChooser = new JButton("...");
    //创建加密解密方法数组
    String []methodList = {"DES","AES","SM4"};
    //创建加密方法下拉选择框
    JComboBox encryptMethod = new JComboBox<String>(methodList);
    //创建解密方法下拉选择框
    JComboBox decryptMethod = new JComboBox<String>(methodList);
    //创建加密选中文件地址文本框
    JTextField encryptFilePath = new JTextField();
    //创建解密选中文件地址文本框
    JTextField decryptFilePath = new JTextField();
    //创建加密密钥输入文本框
    JTextField encryptKey = new JTextField();
    //创建解密密钥输入文本框
    JTextField decryptKey = new JTextField();
    //创建文本输出域
    JTextArea consoleArea = new JTextArea(20,50);

    //创建主函数
    public static void main(String[] args){
        //创建窗口类实例
        MainFrame mainFrame = new MainFrame();
        //窗口实例化
        mainFrame.Init();
    }
    //窗口初始化
    public void Init(){
        //设置窗口名称
        this.setTitle("文件加密系统");
        //设置窗口大小
        this.setSize(900,500);
        //设置窗口不能被用户调整大小
        this.setResizable(false);
        // 设置窗口相对于指定组件的位置,设置为null则窗口位于屏幕中央,
        this.setLocationRelativeTo(null);
        // 用户单击窗口的关闭按钮时程序执行的操作,参数为3时，表示关闭窗口则程序退出
        this.setDefaultCloseOperation(3);
        //使用this.getClass().getResource(path)获取文件资源
        URL icon_URL = this.getClass().getResource("/resources/icon.png");
        //根据Image绘制ICON
        ImageIcon icon = new ImageIcon(icon_URL);
        //设置JFrame窗口标题图标
        this.setIconImage(icon.getImage());

        //使用流式布局
        FlowLayout left1 = new FlowLayout(FlowLayout.LEFT);
        //窗口设置为流式左对齐
        this.setLayout(left1);

        //创建组件标签
        JLabel label0 = new JLabel();
        //设置标签大小
        label0.setPreferredSize(new Dimension(130, 90));
        //添加标签到窗口
        this.add(label0);

        //获取title图片资源
        URL titleURL = this.getClass().getResource("/resources/title.png");
        ImageIcon titleImage = new ImageIcon(titleURL);
        //创建指定图片和水平方向对齐的标签
        JLabel titleLabel = new JLabel(titleImage,SwingConstants.LEFT);
        //设置标签大小
        titleLabel.setPreferredSize(new Dimension(630,90));
        //添加title标签到窗口
        this.add(titleLabel);

        //创建组件标签
        JLabel label1 = new JLabel();
        //设置标签大小
        label1.setPreferredSize(new Dimension(130, 90));
        //添加标签到窗口
        this.add(label1);

        //加密文件标签
        JLabel encryptLabel = new JLabel("加密文件: ");
        //设置标签大小
        encryptLabel.setPreferredSize(new Dimension(70, 30));
        //添加文件加密标签到窗口
        this.add(encryptLabel);

        //加密文件地址文本框
        encryptFilePath.setPreferredSize(new Dimension(220, 30));
        //添加文件地址文本框到窗口
        this.add(encryptFilePath);

        //给加密文件选择按钮绑定监听
        btnDecryptFileChooser.addActionListener(this);
        //设置按钮大小
        btnDecryptFileChooser.setPreferredSize(new Dimension(20,30));
        //添加加密文件选择按钮到窗口
        this.add(btnDecryptFileChooser);

        //创建加密方法标签
        JLabel method0 = new JLabel("加密方法");
        //设置标签大小
        method0.setPreferredSize(new Dimension(30,30));
        //将方法标签添加到窗口
        this.add(method0);
        //将加密选择按钮添加到窗口
        this.add(encryptMethod);

        //创建密钥标签
        JLabel encryptLabel1 = new JLabel("密钥: ");
        //设置密钥标签大小
        encryptLabel1.setPreferredSize(new Dimension(50,30));
        //添加密钥标签到窗口
        this.add(encryptLabel1);

        //创建加密密钥输入标签
        encryptKey.setPreferredSize(new Dimension(130,30));
        //设置加密密钥默认文字内容
        encryptKey.setText("3.141592653");
        //添加加密密钥输入标签到窗口
        this.add(encryptKey);

        //为加密按钮添加点击事件
        btnEncrypt.addActionListener(this);
        //设置加密按钮的大小
        btnEncrypt.setPreferredSize(new Dimension(60,30));
        //添加加密按钮到窗口
        this.add(btnEncrypt);


        //创建组件标签2
        JLabel label2 = new JLabel();
        //设置标签大小
        label2.setPreferredSize(new Dimension(60, 30));
        //添加标签到窗口
        this.add(label2);

        //创建组件标签3
        JLabel label3 = new JLabel();
        //设置标签大小
        label3.setPreferredSize(new Dimension(90, 30));
        //添加标签到窗口
        this.add(label3);

        //创建解密文件标签
        JLabel password = new JLabel("解密文件:");
        //设置标签大小
        password.setPreferredSize(new Dimension(70,30));
        //添加标签到窗口
        this.add(password);

        //设置解密文本输入框大小
        decryptFilePath.setPreferredSize(new Dimension(220,30));
        //添加到窗口
        this.add(decryptFilePath);

        //解密文件选择按钮绑定监听
        btnEncryptFileChooser.addActionListener(this);
        //解密文件选择按钮设置大小
        btnEncryptFileChooser.setPreferredSize(new Dimension(20,30));
        //添加到窗口
        this.add(btnEncryptFileChooser);

        //创建解密方法标签
        JLabel method1 = new JLabel("解密方法: ");
        //设置标签大小
        method1.setPreferredSize(new Dimension(30,30));
        //将方法标签添加到窗口
        this.add(method1);
        //将解密选择按钮添加到窗口
        this.add(decryptMethod);

        //创建解密密钥标签
        JLabel decryptLabel1 = new JLabel("解密密钥: ");
        //设置密钥标签大小
        decryptLabel1.setPreferredSize(new Dimension(50,30));
        //添加密钥标签到窗口
        this.add(decryptLabel1);

        //设置界面密钥文本输入框大小
        decryptKey.setPreferredSize(new Dimension(130, 30));
        //设置解密密钥,默认内容
        decryptKey.setText("3.141592653");
        //添加到窗口
        this.add(decryptKey);

        //给解密按钮添加监听
        btnDecrypt.addActionListener(this);
        //设置解锁按钮大小
        btnDecrypt.setPreferredSize(new Dimension(60,30));
        //添加解锁按钮到窗口
        this.add(btnDecrypt);

        //创建组件标签4
        JLabel label4 = new JLabel();
        //设置标签大小
        label4.setPreferredSize(new Dimension(90, 30));
        //添加标签到窗口
        this.add(label4);

        //创建组件标签5
        JLabel label5 = new JLabel();
        //设置标签大小
        label5.setPreferredSize(new Dimension(90, 30));
        //添加标签到窗口
        this.add(label5);
        //设置输出文本框不可编辑
        consoleArea.setEditable(false);
        //滚动面板
        JScrollPane consolePanel = new JScrollPane(consoleArea);
        //分别设置水平和垂直滚动条出现方式
        consolePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        consolePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //添加到窗口
        this.add(consolePanel);

        // 设置窗口可见，此句一定要在窗口属性设置好了之后才能添加，不然无法正常显示
        this.setVisible(true);

    }
    //设置监听事件触发的方法
    public void actionPerformed(ActionEvent e){
        //当前触发的事件发起者是文件加密选择
        if (e.getSource() == btnDecryptFileChooser){

        }else if (e.getSource() == btnEncrypt){//当前触发的事件发起者是文件加密按钮

        }else if(e.getSource() == btnDecrypt){//当前触发的事件发起者是文件解密按钮

        }
    }
}
