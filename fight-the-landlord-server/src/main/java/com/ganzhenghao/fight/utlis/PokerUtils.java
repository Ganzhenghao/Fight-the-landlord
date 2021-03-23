package com.ganzhenghao.fight.utlis;

import com.ganzhenghao.fight.bean.Poker;
import com.ganzhenghao.fight.bean.PokerNoColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName PokerUtils
 * @Description 扑克牌工具类
 * @Author Ganzhenghao
 * @Date 2021/3/21 16:43 星期日
 * @Version 1.0
 */
public class PokerUtils {

    private static final List<String> POINT = new ArrayList<>(List.of("3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"));

    private PokerUtils() {
    }

    /**
     * 印刷扑克
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


    /**
     * 得到点值映射
     *
     * @return {@link Map<String, Integer>}
     */
    public static Map<String,Integer> getPointValueMap(){
        Map<String,Integer> map = new HashMap<>();
        AtomicInteger value = new AtomicInteger(1);
        POINT.forEach(p -> {
            map.put(p, value.intValue());
            value.getAndIncrement();
        });
        map.put("小王", value.intValue());
        value.getAndIncrement();
        map.put("大王", value.intValue());
        return map;
    }

    /**
     * 获得扑克列表(不带花色)
     *
     * @return {@link List<Poker>}
     */
    public static List<PokerNoColor> getPokerNoColorList() {
        List<String> point = new ArrayList<>(List.of("3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"));
        List<PokerNoColor> poker = new ArrayList<>(54);
        AtomicInteger value = new AtomicInteger(1);
        point.forEach(p -> {
            for (int i = 0; i < 4; i++) {
                poker.add(new PokerNoColor(p, value.intValue()));
            }
            value.getAndIncrement();
        });

        poker.add(new PokerNoColor("小王", value.intValue()));
        value.getAndIncrement();
        poker.add(new PokerNoColor("大王", value.intValue()));
        return poker;
    }

    /**
     * 获得扑克列表(带花色)
     *
     * @return {@link List<Poker>}
     */
    public static List<Poker> getPokerList() {
        List<String> color = new ArrayList<>(List.of("♠", "♥", "♦", "♣"));
        List<Poker> poker = new ArrayList<>(54);
        AtomicInteger value = new AtomicInteger(1);
        POINT.forEach(p -> {
            color.forEach(c -> {
                poker.add(new Poker(c, p, value.intValue()));
            });
            value.getAndIncrement();
        });

        poker.add(new Poker("", "小王", value.intValue()));
        value.getAndIncrement();
        poker.add(new Poker("", "大王", value.intValue()));
        return poker;
    }

}
