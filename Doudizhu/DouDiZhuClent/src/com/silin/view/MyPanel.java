package com.silin.view;

import javax.swing.*;
import java.awt.*;

/**
 * @author wsl
 * @create ${Year}-04-17-21:14
 */
public class MyPanel extends JPanel{
    //C面板


    public MyPanel() {
        this.setLayout(null);//如果需要用到setLocation() setBounds()就需要设置布局为null
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);

        //重写paintComponent，这个方法作用是画主界面
        Image image = new ImageIcon("RelatedMaterial\\BackgroundImg\\bg1.png").getImage();
        //定义主界面的宽高
        g.drawImage(image, 0, 0, this.getWidth(),this.getHeight(),null);
    }

}
