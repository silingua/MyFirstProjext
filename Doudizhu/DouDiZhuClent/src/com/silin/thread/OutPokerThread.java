package com.silin.thread;

import com.alibaba.fastjson.JSON;
import com.silin.model.Message;
import com.silin.model.Poker;
import com.silin.model.PokerLabel;
import com.silin.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsl
 * @create ${Year}-04-26-16:45
 */
public class OutPokerThread extends Thread{
    private int time;
    private MainFrame mainFrame;
    private boolean isRun;

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public OutPokerThread(int time, MainFrame mainFrame, boolean isRun) {
        this.time = time;
        this.mainFrame = mainFrame;
        this.isRun = isRun;
    }

    @Override
    public void run() {
        while (time >= 0 && isRun){
            mainFrame.timeLabel.setText(time + "");
            time--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Message message = null;
        //R:如果不出牌(一种是时间到了，一种是选择不出)
        if (time == -1 || isRun == false && mainFrame.isOutPoker == false){
            if (time == -1){
                mainFrame.outPokerJlabel.setVisible(false);
                mainFrame.notOutPokerJlabel.setVisible(false);
                mainFrame.timeLabel.setVisible(false);
            }
            message = new Message(3, mainFrame.currentPlayer.getId(), "不出", null);
            //转换为Json交给sendThread中的msg，发送到服务器端
            String msg = JSON.toJSONString(message);
            mainFrame.sendThread.setMsg(msg);
        }

        if (isRun == false && mainFrame.isOutPoker == true){
            //出牌的的列表需要强转
            message = new Message(4, mainFrame.currentPlayer.getId(), "出牌", changePokerLabelToPoker(mainFrame.selectedPokerLabels));
            //转换为Json交给sendThread中的msg，发送到服务器端
            String msg = JSON.toJSONString(message);
            mainFrame.sendThread.setMsg(msg);
            //R4:将当前发送出去的扑克牌从扑克牌列表中移除
            mainFrame.removeOutPokerFromPokerList();
            //U:如果扑克列表数量为0，代表赢了
            if (mainFrame.pokerLabels.size() == 0){
                message = new Message(5, mainFrame.currentPlayer.getId(), "游戏结束", null);
                msg = JSON.toJSONString(message);
                try {
                    Thread.sleep(100);//线程休眠
                    mainFrame.sendThread.setMsg(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
//        mainFrame.outPokerJlabel.setVisible(false);
//        mainFrame.notOutPokerJlabel.setVisible(false);
//        mainFrame.timeLabel.setVisible(false);

////        转换为Json交给sendThread中的msg，发送到服务器端
//        String msg = JSON.toJSONString(message);
//        mainFrame.sendThread.setMsg(msg);
    }

    //从selectedPokerLabels中拿到出牌的列表
    public List<Poker> changePokerLabelToPoker(List<PokerLabel> selectedPokerLabels){
        List<Poker> list = new ArrayList<Poker>();
        for (int i = 0; i < selectedPokerLabels.size(); i++){
            PokerLabel pokerLabel = selectedPokerLabels.get(i);
            Poker poker = new Poker(pokerLabel.getId(), pokerLabel.getName(), pokerLabel.getNum());
            list.add(poker);
        }
        return list;
    }
}
