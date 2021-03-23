package com.ganzhenghao.fight.utlis;

import com.ganzhenghao.fight.NotEqualsException;
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

    private static final List<String> POINT = new ArrayList<>(List.of("3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A", "2"));

    private final static Map<String, Integer> POINT_VALUE_MAP;

    private PokerUtils() {
    }

    static {
        Map<String, Integer> map = new HashMap<>();
        AtomicInteger value = new AtomicInteger(1);
        POINT.forEach(p -> {
            map.put(p, value.intValue());
            value.getAndIncrement();
        });
        map.put("S", value.intValue());
        value.getAndIncrement();
        map.put("B", value.intValue());
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
            if ("S".equals(e.getPoint())) {
                System.out.print("|" + "大王");
            } else if ("B".equals(e.getPoint())) {
                System.out.print("|" + "小王");
            } else if ("T".equals(e.getPoint())) {
                System.out.println("|10");
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
    public static Map<String, Integer> getPointValueMap() {
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

        poker.add(new PokerNoColor("S", value.intValue()));
        value.getAndIncrement();
        poker.add(new PokerNoColor("B", value.intValue()));
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

        poker.add(new Poker("", "S", value.intValue()));
        value.getAndIncrement();
        poker.add(new Poker("", "B", value.intValue()));
        return poker;
    }

    /**
     * 检查出牌是否符合规范 然后封装牌的类型(单张 对子 ...) 0号索引
     *
     * @param playCards 打牌
     * @return {@link List<Object>}
     */
    public static List<Object> checkPlayCards(String playCards) {

        List<PokerNoColor> list = convertString2List(playCards);

        // 0 -> Rule  1 -> List<PokerNoColor>
        List<Object> info = new ArrayList<>(2);


        //解析 单张
        if (list.size() == 1) {
            info.add(Rule.Single);
        }

        //解析对子 王炸

        if (list.size() == 2) {
            //判断是否为王炸
            PokerNoColor big = new PokerNoColor("B", POINT_VALUE_MAP.get("B"));
            PokerNoColor small = new PokerNoColor("S", POINT_VALUE_MAP.get("S"));
            if (list.contains(big) && list.contains(small)) {
                info.add(Rule.KingBomb);
            }

            //判断是否为对子

            if (list.get(0).equals(list.get(1))) {
                info.add(Rule.Double);
            }
        }

        //当size == 4 时判断 是否为炸弹 和 三带一

        if (list.size() == 4){
            try {
                //四个比较  出异常则表示 四个不等 那就不是炸弹
                list.get(0).chainCompare(list.get(1))
                        .chainCompare(list.get(2))
                        .chainCompare(list.get(3));
                info.add(Rule.Bomb);
            } catch (NotEqualsException e) {
                //三个比较 出异常则表示 不是三带一
                try {
                    list.get(0).chainCompare(list.get(1))
                            .chainCompare(list.get(2));
                    info.add(Rule.ThreeBeltOne);
                } catch (Exception ignored) { }
            }
        }

        //当size大于等于5时 开始判断 顺子 姊妹对 多飞机 等等
        if (list.size() >= 5){
            //判断 顺子
            boolean flag = true;
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i).getValue() != (list.get(i + 1).getValue() - 1)) {
                    flag = false;
                    break;
                }
            }
            if (flag){
                info.add(Rule.Straight);
            }

            //双数 判断
            if (list.size() % 2 == 0){
                //判断姊妹对
                boolean secondFlag = true;
                for (int i = 0; i < list.size(); i += 2) {
                    if (!list.get(i).equals(list.get(i + 1))) {
                        secondFlag = false;
                        break;
                    }
                }
                if (secondFlag){
                    info.add(Rule.SisterPair);
                }
            }
        }

        // %4 == 0 且 大于8
        //判断飞机
        if (list.size() >= 8 && list.size() % 4 == 0){

            for (int i = 0; i < list.size(); i+=4) {
                try {
                    list.get(i).chainCompare(list.get(i+1))
                            .chainCompare(list.get(i+2));
                    info.add(Rule.Plain);
                } catch (NotEqualsException ignored) { }
            }
        }
        info.add(list);
        return info;
    }

    /**
     * 转换string转换为扑克列表
     *
     * @param playCards 打牌
     * @return {@link List<PokerNoColor>}
     */
    public static List<PokerNoColor> convertString2List(String playCards) {

        if (playCards.isBlank()) {
            throw new RuntimeException("出牌不能为空!");
        }

        //字符串解析前 去除所有空格 将所有10转换为T 将所有大王小王转换为BS
        playCards = playCards
                .replace(" ", "")
                .replace("10", "T")
                .replace("大王", "B")
                .replace("小王", "S");

        //将简写格式 转换为 完整格式
        if (playCards.contains("-")) {
            StringBuilder builder = new StringBuilder();
            //顺子
            if (playCards.matches(".-.")) {
                String start = String.valueOf(playCards.charAt(0));
                String end = String.valueOf(playCards.charAt(2));

                int startValue = POINT_VALUE_MAP.get(start);
                int endValue = POINT_VALUE_MAP.get(end);

                for (int i = startValue; i <= endValue; i++) {
                    builder.append(findPointByValue(startValue));
                }
            }

            //姊妹对
            if (playCards.matches("..-..")) {

                String start = String.valueOf(playCards.charAt(0));
                String end = String.valueOf(playCards.charAt(3));

                int startValue = POINT_VALUE_MAP.get(start);
                int endValue = POINT_VALUE_MAP.get(end);

                for (int i = startValue; i <= endValue; i++) {
                    builder.append(findPointByValue(startValue));
                    builder.append(findPointByValue(startValue));
                }
            }
            playCards = builder.toString();
        }

        //解析为PokerNoColor集合
        List<PokerNoColor> playCardsList = new ArrayList<>();
        char[] cards = playCards.toCharArray();
        Stream.of(cards)
                .forEach(e -> playCardsList.add(new PokerNoColor(String.valueOf(e), POINT_VALUE_MAP.get(String.valueOf(e)))));
        return playCardsList;
    }


    /**
     * 出牌  如果手牌能够出 则返回true 并扣减相应手牌
     * 如果手牌能出 也需要 判断是否能大过上家
     * <p>
     * 否则返回false
     *
     * @param handCardsList 手牌列表
     * @param playCardsList 出牌列表
     * @param upPlayerCards 上家的出牌
     * @return boolean
     */
    public static boolean playCards(List<PokerNoColor> handCardsList, List<PokerNoColor> playCardsList, List<PokerNoColor> upPlayerCards) {

        List<PokerNoColor> handCardsListCopy = new ArrayList<>(handCardsList);

        for (PokerNoColor card : playCardsList) {
            for (int i = 0; i < handCardsListCopy.size(); i++) {

                PokerNoColor pokerNoColor = handCardsListCopy.get(i);
                if (isSame(card, pokerNoColor)) {
                    handCardsListCopy.remove(i);
                    i--;
                    continue;
                }

                if (i == handCardsListCopy.size() - 1) {
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
    public static boolean isSame(PokerNoColor p1, PokerNoColor p2) {
        return p1.getPoint().equals(p2.getPoint());
    }

    /**
     * 判断当前玩家出牌 是否大于上家
     *
     * @param upPlayerCards      上家出牌
     * @param currentPlayerCards 当前玩家出牌
     * @return boolean 大于上家 则返回true
     */
    public static boolean isUpper(List<PokerNoColor> upPlayerCards, List<PokerNoColor> currentPlayerCards) {

        //如果上家出牌为空,则表明由当前玩家出牌
        if (upPlayerCards == null || upPlayerCards.size() == 0){

        }
        //如果上家 出牌不为空 则需要进行出牌规则校验 且 要大于 上家

        return true;
    }


    /**
     * 根据Poker对象的实际值 查找点数
     *
     * @param value 价值
     * @return {@link String}
     */
    public static String findPointByValue(int value) {
        for (Map.Entry<String, Integer> entry : POINT_VALUE_MAP.entrySet()) {
            if (entry.getValue() == value){
                return entry.getKey();
            }
        }
        throw new RuntimeException("没有这个值对应的扑克点数");
    }


}
