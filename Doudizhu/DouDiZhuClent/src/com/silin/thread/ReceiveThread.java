package com.silin.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.silin.model.Player;
import com.silin.model.Poker;
import com.silin.view.MainFrame;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * I1创建一个接收消息的线程
 *
 * @author wsl
 * @create ${Year}-04-19-14:19
 */
public class ReceiveThread extends Thread {

    private Socket socket;
    private MainFrame mainFrame;

    //P:接收服务器端的消息
    private int step = 0;
    private boolean isRun = true;

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public ReceiveThread(Socket socket, MainFrame mainFrame) {
        this.socket = socket;
        this.mainFrame = mainFrame;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                if (isRun == false){
                    break;
                }
                String jsonString = dataInputStream.readUTF();
                //获取三个玩家信息
                if (step == 0) {//第一步

                    List<Player> players = new ArrayList<Player>();


                    // System.out.println(jsonString);
                    //J2解析JSON字符串,"[{},{}]" [{},{}],去掉了外面的双引号成为一个数组结构
                    //将json字符串转换为json数组
                    JSONArray PlayerJsonArray = JSONArray.parseArray(jsonString);
                    for (int i = 0; i < PlayerJsonArray.size(); i++) {

                        //获得当前的json对象-->玩家对象
                        JSONObject playerJson = (JSONObject) PlayerJsonArray.get(i);
                        Integer id = playerJson.getInteger("id");
                        String name = playerJson.getString("name");

                        List<Poker> pokers = new ArrayList<Poker>();
                        JSONArray pokerJsonArray = playerJson.getJSONArray("pokers");

                        for (int j = 0; j < pokerJsonArray.size(); j++) {
                            //每循环一次获得一个扑克对象
                            JSONObject pokerJSON = (JSONObject) pokerJsonArray.get(j);
                            int pokerid = pokerJSON.getInteger("id");
                            String pokername = pokerJSON.getString("name");
                            int pokernum = pokerJSON.getInteger("num");

                            Poker poker = new Poker(pokerid, pokername, pokernum);
                            pokers.add(poker);
                        }

                        Player player = new Player(id, name, pokers);
                        players.add(player);
                    }

                    //K获得三个玩家信息后需要主窗口显示所有玩家信息
                    if (players.size() == 3) {
                        mainFrame.showAllPlayersInfo(players);
                        step = 1;//玩家到齐，进展到第二步
                    }
                } else if (step == 1) {
                    //接收抢地主的消息或者出牌的消息
                    JSONObject msgJsonObject = JSONObject.parseObject(jsonString);
                    //解析消息对象
                    int typeid = msgJsonObject.getInteger("typeid");
                    int playerid = msgJsonObject.getInteger("playerid");
                    String contentString = msgJsonObject.getString("content");


                    //消息类型为不抢
                    if (typeid == 1) {
                        //1.主窗口显示不抢的信息
                        mainFrame.showMsg(1, playerid);

                        //2.设置下一家开始抢地主
                        //如果自己是下一家，就是要开始抢地主了
                        if (playerid + 1 == mainFrame.currentPlayer.getId()){
                            mainFrame.getLord();
                        }
                    }
                    //抢地主的消息
                    if (typeid == 2) {
                        //抢地主，获得地主牌

                        List<Poker> lordPokers = new ArrayList<Poker>();

                        JSONArray pokersJsonArray = msgJsonObject.getJSONArray("pokers");
                        for (int i = 0; i < pokersJsonArray.size(); i++) {
                            JSONObject pokerObject = (JSONObject) pokersJsonArray.get(i);
                            int id = pokerObject.getInteger("id");
                            String name = pokerObject.getString("name");
                            int num = pokerObject.getInteger("num");
                            Poker poker = new Poker(id, name, num);
                            lordPokers.add(poker);
                        }
                        //获得地主牌要添加给抢地主的玩家，如果自己是地主
                        if (mainFrame.currentPlayer.getId() == playerid){
                            //添加地主牌
                            mainFrame.addLordPokers(lordPokers);
                            //Q3:显示出牌的按钮，第一家出牌
                            //R：显示出牌标签
                            mainFrame.showOutPokerJlabel();
                        }

                        //Q:显示地主图标
                        mainFrame.showLordIcon(playerid);

                        //Q:隐藏之前的消息框
                        mainFrame.msgLabel.setVisible(false);

                        //Q3:所有玩家都可以选择出牌列表(不代表能出牌)
                        mainFrame.addClickEventToPoker();
                    }
                    if (typeid == 3){//不出牌
                        //显示不出牌的消息
                        mainFrame.showMsg(3, playerid);
                        //S:判断自己是不是下一家，如果是显示出牌按钮
                        if (playerid + 1 == mainFrame.currentPlayer.getId() || playerid - 2 == mainFrame.currentPlayer.getId()){
                            mainFrame.showOutPokerJlabel();
                        }

                    }

                    if (typeid == 4){//出牌
                        //获得出牌的列表
                        JSONArray pokersJsonArray = msgJsonObject.getJSONArray("pokers");
                        List<Poker> outPokers = new ArrayList<Poker>();
                        for (int i = 0; i < pokersJsonArray.size(); i++) {
                            JSONObject pokerObject = (JSONObject) pokersJsonArray.get(i);
                            int id = pokerObject.getInteger("id");
                            String name = pokerObject.getString("name");
                            int num = pokerObject.getInteger("num");
                            Poker poker = new Poker(id, name, num);
                            outPokers.add(poker);
                        }

                        //显示出牌列表
                        mainFrame.showOutPokerList(playerid, outPokers);
                        //S:判断自己是不是下一家，如果是显示出牌按钮
                        if (playerid + 1 == mainFrame.currentPlayer.getId() || playerid - 2 == mainFrame.currentPlayer.getId()){
                            mainFrame.showOutPokerJlabel();
                        }
                        mainFrame.prevPlayerid = playerid;//记录上一个出牌的玩家id
                    }

                    //U:如果是游戏结束的消息
                    if (typeid == 5){
                        if (playerid == mainFrame.currentPlayer.getId()){
                            JOptionPane.showMessageDialog(mainFrame, "游戏胜利");
                        }else{
                            JOptionPane.showMessageDialog(mainFrame, "游戏失败");
                        }

                        //游戏结束，处理窗口，清空，暂停接收和发送消息的线程处理
                        mainFrame.gameOver();
//                        mainFrame.sendThread.interrupt();
//                        mainFrame.receiveThread.interrupt();
//                        mainFrame.countThread.interrupt();
//                        mainFrame.outPokerThread.interrupt();

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//J接收服务器端的信息后需要进行解析
