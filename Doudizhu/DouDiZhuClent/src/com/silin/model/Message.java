package com.silin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *O：将要传递的信息写到一个类中
 * @author wsl
 * @create ${Year}-04-20-21:39
 */
public class Message implements Serializable{//流传递之前需要序列化

    private int typeid;//消息类型
    private int playerid;//玩家id
    private String content;//消息内容
    private List<Poker> pokers = new ArrayList<Poker>();//扑克列表

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public int getPlayerid() {
        return playerid;
    }

    public void setPlayerid(int playerid) {
        this.playerid = playerid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Poker> getPokers() {
        return pokers;
    }

    public void setPokers(List<Poker> pokers) {
        this.pokers = pokers;
    }

    public Message() {
    }

    public Message(int typeid, int playerid, String content, List<Poker> pokers) {
        this.typeid = typeid;
        this.playerid = playerid;
        this.content = content;
        this.pokers = pokers;
    }
}

