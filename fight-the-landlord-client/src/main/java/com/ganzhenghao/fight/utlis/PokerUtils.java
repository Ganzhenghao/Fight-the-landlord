package com.ganzhenghao.fight.utlis;

import com.ganzhenghao.fight.bean.Poker;
import com.ganzhenghao.fight.bean.PokerNoColor;
import com.ganzhenghao.fight.enu.Rule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @ClassName PokerUtils
 * @Description 扑克牌工具类
 * @Author Ganzhenghao
 * @Date 2021/3/21 16:43 星期日
 * @Version 1.0
 */
public class PokerUtils {

    private static final List<String> POINT = new ArrayList<>(List.of("3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"));

    private final static Map<String,Integer> POINT_VALUE_MAP;

    private PokerUtils() {
    }

    static {
        Map<String,Integer> map = new HashMap<>();
        AtomicInteger value = new AtomicInteger(1);
        POINT.forEach(p -> {
            map.put(p, value.intValue());
            value.getAndIncrement();
        });
        map.put("小王", value.intValue());
        value.getAndIncrement();
        map.put("大王", value.intValue());
        POINT_VALUE_MAP = map;
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
        return POINT_VALUE_MAP;
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

    public static boolean checkPlayCards(String playCards){

        List<PokerNoColor> list = convertString2List(playCards);

        // 0 -> Rule  1 -> List<PokerNoColor>
        List<Object> info = new ArrayList<>(2);

        // TODO　检查合法性

        //解析 单张
        if (list.size() == 1){
            info.add(Rule.Single);
        }

        //解析对子 王炸

        if (list.size() == 2){
            //判断是否为王炸
            PokerNoColor big = new PokerNoColor("大王", POINT_VALUE_MAP.get("大王"));
            PokerNoColor small = new PokerNoColor("小王", POINT_VALUE_MAP.get("小王"));
            if (list.contains(big) && list.contains(small)){
                info.add(Rule.KingBomb);
            }

            //判断是否为对子

            if (list.get(0).equals(list.get(1))){

            };

        }

        //解析 顺子

        //解析 顺子简写 3-7

        //解析 姊妹对

        //解析 姊妹对 简写 33-77

        //解析 3带1

        //飞机

        //解析 普通炸弹




        return true;
    }

    /**
     * 转换string转换为扑克列表
     *
     * @param playCards 打牌
     * @return {@link List<PokerNoColor>}
     */
    public static List<PokerNoColor> convertString2List(String playCards){

        if (playCards.isBlank()){
            throw new RuntimeException("出牌不能为空!");
        }

        //字符串解析前 去除所有空格

        playCards = playCards.replace(" ", "");

        //解析为PokerNoColor集合
        List<PokerNoColor> playCardsList = new ArrayList<>();
        char[] cards = playCards.toCharArray();
        Stream.of(cards)
                .forEach(e -> playCardsList.add(new PokerNoColor(String.valueOf(e),POINT_VALUE_MAP.get(String.valueOf(e)))));
        return playCardsList;
    }


    /**
     * 出牌  如果手牌能够出 则返回true 并扣减相应手牌
     * 如果手牌能出 也需要 判断是否能大过上家
     *
     * 否则返回false
     *
     * @param handCardsList 手牌列表
     * @param playCardsList 出牌列表
     * @param upPlayerCards 上家的出牌
     * @return boolean
     */
    public static boolean playCards(List<PokerNoColor> handCardsList,List<PokerNoColor> playCardsList,List<PokerNoColor> upPlayerCards){

        List<PokerNoColor> handCardsListCopy = new ArrayList<>(handCardsList);

        for (PokerNoColor card : playCardsList) {
            for (int i = 0; i < handCardsListCopy.size(); i++) {

                PokerNoColor pokerNoColor = handCardsListCopy.get(i);
                if (isSame(card , pokerNoColor)) {
                    handCardsListCopy.remove(i);
                    i--;
                    continue;
                }

                if (i == handCardsListCopy.size()-1){
                    return false;
                }
            }
        }

        //TODO 判断手牌是否能大过上家

        handCardsList.clear();
        handCardsList.addAll(handCardsListCopy);
        return true;
    }


    /**
     * 比较两个PokerNoColor对象的点数是否相同
     *
     * @return boolean
     */
    public static boolean isSame(PokerNoColor p1,PokerNoColor p2){
        return p1.getPoint().equals(p2.getPoint());
    }

    /**
     * 判断当前
     *
     * @param upPlayerCards      上家出牌
     * @param currentPlayerCards 当前玩家出牌
     * @return boolean
     */
    public static boolean isUpper(List<PokerNoColor> upPlayerCards,List<PokerNoColor> currentPlayerCards){

        //如果上家出牌为空,则表明由当前玩家出牌 TODO

        return true;
    }



}
