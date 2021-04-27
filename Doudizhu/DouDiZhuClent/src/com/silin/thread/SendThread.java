package com.silin.thread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author wsl
 * @create ${Year}-04-17-22:17
 */

//E2发送消息的线程
public class SendThread extends Thread{
    private String msg;
    private Socket socket;
    private  boolean isRun = true;

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    //构造函数传递socket对象
    public SendThread() {
    }

    public SendThread(Socket socket){
        this.socket = socket;
    }

    public SendThread(Socket socket, String msg) {
        this.msg = msg;
        this.socket = socket;
    }


    //发送：需要拿到socket中的输出流(字节流——>包装成数据流)，因为传过来的数据类型不统一
    public void run(){

        DataOutputStream dataOutputStream;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            while (true){

                if (this.isRun == false)
                    break;
                //如果消息不为空，需要发送出去，发送后消息内容要清空
                if (msg != null){
                    System.out.println("消息在发送中："+ msg);
                    dataOutputStream.writeUTF(msg);
                    msg = null;
                }
                Thread.sleep(50);//暂停等待新消息进来
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
