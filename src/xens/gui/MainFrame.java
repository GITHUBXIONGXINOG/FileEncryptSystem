package xens.gui;

import xens.gui.thread.DecryptThread;
import xens.gui.thread.EncryptThread;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.URL;


public class MainFrame extends JFrame implements ActionListener{
    //使用serialVersionUID序列化实体类来判断版本一致,默认是1L
    private static final long serialVersionUID = 1L;
    //创建加密按钮
    JButton btnEncrypt = new JButton("加密");
    //创建解密按钮
    JButton btnDecrypt = new JButton("解密");
    //创建加密文件选择按钮
    JButton btnEncryptFileChooser = new JButton("...");
    //创建解密文件选择按钮
    JButton btnDecryptFileChooser = new JButton("...");
    //创建加密解密方法数组
    String []methodList = {"DES","AES","SM4","ECC"};
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
    JTextArea consoleArea = new JTextArea(10,76);
    //创建加密密钥输入标识
    Boolean inputEnPassword = false;
    //创建解密密钥输入标识
    Boolean inputDePassword = false;
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
        this.setSize(860,530);
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

        //创建title占位组件标签0
        JLabel label0 = new JLabel();
        //设置标签大小
        label0.setPreferredSize(new Dimension(220, 90));
        label0.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加标签到窗口
        this.add(label0);

        //获取title图片资源
        URL titleURL = this.getClass().getResource("/resources/title.png");
        ImageIcon titleImage = new ImageIcon(titleURL);
        //创建指定图片和水平方向对齐的标签
        JLabel titleLabel = new JLabel(titleImage,SwingConstants.LEFT);
        //设置标签大小
        titleLabel.setPreferredSize(new Dimension(380,90));
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加title标签到窗口
        this.add(titleLabel);

        //创建title占位组件标签1
        JLabel label1 = new JLabel();
        //设置标签大小
        label1.setPreferredSize(new Dimension(220, 90));
        label1.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加标签到窗口
        this.add(label1);

        //创建加密行占位组件标签2
        JLabel label2 = new JLabel();
        //设置标签大小
        label2.setPreferredSize(new Dimension(20, 50));
        label2.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加标签到窗口
        this.add(label2);

        //加密文件标签
        JLabel encryptLabel = new JLabel("加密文件: ");
        encryptLabel.setFont(new Font("宋体",Font.BOLD,16));
        //设置标签大小
        encryptLabel.setPreferredSize(new Dimension(90, 30));
        encryptLabel.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加文件加密标签到窗口
        this.add(encryptLabel);

        //加密文件地址文本框
        encryptFilePath.setPreferredSize(new Dimension(220, 30));
        encryptFilePath.addFocusListener(new JTextFieldHintListener(encryptFilePath,"拖拽文件或点击...按钮选择文件"));
        //添加拖拽
        encryptFilePath.setTransferHandler(new TransferHandler(){
            private static final long serialVersionUID = 1L;
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    //获取拖拽的文件信息
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    //转为String,并取代[]
                    String filepath = o.toString();
                    if (filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length() - 1);
                    }
                    System.out.println(filepath);
                    String[] pathList = filepath.replace(", ",",").split(",");
                    //添加到文本框
                    encryptFilePath.setText(filepath);
                    int len = pathList.length;
                    if (len==1){
                        //设置输出文本域
                        consoleArea.setText("当前选择文件大小约");
                        File f = new File(filepath);
                        consoleArea.append(byte2String(f.length()));
                    }else if (len > 1){
                        //设置输出文本域
                        consoleArea.setText("当前选择文件数量为:"+len+"\r\n");
                    }


                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            //拖拽文件触发,判断能否被导入
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for (int i = 0; i < flavors.length; i++) {
                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
        encryptFilePath.setBorder(BorderFactory.createLineBorder(Color.red));
//
        //添加文件地址文本框到窗口
        this.add(encryptFilePath);

        //给加密文件选择按钮绑定监听
        btnEncryptFileChooser.addActionListener(this);
        //设置按钮大小
        btnEncryptFileChooser.setPreferredSize(new Dimension(20,30));
        btnEncryptFileChooser.setBorder(BorderFactory.createLineBorder(Color.black));
        //添加加密文件选择按钮到窗口
        this.add(btnEncryptFileChooser);

        //创建加密方法标签
        JLabel method0 = new JLabel("加密方法:");
        method0.setFont(new Font("宋体",Font.BOLD,16));
        //设置标签大小
        method0.setPreferredSize(new Dimension(90,30));
        method0.setBorder(BorderFactory.createLineBorder(Color.red));
        //将方法标签添加到窗口
        this.add(method0);
        encryptMethod.setBorder(BorderFactory.createLineBorder(Color.red));
        //将加密选择按钮添加到窗口
        this.add(encryptMethod);

        //创建密钥标签
        JLabel encryptLabel1 = new JLabel("加密密钥: ");
        encryptLabel1.setFont(new Font("宋体",Font.BOLD,16));
        //设置密钥标签大小
        encryptLabel1.setPreferredSize(new Dimension(90,30));
        encryptLabel1.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加密钥标签到窗口
        this.add(encryptLabel1);

        //创建加密密钥输入标签
        encryptKey.setPreferredSize(new Dimension(130,30));
        //设置加密密钥默认文字内容
//        encryptKey.setText("1234567887654344");
        encryptKey.addFocusListener(new JTextFieldHintListener(encryptKey,"请输入加密密钥"));

        encryptKey.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加加密密钥输入标签到窗口
        this.add(encryptKey);

        //为加密按钮添加点击事件
        btnEncrypt.addActionListener(this);
        //设置加密按钮的大小
        btnEncrypt.setPreferredSize(new Dimension(60,30));
        btnEncrypt.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加加密按钮到窗口
        this.add(btnEncrypt);


        //创建加密行占位组件标签3
        JLabel label3 = new JLabel();
        //设置标签大小
        label3.setPreferredSize(new Dimension(20, 50));
        label3.setBorder(BorderFactory.createLineBorder(Color.black));
        //添加标签到窗口
        this.add(label3);

        //创建解密行占位组件标签4
        JLabel label4 = new JLabel();
        //设置标签大小
        label4.setPreferredSize(new Dimension(20, 80));
        label4.setBorder(BorderFactory.createLineBorder(Color.black));
        //添加标签到窗口
        this.add(label4);

        //创建解密文件标签
        JLabel password = new JLabel("解密文件:");
        password.setFont(new Font("宋体",Font.BOLD,16));
        //设置标签大小
        password.setPreferredSize(new Dimension(90,30));
        password.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加标签到窗口
        this.add(password);

        //设置解密文本输入框大小
        decryptFilePath.setPreferredSize(new Dimension(220,30));
        decryptFilePath.addFocusListener(new JTextFieldHintListener(decryptFilePath,"拖拽文件或点击...按钮选择文件"));
        //添加拖拽
        decryptFilePath.setTransferHandler(new TransferHandler(){
            private static final long serialVersionUID = 1L;
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    //获取拖拽的文件信息
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    //转为String,并取代[]
                    String filepath = o.toString();
                    if (filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length() - 1);
                    }
                    //添加到文本框
                    decryptFilePath.setText(filepath);
                    String[] pathList = filepath.replace(", ",",").split(",");
                    int len = pathList.length;
                    if (len==1){
                        //设置输出文本域
                        consoleArea.setText("当前选择文件大小约");
                        File f = new File(filepath);
                        consoleArea.append(byte2String(f.length()));
                    }else if (len > 1){
                        //设置输出文本域
                        consoleArea.setText("当前选择文件数量为:"+len+"\r\n");
                    }
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            //拖拽文件触发,判断能否被导入
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for (int i = 0; i < flavors.length; i++) {
                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
        //添加到窗口
        this.add(decryptFilePath);

        //解密文件选择按钮绑定监听
        btnDecryptFileChooser.addActionListener(this);
        //解密文件选择按钮设置大小
        btnDecryptFileChooser.setPreferredSize(new Dimension(20,30));
        btnDecryptFileChooser.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加到窗口
        this.add(btnDecryptFileChooser);

        //创建解密方法标签
        JLabel method1 = new JLabel("解密方法: ");
        method1.setFont(new Font("宋体",Font.BOLD,16));
        //设置标签大小
        method1.setPreferredSize(new Dimension(90,30));
        method1.setBorder(BorderFactory.createLineBorder(Color.red));
        //将方法标签添加到窗口
        this.add(method1);
        decryptMethod.setBorder(BorderFactory.createLineBorder(Color.red));
        //将解密选择按钮添加到窗口
        this.add(decryptMethod);

        //创建解密密钥标签
        JLabel decryptLabel1 = new JLabel("解密密钥: ");
        decryptLabel1.setFont(new Font("宋体",Font.BOLD,16));
        //设置密钥标签大小
        decryptLabel1.setPreferredSize(new Dimension(90,30));
        decryptLabel1.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加密钥标签到窗口
        this.add(decryptLabel1);

        //设置界面密钥文本输入框大小
        decryptKey.setPreferredSize(new Dimension(130, 30));
        //设置解密密钥,默认内容
//        decryptKey.setText("1234567887654344");
        decryptKey.addFocusListener(new JTextFieldHintListener(decryptKey,"请输入解密密钥"));

        decryptKey.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加到窗口
        this.add(decryptKey);

        //给解密按钮添加监听
        btnDecrypt.addActionListener(this);
        //设置解锁按钮大小
        btnDecrypt.setPreferredSize(new Dimension(60,30));
        btnDecrypt.setBorder(BorderFactory.createLineBorder(Color.red));

        //添加解锁按钮到窗口
        this.add(btnDecrypt);

        //创建解密组件占位标签4
        JLabel label5 = new JLabel();
        //设置标签大小
        label5.setPreferredSize(new Dimension(20, 50));
        label5.setBorder(BorderFactory.createLineBorder(Color.black));
        //添加标签到窗口
        this.add(label5);

        //创建解密组件占位标签4
        JLabel label6 = new JLabel();
        //设置标签大小
        label6.setPreferredSize(new Dimension(60, 50));
        label6.setBorder(BorderFactory.createLineBorder(Color.black));
        //添加标签到窗口
        this.add(label6);

        //设置输出文本框不可编辑
        consoleArea.setEditable(false);
        Font font = new Font("宋体",Font.PLAIN,18);
        consoleArea.setFont(font);
        consoleArea.setLineWrap(true);
        //滚动面板
        JScrollPane consolePanel = new JScrollPane(consoleArea);
        //分别设置水平和垂直滚动条出现方式
        consolePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        consolePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //添加到窗口
        this.add(consolePanel);

        //创建输出域占位组件标签5
        JLabel label7 = new JLabel();
        //设置标签大小
        label7.setPreferredSize(new Dimension(60, 90));
        label7.setBorder(BorderFactory.createLineBorder(Color.red));
        //添加标签到窗口
        this.add(label7);

        // 设置窗口可见，此句一定要在窗口属性设置好了之后才能添加，不然无法正常显示
        this.setVisible(true);

    }
    //设置监听事件触发的方法
    public void actionPerformed(ActionEvent e){
        //当前触发的事件发起者是文件加密选择
        if (e.getSource() == btnEncryptFileChooser){
            //创建文件选择器,不指定文件目录,默认为文档目录
            JFileChooser fileChooser = new JFileChooser();
            //设置JFileChooser窗口标题栏中的字符串。
            fileChooser.setDialogTitle("请选择要加密的文件...");
            //设置FileChooseUI中ApproveButton中使用的文本。
            fileChooser.setApproveButtonText("确定");
            fileChooser.showOpenDialog(this);// 显示打开的文件对话框
            //f就是选中的文件
            File f = fileChooser.getSelectedFile();// 使用文件类获取选择器选择的文件
            if (f != null) {
                String s = f.getAbsolutePath();// 返回路径名
                //将文本加密标签,将当前的路径设置为文字内容
                encryptFilePath.setText(s);
                //设置输出文本域
                consoleArea.setText("当前选择文件大小约");
                consoleArea.append(byte2String(f.length()));

            }
        }else if (e.getSource() == btnDecryptFileChooser){ //当前触发的事件发起者是文件解密选择
            //创建文件选择器,不指定文件目录,默认为文档目录
            JFileChooser fileChooser = new JFileChooser();
            //设置JFileChooser窗口标题栏中的字符串。
            fileChooser.setDialogTitle("请选择要解密的文件...");
            //设置FileChooseUI中ApproveButton中使用的文本。
            fileChooser.setApproveButtonText("确定");
            fileChooser.showOpenDialog(this);// 显示打开的文件对话框
            //f就是选中的文件
            File f = fileChooser.getSelectedFile();// 使用文件类获取选择器选择的文件
            if (f != null) {
                String s = f.getAbsolutePath();// 返回路径名
                //将文本加密标签,将当前的路径设置为文字内容
                decryptFilePath.setText(s);
                //设置输出文本域
                consoleArea.setText("当前选择文件大小约");
                consoleArea.append(byte2String(f.length()));

            }
        }else if (e.getSource() == btnEncrypt){//当前触发的事件发起者是文件加密按钮
            //输入了密码
//            if (inputEnPassword){
                //将方法选择框选中的加密方法转换为String类型
                String.valueOf(encryptMethod.getSelectedItem());
                try {
                    int start = consoleArea.getLineStartOffset(1);
                    int end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-1);
                    consoleArea.replaceRange("",start,end);
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                //没有输入密钥,清空
                if (!inputEnPassword){
                    encryptKey.setText("");
                }
                //创建新的加密线程,并传入对应的信息,使用start()调用新线程
                new EncryptThread(this,btnEncrypt,btnDecrypt,encryptFilePath,encryptKey,encryptMethod,consoleArea).start();
//            }
//            else {//未输入密码
//                if (encryptMethod.getSelectedIndex()==3){
//
//                }else {
////                    String msg = "";
////                    if (encryptFilePath.getText().equals("")){
////                        msg = "请选择或输入地址!";
////                    }
////                    JOptionPane.showMessageDialog(this, "请输入密钥!", "错误",JOptionPane.WARNING_MESSAGE);
//
//                }
//            }

        }else if(e.getSource() == btnDecrypt){//当前触发的事件发起者是文件解密按钮
            if (inputDePassword){
                //将方法选择框选中的加密方法转换为String类型
                String.valueOf(encryptMethod.getSelectedItem());
                try {
                    int start = consoleArea.getLineStartOffset(1);
                    int end = consoleArea.getLineEndOffset(consoleArea.getLineCount()-1);
                    consoleArea.replaceRange("",start,end);
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                //创建新的加密线程,并传入对应的信息,使用start()调用新线程
                new DecryptThread(this,btnEncrypt,btnDecrypt,decryptFilePath,decryptKey,decryptMethod,consoleArea).start();

            }
        }
    }

    //文本框提示
    public class JTextFieldHintListener implements FocusListener {
        private String hintText;
        private JTextField textField;
        public JTextFieldHintListener(JTextField jTextField,String hintText) {
            this.textField = jTextField;
            this.hintText = hintText;
            jTextField.setText(hintText);  //默认直接显示
            jTextField.setForeground(Color.GRAY);
        }

        public void focusGained(FocusEvent e) {
            //获取焦点时，清空提示内容
            String temp = textField.getText();
            if(temp.equals(hintText)) {
                textField.setText("");
                textField.setForeground(Color.BLACK);
            }

        }

        public void focusLost(FocusEvent e) {
            //失去焦点时，没有输入内容，显示提示内容
            String temp = textField.getText();
            if(temp.equals("")) {
                textField.setForeground(Color.GRAY);
                textField.setText(hintText);
                if (hintText.equals("请输入加密密钥")){
                    inputEnPassword=false;
                }else {
                    inputDePassword=false;
                }
            }else {
                if (hintText.equals("请输入加密密钥")){
                    inputEnPassword=true;
                }else {
                    inputDePassword=true;
                }
            }

        }

    }

    //存储单位转换
    private String byte2String(Long num){
        if(num < 1024 ){
            return num + "B\r\n";
        } else if (num >= 1024 && num < Math.pow(1024,2) ){
            return new java.text.DecimalFormat("#.00").format(num/1024) + "KB\r\n";
        } else if (num >= Math.pow(1024,2) && num < Math.pow(1024,3)){
            return new java.text.DecimalFormat("#.00").format(num/Math.pow(1024,2)) + "MB\r\n";
        } else if (num >= Math.pow(1024,3) ){
            return new java.text.DecimalFormat("#.00").format(num/Math.pow(1024,3)) + "GB\r\n";
        }
        return "";
    }
}
