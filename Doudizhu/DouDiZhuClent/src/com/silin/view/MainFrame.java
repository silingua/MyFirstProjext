package com.silin.view;

import com.silin.model.Player;
import com.silin.model.Poker;
import com.silin.model.PokerLabel;
import com.silin.thread.CountThread;
import com.silin.thread.OutPokerThread;
import com.silin.thread.ReceiveThread;
import com.silin.thread.SendThread;
import com.silin.util.GameUtil;
import com.silin.util.PokerRule;
import com.silin.util.PokerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wsl
 * @create ${Year}-04-17-21:12
 */
public class MainFrame extends JFrame{

    //1.创建一个面板存背景图
    public MyPanel myPanel;

    //D
    public String uname;//创建成员变量接收用户名的信息

    public Socket socket;

    public SendThread sendThread;//E3把发送的线程作为成员变量

    public ReceiveThread receiveThread;//I2接收消息的线程

    public Player currentPlayer;//存放当前玩家对象

    public List<PokerLabel> pokerLabels = new ArrayList<PokerLabel>();//存放扑克标签列表

    public JLabel lordLabel1; //抢地主标签

    public JLabel lordLabel2; //不叫

    public JLabel timeLabel;  //定时器标签

    public CountThread countThread;//计数器的线程

    public boolean isLord;//是否抢了地主

    public JLabel msgLabel;//存放消息的label

    public JLabel lordIconLabel;//地主图标

    public List<PokerLabel> selectedPokerLabels = new ArrayList<PokerLabel>();//存放选中的扑克列表

    public JLabel outPokerJlabel;//出牌的标签

    public JLabel notOutPokerJlabel;//不出牌的标签

    public OutPokerThread outPokerThread;//出牌定时器的线程

    public boolean isOutPoker;//选择的是出牌还是不出牌

    public int prevPlayerid = -1;//上一个出牌的玩家ID

    public List<PokerLabel> showOutPokerLabels = new ArrayList<PokerLabel>();//存放当前已出牌的列表并显示

    public MainFrame(String uname, Socket socket) throws HeadlessException {//E1
        this.uname = uname;
        this.socket = socket;

        //设置窗口属性
        this.setTitle(uname);
        this.setSize(1200,700);//参考背景图大小
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //把面板mypanel添加至窗口
        myPanel = new MyPanel();//创建面板对象
        myPanel.setBounds(0,0,1200,700);//设置面板位置，绝对定位，从坐标(0,0)开始撑满窗口
        this.add(myPanel);

        //初始化窗口信息
        init();

        //多线程收消息，发消息
        // E3启动发消息的线程
//        SendThread sendThread = new SendThread(socket, uname);
        sendThread = new SendThread(socket, uname);
        sendThread.start();
        //F-I1需要建服务器端接收消息
        //I1启动接收消息的线程
        receiveThread = new ReceiveThread(socket, this);
        receiveThread.start();
    }

    //Q5,优化代码，增加窗口初始化方法
    public void init(){
        //Q1，优化代码，先创建msgLabel，创建消息框
        msgLabel = new JLabel();

        //显示出牌、不出牌和定时器的Label按钮
        outPokerJlabel = new JLabel();
        outPokerJlabel.setBounds(400, 380, 110, 53);
        outPokerJlabel.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/chupai.png"));
        outPokerJlabel.addMouseListener(new MyMouseEvent());
        outPokerJlabel.setVisible(false);
        this.myPanel.add(outPokerJlabel);

        notOutPokerJlabel = new JLabel();
        notOutPokerJlabel.setBounds(510, 380, 110, 53);
        notOutPokerJlabel.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/buchupai.png"));
        notOutPokerJlabel.addMouseListener(new MyMouseEvent());
        notOutPokerJlabel.setVisible(false);
        this.myPanel.add(notOutPokerJlabel);

        timeLabel = new JLabel();
        timeLabel.setBounds(640, 380, 50, 50);
        timeLabel.setFont(new Font("Dialog", 0, 30));
        timeLabel.setForeground(Color.red);
        timeLabel.setVisible(false);
        this.myPanel.add(timeLabel);
    }


    //K1在客户端显示扑克牌
    public void showAllPlayersInfo(List<Player> players) {

        //1.显示三个玩家名称

        //2.显示当前玩家的扑克列表
        for (int i = 0; i < players.size(); i++){

            if (players.get(i).getName().equals(uname)){
                currentPlayer = players.get(i);
            }
        }
        List<Poker> pokers = currentPlayer.getPokers();
        for (int i = 0; i < pokers.size(); i++){
            //创建扑克标签
            Poker poker = pokers.get(i);
            PokerLabel pokerLabel = new PokerLabel(poker.getId(), poker.getName(), poker.getNum());
            pokerLabel.turnUp();//显示正面图
            //添加到面板中
            this.myPanel.add(pokerLabel);
            this.pokerLabels.add(pokerLabel);
            //动态显示出来
            this.myPanel.setComponentZOrder(pokerLabel,0);//设置z轴的堆叠顺序，0代表每次叠加放置于最上部
            //一张一张的显示出来!涉及线程关系
            GameUtil.move(pokerLabel, 280 + 30 * i, 450);
        }

        //L1对扑克列表排序
        Collections.sort(pokerLabels);

        //L2重新移动位置
        for (int i = 0; i < pokerLabels.size(); i++){
            this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
            GameUtil.move(pokerLabels.get(i),  280 + 30 * i, 450);
        }

        //M设置抢地主
        System.out.println(currentPlayer);
        if (currentPlayer.getId() == 0){
            getLord(); //抢地主
        }

    }

    public void getLord() {
        //显示抢地主的按钮和定时器的按钮，而且后续的事件也要用，所以定义为成员变量

        lordLabel1 = new JLabel();
        lordLabel1.setBounds(400, 380, 104, 46);
        lordLabel1.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/jiaodizhu.png"));
        lordLabel1.addMouseListener(new MyMouseEvent());
        this.myPanel.add(lordLabel1);

        lordLabel2 = new JLabel();
        lordLabel2.setBounds(510, 380, 104, 46);
        lordLabel2.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/bujiao.png"));
        lordLabel2.addMouseListener(new MyMouseEvent());
        this.myPanel.add(lordLabel2);

//        timeLabel = new JLabel();
//        timeLabel.setBounds(350, 350, 50, 50);
//        timeLabel.setFont(new Font("Dialog", 0, 50));
//        timeLabel.setForeground(Color.red);
//        this.myPanel.add(timeLabel);
        //显示定时器图标
        this.timeLabel.setVisible(true);

        //重绘,类似刷新
        this.repaint();

        //启动计时器的线程
        countThread = new CountThread(10, this, true);
//        countThread.setRun(true);
        countThread.start();


    }

    //显示出牌的标签
    public void showOutPokerJlabel(){

        //U:优化
        if (prevPlayerid == currentPlayer.getId()){
            for (int i = 0; i < showOutPokerLabels.size(); i++){
                myPanel.remove(showOutPokerLabels.get(i));
            }
        }

        //显示出牌、不出牌和定时器的Label按钮
        outPokerJlabel.setVisible(true);
        notOutPokerJlabel.setVisible(true);
        timeLabel.setVisible(true);


        //重绘,类似刷新
        this.repaint();

        //Q6：启动出牌的定时器(线程)
        outPokerThread = new OutPokerThread(20, this, true);
        outPokerThread.start();

    }

    //P1显示消息（不抢或者不出）
    public void showMsg(int typeid, int playerid){
//        msgLabel = new JLabel();
        msgLabel.setVisible(true);
        msgLabel.setBounds(500,330,129,77);
        if (typeid == 1){
            msgLabel.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/buqiang.png"));
        }
        if (typeid == 3){
            msgLabel.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/buchu.png"));
        }

        //根据玩家id，文字信息需要显示到不同位置
        if (playerid == currentPlayer.getId()){
            msgLabel.setLocation(140, 530);
        }else if(playerid + 1 == currentPlayer.getId() || playerid - 2 == currentPlayer.getId()){
            msgLabel.setLocation(260, 230);//上一家2, 0    0, 1    1,2
        }else{
            msgLabel.setLocation(840, 230);//下一家
        }
        this.myPanel.add(msgLabel);
        this.repaint();

    }

    //p2添加地主牌
    public void addLordPokers(List<Poker> lordPokers){
        for (int i = 0; i < lordPokers.size(); i++){
            Poker poker = lordPokers.get(i);
            PokerLabel pokerLabel = new PokerLabel(poker.getId(), poker.getName(), poker.getNum());
            pokerLabel.turnUp();//显示正面图
            //添加到面板中
            this.pokerLabels.add(pokerLabel);
        }
        Collections.sort(pokerLabels);
        for (int i = 0; i < pokerLabels.size(); i++){
            this.myPanel.add(pokerLabels.get(i));
            //动态显示出来
            this.myPanel.setComponentZOrder(pokerLabels.get(i),0);//设置z轴的堆叠顺序，0代表每次叠加放置于最上部
            //一张一张的显示出来!涉及线程关系
            GameUtil.move(pokerLabels.get(i), 280 + 30 * i, 450);
        }
        currentPlayer.getPokers().addAll(lordPokers);
    }

    //Q:创建显示地主图标的方法
    public void showLordIcon(int playerid){
        //创建地主图标对象
        lordIconLabel = new JLabel();
        lordIconLabel.setIcon(new ImageIcon("RelatedMaterial/BackgroundImg/dizhu.png"));
        lordIconLabel.setSize(60, 89);

        //根据玩家id显示到具体位置
        if (playerid == currentPlayer.getId()){
            lordIconLabel.setLocation(150, 450);
        }else if(playerid + 1 == currentPlayer.getId() || playerid - 2 == currentPlayer.getId()){
            lordIconLabel.setLocation(270, 150);//上一家2, 0    0, 1    1,2
        }else{
            lordIconLabel.setLocation(850, 150);//下一家
        }

        //添加地主图标到面板上
        this.myPanel.add(lordIconLabel);
        this.repaint();//重绘
    }

    //Q4：为每张扑克牌添加单击事件
    public void addClickEventToPoker(){
        for (int i = 0; i < pokerLabels.size(); i++){
            pokerLabels.get(i).addMouseListener(new PokerEvent());
        }
    }

    //R:显示出牌的列表
    public void showOutPokerList(int playerid, List<Poker> outPokers){
        //从窗口上移除之前的出牌的列表
        for (int i = 0; i < showOutPokerLabels.size(); i++){
            myPanel.remove(showOutPokerLabels.get(i));
        }

        //清空之前的出牌列表
        showOutPokerLabels.clear();
        //S:代码优化
        msgLabel.setVisible(false);

        //R4：显示当前要出牌的列表
        for (int i = 0; i < outPokers.size(); i++){
            Poker poker = outPokers.get(i);
            PokerLabel pokerLabel = new PokerLabel(poker.getId(), poker.getName(), poker.getNum());
            pokerLabel.setSize(70, 100);
            pokerLabel.minPokerSize();
//            pokerLabel.setLocation(400 + 20 * i, 200);
            if (playerid == currentPlayer.getId()){
                pokerLabel.setLocation(500 + 20 * i, 280);
            }else if(playerid + 1 == currentPlayer.getId() || playerid - 2 == currentPlayer.getId()){
                pokerLabel.setLocation(300 + 20 * i, 260);//上一家2, 0    0, 1    1,2
            }else{
                pokerLabel.setLocation(720 + 20 * i, 260);;//下一家
            }

            myPanel.add(pokerLabel);
            showOutPokerLabels.add(pokerLabel);
            myPanel.setComponentZOrder(pokerLabel, 0);
        }
        this.repaint();//窗口重绘

    }

    //R4:出牌后将当前发送出去的扑克牌从当前玩家扑克牌列表中移除
    public void removeOutPokerFromPokerList(){
        //1.从当前玩家扑克列表中移除；
        pokerLabels.removeAll(selectedPokerLabels);
        //2.从面板中移除
        for (int i = 0;i < selectedPokerLabels.size(); i++){
            myPanel.remove(selectedPokerLabels.get(i));
        }
        //3.剩下的扑克要重新排序
        for (int i = 0; i < pokerLabels.size(); i++){
            myPanel.setComponentZOrder(pokerLabels.get(i), 0);
            GameUtil.move(pokerLabels.get(i), 280 + 30 * i, 450);
        }

        this.repaint();
        //4.清空selectedPokerLabels列表（ //S:代码优化）
        selectedPokerLabels.clear();
    }


    //U:游戏结束
    public void gameOver(){
        this.myPanel.removeAll();

        this.sendThread.setRun(false);

        this.receiveThread.setRun(false);

        this.repaint();
    }

    //O下一步，制作叫地主的单击事件，鼠标事件监听器
    class MyMouseEvent implements MouseListener {
        //重写鼠标事件中的方法

        @Override
        public void mouseClicked(MouseEvent e) {
            //点击的是抢地主
            if (e.getSource().equals(lordLabel1)){
                //停止计时器
                countThread.setRun(false);
                isLord = true;
                System.out.println("点击抢地主后，isRun是" + countThread.isRun());
                System.out.println("点击抢地主后，isLord是" + isLord);

                //设置抢地主按钮不可见
                lordLabel1.setVisible(false);
                lordLabel2.setVisible(false);
                timeLabel.setVisible(false);
//                System.out.println("抢地主");

            }

            //点击的是不抢地主
            if (e.getSource().equals(lordLabel2)){
                //停止计时器
                countThread.setRun(false);
                isLord = false;
                System.out.println("点击不抢地主后，isRun是" + countThread.isRun());
                System.out.println("点击不抢地主后，isLord是" + isLord);
                //设置抢地主按钮不可见
                lordLabel1.setVisible(false);
                lordLabel2.setVisible(false);
                timeLabel.setVisible(false);
//                System.out.println("不抢地主");

            }

            if (e.getSource().equals(outPokerJlabel)){
                PokerType pokerType = PokerRule.checkPokerType(selectedPokerLabels);

                //T:判断是否符合牌型
                if (! pokerType.equals(pokerType.p_error)){

                    //T:符合牌型，判断是不是比上家大或者上家就是自己
                    if (prevPlayerid == -1 || prevPlayerid == currentPlayer.getId() || PokerRule.isBigger(showOutPokerLabels, selectedPokerLabels)){
                        isOutPoker = true;
                        //计时器停止
                        outPokerThread.setRun(false);
                        outPokerJlabel.setVisible(false);
                        notOutPokerJlabel.setVisible(false);
                        timeLabel.setVisible(false);

                    }else{
                        JOptionPane.showMessageDialog(null, "请按规则出牌");

                    }

                }else{
                    JOptionPane.showMessageDialog(null, "不符合牌型");
                }

            }

            if (e.getSource().equals(notOutPokerJlabel)){
                isOutPoker = false;
                //计时器停止
                outPokerThread.setRun(false);
                outPokerJlabel.setVisible(false);
                notOutPokerJlabel.setVisible(false);
                timeLabel.setVisible(false);
            }



        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //Q4：制作扑克牌的单击事件监听器
    class PokerEvent implements  MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //如果选择过了，需要取消选择(位置回到原位，从选择的扑克牌列表中移除,设置选中属性为false)
            PokerLabel pokerLabel = (PokerLabel) e.getSource();
            if (pokerLabel.isSelected()){
                pokerLabel.setLocation(pokerLabel.getX(), pokerLabel.getY() + 15);//往下移动，+30
                selectedPokerLabels.remove(pokerLabel);
                pokerLabel.setSelected(false);
            }else{
            //如果之前没有选择，则选中(位置往上移动一点点，添加到选择的扑克牌列表中，设置选中属性为true)
                pokerLabel.setLocation(pokerLabel.getX(), pokerLabel.getY() - 15);
                selectedPokerLabels.add(pokerLabel);
                pokerLabel.setSelected(true);
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


}
