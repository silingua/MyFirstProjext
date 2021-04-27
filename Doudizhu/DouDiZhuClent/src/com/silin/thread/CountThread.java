package com.silin.thread;

import com.alibaba.fastjson.JSON;
import com.silin.model.Message;
import com.silin.view.MainFrame;

/**
 * N设置计时器的线程
 * @author wsl
 * @create ${Year}-04-20-19:37
 */

public class CountThread extends Thread{
    private int i;

    private MainFrame mainFrame;

    private boolean isRun;

    public void setRun(boolean run) {
        isRun = run;
    }

    public boolean isRun() {
        return isRun;

    }

//    public void setRun(boolean isrun) {
//        this.isRun = isRun;
//    }

    public CountThread(int i, MainFrame mainFrame, boolean isRun) {
        this.i = i;
        this.mainFrame = mainFrame;
        this.isRun = isRun;
    }

//    public CountThread(int i, MainFrame mainFrame) {
//        isRun = true;
//        this.i = i;
//
//        this.mainFrame = mainFrame;
//    }

    @Override
    public void run() {
        while (isRun && i >= 0){
            System.out.println(isRun);
            mainFrame.timeLabel.setText(i + "");
            i--;
//            System.out.println("第"+i + "秒");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //如果退到了循环外，说明时间到了，或者已经点击过抢地主/不抢地主按钮

        Message msg = null;
        //时间到了  或者  中途停止已经点击过不抢地主的按钮了
        if (i == -1 || (isRun == false && mainFrame.isLord == false)){
            msg = new Message(1, mainFrame.currentPlayer.getId(), "不抢", null);
//            System.out.println(msg.getContent());
//            System.out.println(msg.getPlayerid());
//            System.out.println("煞笔，不抢");
        }
        //中途停止，点击了抢地主的按钮了
        if ((isRun == false) && (mainFrame.isLord == true)){
            msg = new Message(2, mainFrame.currentPlayer.getId(), "抢地主", null);
//            System.out.println(msg.getContent());
//            System.out.println(msg.getPlayerid());
//            System.out.println("煞笔，抢");
        }

        //将消息传到服务器端
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));
//        mainFrame.sendThread.run();
    }
}
