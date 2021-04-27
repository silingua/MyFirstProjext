package com.silin.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silin.model.Message;
import com.silin.model.Player;
import com.silin.model.Poker;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用来服务器端接受socket
 * @author wsl
 * @create ${Year}-04-17-22:37
 */
public class MainFrame {
    //G3创建玩家列表存储玩家
    public List<Player> players = new ArrayList<Player>();

    //G3玩家id自增
    public int index = 0;

    //H1创建集合存放所有扑克牌
    public List<Poker> allPokers = new ArrayList<Poker>();

    //存放扑克底牌
    public List<Poker> lordPokers = new ArrayList<Poker>();

    //O1
    public int step = 0;//牌局的进展步骤

    public MainFrame(){
        //H2创建扑克列表

        createPokers();



        try {
            //1.创建服务器端的socket
            ServerSocket serverSocket = new ServerSocket(8888);//创建服务器端的端口号
            //debug:F2只接受了一次客户端的消息，需要多次接收，加个while循环
            while (true){
                if (index == 3){
                    break;
                }

                //2.接受客户端传来的socket
                Socket socket = serverSocket.accept();
                //3.F1开启线程，处理客户端的socket(客户端可能传来多个socket)
                AcceptThread acceptThread = new AcceptThread(socket);
                acceptThread.start();
            }
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("服务器异常：" + e.getMessage());
        }


    }


    //F1创建一个内部接受线程处理客户端的信息
    class AcceptThread extends Thread{
        //需要构造函数接受socket
        Socket socket;

        public AcceptThread(Socket socket) {
            this.socket = socket;
        }

        //线程重写run方法
        @Override
        @Test
        public void run() {
            //接收消息需要输入流
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                while (true){
                    String msg = dataInputStream.readUTF();

                    System.out.println(msg);
                    if (step == 0){

                        //G3创建player对象
                        Player player = new Player(index++, msg);
                        player.setSocket(socket);
                        //存入玩家列表
                        players.add(player);
                        //"从客户端传来的消息：" +
                        System.out.println(msg + "上线了");
                        System.out.println("当前上线人数：" + players.size());

                        //H玩家上线3人后续操作为发牌
                        //H3
                        if (players.size() == 3){
                            sendpokers();
                            step = 1;
//                            System.out.println("step已经变为1");
                        }
                    }else if (step == 1){//接收抢地主的信息
                        System.out.println("接收抢地主的消息");
                        JSONObject msgJsonObject = JSON.parseObject(msg);
                        int typeid = msgJsonObject.getInteger("typeid");
                        int playerid = msgJsonObject.getInteger("playerid");
                        String content = msgJsonObject.getString("content");


                        //抢地主需要带回地主牌
                        if (typeid == 2){
                            //重新组将一个消息对象，添加地主牌
                            Message sendMessage = new Message(typeid, playerid, content, lordPokers);
                            msg = JSON.toJSONString(sendMessage);
                            step = 2;

                        }
                        //不抢
//                        if (typeid == 1){
//                            //将客户端发回的不抢的信息原样群发到所有玩家
//
//                        }
                        sendMessageToClient(msg);

//                    Thread.sleep(50);

                    }else if (step == 2){//代表出牌和不出牌的消息 + 游戏结束的消息
                        sendMessageToClient(msg);//
                        //关于出牌与不出牌，服务器端仅仅是转发到所有客户端的作用
                    }



                }

            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("服务器异常:" + e.getMessage());
            }
        }
    }

    //群发送消息到客户端
    public void sendMessageToClient(String msg){
        for (int i = 0;i < players.size(); i++){
            DataOutputStream dataOutputStream;
            try {
                dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
                dataOutputStream.writeUTF(msg);
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        }
    }
    //P:之后客户端需要接收消息


    //H2创建所有扑克列表（确认好扑克图片与编号的对应）
    public void createPokers() {
        //创建大小王
        Poker dawang = new Poker(0, "大王", 17);
        Poker xiaowang = new Poker(1, "小王", 16);

        allPokers.add(dawang);
        allPokers.add(xiaowang);

        //创建其它扑克，4个一创建
        String[] names = new String[]{"2", "A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3"};
        String[] colors = new String[]{"黑桃", "红桃", "梅花", "方块"};


        int id = 2;
        int num = 15;
        //加强for循环，遍历数组里每一项为字符串

        //遍历扑克种类
        for (String name: names){
            //遍历扑克花色
            for (String color: colors){
                Poker poker = new Poker(id++, color + name, num);
                allPokers.add(poker);
            }
            num--;
        }
        //洗牌-集合中的方法，打乱顺序
        Collections.shuffle(allPokers);

    }

    //H4创建发牌方法，给三个玩家分配扑克列表
    public void sendpokers() {

        //发给三个玩家
        for (int i = 0; i < allPokers.size(); i++){
            if(i >= 51){//最后3张牌放在底牌里（地主牌）
                lordPokers.add(allPokers.get(i));//当前的扑克对象存到底牌里
            }else{
                //依次发给三个玩家
                if (i % 3 == 0){
                    players.get(0).getPokers().add(allPokers.get(i));//取模为0，发给第一个玩家的扑克列表里
                }else if (i % 3 == 1){
                    players.get(1).getPokers().add(allPokers.get(i));//取模为1，发给第二个玩家的扑克列表里
                }else{
                    players.get(2).getPokers().add(allPokers.get(i));//取模为2，发给第三个玩家的扑克列表里
                }

            }
        }
        //I将玩家信息发送给客户端，添加jar包
        //集合套对象，对象套集合，用socket传递相对复杂，需要序列化
        //用json对象传递{"id":1, "name":"aa","socket":"","pokers":[{"id":1,"name":"黑桃k","num":13},{},{}]}
        //相当于先转为json字符串，转过去再转回来
        for (int i = 0;i < players.size(); i++){
            try {
                //把当前玩家的信息发过去
                DataOutputStream dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
                String jsonString = JSON.toJSONString(players);
                dataOutputStream.writeUTF(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
}
