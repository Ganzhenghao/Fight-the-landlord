package com.ganzhenghao.fight.utlis;

import com.ganzhenghao.fight.bean.Poker;
import com.ganzhenghao.fight.bean.PokerNoColor;

import java.util.List;

/**
 * @ClassName PokerUtils
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/21 16:43 星期日
 * @Version 1.0
 */
public class PokerUtils {

    private PokerUtils() {
    }

    /**
     * 印刷扑克  TODO 待完善
     *
     * @param poker 扑克
     */
    public static void printPoker(List<PokerNoColor> poker) {
        int size = poker.size();
        System.out.println("————".repeat(size));
        poker.forEach(e -> {
            if ("小王".equals(e.getPoint()) || "大王".equals(e.getPoint())) {
                System.out.print("|" + e);
            } else {
                System.out.print("| " + e);
            }
        });
        System.out.println("|");
        System.out.println("|   ".repeat(size) + "|");
        System.out.println("————".repeat(size));
    }

}
