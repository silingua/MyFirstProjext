package com.silin.model;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author wsl
 * @create ${Year}-04-19-14:35
 */

//J1扑克标签类，用来解析服务器端传来的信息
public class PokerLabel extends JLabel implements Comparable{
    private int id;
    private String name;
    private int num;
    private boolean isOut;
    private boolean isUp;
    private boolean isSelected;//是否选中

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    public PokerLabel() {
        this.setSize(105, 150);//对应每张扑克图片的大小
    }

    public PokerLabel(int id, String name, int num) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.setSize(105, 150);
    }

    public PokerLabel(int id, String name, int num, boolean isOut, boolean isUp) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.isOut = isOut;
        this.isUp = isUp;

        if (isUp){
            turnUp();
        }else{
            turnDown();
        }
    }

    //设置正面图片方法
    public void turnUp(){
        ImageIcon imageIcon = new ImageIcon("RelatedMaterial/poker/" + id + ".jpg");
        this.setIcon(imageIcon);
    }

    //设置出牌时图片大小为2/3;
    public void minPokerSize(){
        ImageIcon image = new ImageIcon("RelatedMaterial/poker/" + id +".jpg");
        image.setImage(image.getImage().getScaledInstance(70, 100, Image.SCALE_DEFAULT));
        this.setIcon(image);
    }

    //设置反面图片方法
    public void turnDown(){
        this.setIcon(new ImageIcon("RelatedMaterial/poker/Down.jpg"));
    }

    //L:对扑克列表排序
    @Override
    public int compareTo(Object o) {
        PokerLabel pokerLabel = (PokerLabel) o;
        if (this.num > pokerLabel.num)
            return 1;
        else if (this.num < pokerLabel.num)
            return -1;
        else
            return 0;
    }
}
