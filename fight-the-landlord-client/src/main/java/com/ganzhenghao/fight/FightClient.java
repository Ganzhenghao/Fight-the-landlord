package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.PokerNoColor;
import com.ganzhenghao.fight.utlis.PokerUtils;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * @ClassName FightClient
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 17:50 星期六
 * @Version 1.0
 */
public class FightClient {
    /**
     * 手牌
     */
    private static List<PokerNoColor> handCards;
    /**
     * 身份id
     */
    private static String id;
    /**
     * 牌局是否结束 默认true
     */
    private static boolean end = true;
    /**
     * 是否为地主 默认false
     */
    private static boolean isLandlord = false;

    /**
     * ip地址 handCards
     */
    private static final String IP = "127.0.0.1";
    private static final int PORT = 5555;

    private static Integer sort;

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        //第一次连接
        firstConnect();

        //第二次连接 发送自己的id号 获得底牌信息 以及 地主信息 展示自己的手牌
        secondConnect();

        //第三次连接前 初始化点数对应的实际值 map
        Map<String, Integer> pointValue = new HashMap<>();


        //第三次来连接开始后 根据自己的出牌顺序 开始出牌 直到某一方手牌为0  出牌时需要判断出牌方法
        try (Socket socket = new Socket(IP, PORT)) {

            OutputStream os = socket.getOutputStream();
            if (isLandlord) {
                System.out.println("您的手牌如下:");
                PokerUtils.printPoker(handCards);
                System.out.println("您是地主,请先出牌:");
                String playCards = sc.nextLine();
                //TODO 解析出牌是否合法 不合法重新输入
                while (!PokerUtils.checkPlayCards(playCards)) {
                    System.out.println("出牌不合法,请重新出牌:");
                    playCards = sc.nextLine();
                }

                //解析出牌字符串 转换为List<PokerNoColor>
                List<PokerNoColor> playCardsList = PokerUtils.convertString2List(playCards);

                //解析手牌是否包含所出的牌 如果包含 且符合出票规则 且 大于上家手牌 则出牌




            }


            // 0 -> 是否为地主 1 -> 出牌顺序  2 -> 出牌  3 -> 出牌后剩余手牌  4 -> id
            List<Object> info = new ArrayList<>();
            info.add(isLandlord);
            info.add(sort);
            sort++;
            // todo


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void secondConnect() {
        try (Socket socket = new Socket(IP, PORT)) {

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            //发送自己的Id号
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(id);
            writer.flush();
            socket.shutdownOutput();

            //获取返回信息
            ObjectInputStream ois = new ObjectInputStream(is);

            try {
                ArrayList<Object> info = (ArrayList<Object>) ois.readObject();
                // 0 -> 是否为地主   1 -> 出牌顺序   3-> 底牌
                isLandlord = (boolean) (info.get(0));

                sort = (Integer) info.get(1);

                List<PokerNoColor> bottom = (List<PokerNoColor>) info.get(2);
                if (isLandlord) {
                    handCards.addAll(bottom);
                }

                //展示底牌
                System.out.println("底牌是:");
                PokerUtils.printPoker(bottom);

                //展示手牌
                System.out.println("您的手牌是:");
                Collections.sort(handCards);
                PokerUtils.printPoker(handCards);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void firstConnect() {
        try (Socket socket = new Socket(IP, PORT)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            //获取返回的Id和手牌信息
            ObjectInputStream ois = new ObjectInputStream(is);
            try {
                Object[] playerInfo = (Object[]) ois.readObject();
                id = (String) playerInfo[0];
                handCards = (List<PokerNoColor>) playerInfo[1];
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //返回确认收到信息
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(id + "--> receive");
            writer.flush();
            socket.shutdownOutput();
            System.out.println("player " + id + " 手牌为:" + handCards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 出牌顺序处理
     */
    private static void sortHandle() {
        if (sort == 3) {
            sort = 1;
        } else {
            sort++;
        }
    }
}

