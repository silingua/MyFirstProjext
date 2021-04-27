package com.silin.util;

import com.silin.model.PokerLabel;

/**
 * 把图片移动到指定位置并且有延时效果
 * @author wsl
 * @create ${Year}-04-20-14:26
 */
public class GameUtil {
    public static void move(PokerLabel pokerLabel, int x, int y){
        pokerLabel.setLocation(x, y);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
