package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.Poker;
import com.ganzhenghao.fight.bean.PokerNoColor;
import com.ganzhenghao.fight.thread.FirstConnectThread;
import com.ganzhenghao.fight.thread.LandlordThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName FightServer
 * @Description
 * @Author Ganzhenghao
 * @Date 2021/3/20 15:55 星期六
 * @Version 1.0
 */
public class FightServer {

    private static final List<PokerNoColor> POKER = getPokerNoColorList();
    private static Object[] playerInfo;
    private static List<PokerNoColor> bottom = new ArrayList<>();
    private final static List<String> PLAYERS_ID = new ArrayList<>();


    public static void main(String[] args) {

        ExecutorService pool = new ThreadPoolExecutor(6,
                10, 2,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(5),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

        // 创建ServerSocket对象 等待三人连接
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            //创建锁对象
            final Object lock = new Object();
            System.out.println("lock--main = " + lock);
            deal();
            int count = 0;
            while (count < 3) {
                Socket socket = serverSocket.accept();
                Object[] info = (Object[]) playerInfo[count];
                String id = UUID.randomUUID().toString().replace("-", "");
                PLAYERS_ID.add(id);
                info[0] = id;
                pool.submit(new FirstConnectThread(info, socket, lock));
                count++;
            }

            //线程等待 用于第三个线程连接后 再唤醒全部线程
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("人数已集齐，游戏将在三秒后开始！");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                lock.notifyAll();
            }

            // 第二次连接 开始随机分发地主
            Socket s1 = serverSocket.accept();
            Socket s2 = serverSocket.accept();
            Socket s3 = serverSocket.accept();

            // TODO 判断三个连接的id和服务器id列表相同

            //从ID列表随机一个ID 作为地主 TODO 抢地主
            Random random = new Random();
            int index = random.nextInt(3);
            String landlordId = PLAYERS_ID.get(index);
            pool.submit(new LandlordThread(s1,bottom,landlordId));
            pool.submit(new LandlordThread(s2,bottom,landlordId));
            pool.submit(new LandlordThread(s3,bottom,landlordId));
            //进入打牌环节
            


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 印刷扑克  TODO 待完善
     *
     * @param poker 扑克
     */
    private static void printPoker(List<PokerNoColor> poker) {
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
        System.out.println("|   ".repeat(size) + "|");
        System.out.println("|   ".repeat(size) + "|");
        System.out.println("————".repeat(size));
    }

    /**
     * 获得扑克列表(带花色)
     *
     * @return {@link List<Poker>}
     */
    private static List<Poker> getPokerList() {
        List<String> color = new ArrayList<>(List.of("♠", "♥", "♦", "♣"));
        List<String> point = new ArrayList<>(List.of("3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"));
        List<Poker> poker = new ArrayList<>(54);
        AtomicInteger value = new AtomicInteger(1);
        point.forEach(p -> {
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

    /**
     * 获得扑克列表(不带花色)
     *
     * @return {@link List<Poker>}
     */
    private static List<PokerNoColor> getPokerNoColorList() {
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
     * 发牌
     */
    private static void deal() {

        Object[] playerOne = new Object[2];
        Object[] playerTwo = new Object[2];
        Object[] playerThree = new Object[2];
        bottom.clear();

        //洗牌
        Collections.shuffle(POKER);

        List<PokerNoColor> list1 = new ArrayList<>(POKER.subList(0, 16));
        List<PokerNoColor> list2 = new ArrayList<>(POKER.subList(17, 33));
        List<PokerNoColor> list3 = new ArrayList<>(POKER.subList(34, 50));
        bottom = new ArrayList<>(POKER.subList(51, 53));

        Collections.sort(list1);
        Collections.sort(list2);
        Collections.sort(list3);
        Collections.sort(bottom);


        playerOne[1] = list1;
        playerTwo[1] = list2;
        playerThree[1] = list3;

        playerInfo = new Object[3];
        playerInfo[0] = playerOne;
        playerInfo[1] = playerTwo;
        playerInfo[2] = playerThree;

    }


}
