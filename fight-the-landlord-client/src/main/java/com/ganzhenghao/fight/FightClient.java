package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.CardsWithRule;
import com.ganzhenghao.fight.bean.PlayCardsInfo;
import com.ganzhenghao.fight.bean.PlayerInfo;
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
     * 打牌的信息
     */
    private static final PlayCardsInfo PLAY_CARDS_INFO = new PlayCardsInfo();

    private static final PlayerInfo PLAYER_INFO = new PlayerInfo();

    /**
     * ip地址
     */
    private static final String IP = "127.0.0.1";
    private static final int PORT = 5555;


    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        //第一次连接
        System.out.println("人数满三个后会开始游戏 请耐心等待!");
        firstConnect();

        //第二次连接 发送自己的id号 获得底牌信息 以及 地主信息 展示自己的手牌
        secondConnect();


        //第三次连接前 初始化点数对应的实际值 map
        Map<String, Integer> pointValue = new HashMap<>();


        //第三次来连接开始后 地主优先出牌  然后发送出牌信息给服务器 其他玩家则只发送信息
        try (Socket socket = new Socket(IP, PORT)) {

            OutputStream os = socket.getOutputStream();
            if (PLAY_CARDS_INFO.isLandlord()) {
                System.out.println("您的手牌如下:");
                PokerUtils.printPoker(PLAY_CARDS_INFO.getHandCards());
                System.out.println("您是地主,请先出牌:");
                String playCards = SCANNER.nextLine();
                //解析出牌是否合法 不合法重新输入  返回值 0 -> Rule  1 -> 解析出的出牌列表
                CardsWithRule cardsWithRule = PokerUtils.checkPlayCards(playCards);
                while (cardsWithRule.getRule() == null) {
                    System.out.println("出牌不合法,请重新出牌:");
                    playCards = SCANNER.nextLine();
                    cardsWithRule = PokerUtils.checkPlayCards(playCards);
                }
                //解析手牌是否包含所出的牌 如果包含 且符合出票规则 且 大于上家手牌 则出牌
            }


            // 0 -> 是否为地主 1 -> 出牌顺序  2 -> 出牌  3 -> 出牌后剩余手牌  4 -> id
            //改为 PlayCardsInfo


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
            writer.write(PLAYER_INFO.toString());
            writer.flush();
            socket.shutdownOutput();

            System.out.println("第二次连接 id 发送完成");
            //获取返回信息
            ObjectInputStream ois = new ObjectInputStream(is);

            try {

                PlayCardsInfo playCardsInfo = (PlayCardsInfo) ois.readObject();
                // 0 -> 是否为地主   1 -> 出牌顺序   3-> 底牌

                System.out.println("第二次连接 数据读取完成");

                PLAY_CARDS_INFO.setLandlord(playCardsInfo.isLandlord());

                PLAY_CARDS_INFO.setSort(playCardsInfo.getSort());

                PLAY_CARDS_INFO.setBottomCards(playCardsInfo.getBottomCards());

                if (PLAY_CARDS_INFO.isLandlord()) {

                    PLAY_CARDS_INFO.getHandCards().addAll(
                            PLAY_CARDS_INFO.getBottomCards()
                    );
                }

                //展示底牌
                System.out.println("底牌是:");
                PokerUtils.printPoker(PLAY_CARDS_INFO.getBottomCards());

                //展示手牌
                System.out.println("您的手牌是:");
                Collections.sort(PLAY_CARDS_INFO.getHandCards());
                PokerUtils.printPoker(PLAY_CARDS_INFO.getHandCards());
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

                PlayerInfo playerInfo = (PlayerInfo) ois.readObject();
                PLAY_CARDS_INFO.setId(playerInfo.getId());
                PLAY_CARDS_INFO.setHandCards(playerInfo.getHandCards());

                PLAYER_INFO.setId(playerInfo.getId());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //返回确认收到信息
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(PLAY_CARDS_INFO.getId() + "--> receive");
            writer.flush();
            socket.shutdownOutput();
            System.out.println("player " + PLAY_CARDS_INFO.getId() + " 手牌为:" + PLAY_CARDS_INFO.getHandCards());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 出牌顺序处理
     */
    private static void sortHandle() {
        if (PLAY_CARDS_INFO.getSort() == 3) {
            PLAY_CARDS_INFO.setSort(1);
        } else {
            PLAY_CARDS_INFO.setSort(PLAY_CARDS_INFO.getSort() + 1);
        }
    }
}

