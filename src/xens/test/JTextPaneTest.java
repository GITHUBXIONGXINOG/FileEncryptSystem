package xens.test;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JTextPaneTest extends JFrame{
    static int k,k2 = 0;
    static JLabel jlText = new JLabel("需替换的文本:");
    static JLabel jlColor = new JLabel("         替换内容:");
    static JTextField jtfChange = new JTextField();
    static JTextField jtfText = new JTextField();
    static JButton searchUp = new JButton("向上找");
    static JButton searchDown = new JButton("向下找");
    static JButton changeTextSize = new JButton("替换");//改变文本按钮
    static JTextPane jTextPane = new JTextPane();
    static StyledDocument doc = jTextPane.getStyledDocument();

    public static void main(String[] args){
        new JTextPaneTest();
    }
    JTextPaneTest(){
        jlText.setBounds(0,0,100,30);
        jtfText.setBounds(100,0,200,30);

        jlColor.setBounds(0,35,100,30);
        jtfChange.setBounds(100,35,200,30);

        //按钮相关设置
        /*向上找*/
        searchUp.setBounds(301,0,80,30);
        searchUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = jtfText.getText();
                String paneText = "";
                try {
                    paneText = doc.getText(0, doc.getLength());
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                if (jTextPane.getSelectedText() == null) {
                    k = paneText.lastIndexOf(searchText, jTextPane.getCaretPosition() - 1);
                } else {
                    k = paneText.lastIndexOf(searchText, jTextPane.getCaretPosition() - jTextPane.getSelectedText().length() - 1);

                }
                k2 = k + searchText.length();
                if (k > -1) {
                    jTextPane.setCaretPosition(k);
                    jTextPane.select(k, k2);
                    jTextPane.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "找不到您查找的内容！", "查找", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        /*向下找*/
        searchDown.setBounds(382,0,80,30);
        searchDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = jtfText.getText();
                String paneText = "";
                try {
                    paneText = doc.getText(0, doc.getLength());
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                int k = 0;
                if (jtfText.getSelectedText() == null) {
                    k = paneText.indexOf(searchText, jTextPane.getCaretPosition() + 1);
                } else {
                    k = paneText.indexOf(searchText, jTextPane.getCaretPosition() - searchText.length() + 1);
                }
                if (k > -1) {
                    jTextPane.setCaretPosition(k);
                    jTextPane.select(k, k + searchText.length());
                    jTextPane.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "找不到查找的内容？", "查找", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        /*替换*/
        changeTextSize.setBounds(301,35,162,30);
        changeTextSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextPane.replaceSelection(jtfChange.getText());//替换选中文本内容
            }
        });

        //JTextPane相关设置
        jTextPane.setBounds(10,100,460,340);

        add(searchUp);
        add(searchDown);
        add(jlColor);
        add(jlText);
        add(jtfChange);
        add(jtfText);
        add(changeTextSize);
        add(jTextPane);
        setLayout(null);
        setResizable(false);
        setBounds(500,500,500,500);
        setTitle("JTextPane组件");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}