package com.silin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/**
 * @author wsl
 * @create ${Year}-04-17-20:48
 */
public class Login extends JFrame {//窗口集成JFrame


    private JLabel unameJLabel;//用户名
    private JTextField unameJTextField;//用户名输入框
    private JButton btnJButton;//登陆按钮
    private JButton canJButton;//取消按钮


    public Login() throws HeadlessException {
        //A创建组件对象
        this.unameJLabel = new JLabel("用户名");
        this.unameJTextField = new JTextField();
        this.btnJButton = new JButton("登录");
        this.canJButton = new JButton("取消");

        //设置窗口属性
        this.setSize(400,300);//设置窗口大小
        this.setVisible(true);//默认窗口不可见，需打开
        this.setLocationRelativeTo(null);//设置窗口屏幕居中
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置窗口关闭该怎么操作

        this.setLayout(new GridLayout(2,2));//设置窗口布局，两行两列

        //按顺序添加组件到窗口中
        this.add(unameJLabel);
        this.add(unameJTextField);
        this.add(btnJButton);
        this.add(canJButton);

        //E创建完登录按钮监听器类后,进行绑定到按钮上
        MyEvent myEvent = new MyEvent();
        this.btnJButton.addActionListener(myEvent);//放置监听器对象进行绑定

    }

    //B给登录按钮创建事件监听器类，实现监听接口
    class MyEvent implements ActionListener{
        //重写监听接口里的方法
        @Override
        public void actionPerformed(ActionEvent e) {
            //点击登录需要拿到用户名

            //1.获得用户名
            String uname = unameJTextField.getText();//获得用户名中的文本内容

            //2.E创建一个socket连接服务器端
            try {
                Socket socket = new Socket("127.0.0.1", 8888);//服务器端就在本机，端口号:8888


                //3.跳转到主窗口区,socket要传给主窗口，主窗口要接收E1
                new MainFrame(uname, socket);//需要建一个MainFrame类


            } catch (IOException e1) {
                e1.printStackTrace();
            }


        }
    }
}
