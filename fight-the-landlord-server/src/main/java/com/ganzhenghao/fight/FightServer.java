package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.Flag;
import com.ganzhenghao.fight.bean.PlayerInfo;
import com.ganzhenghao.fight.bean.PokerNoColor;
import com.ganzhenghao.fight.thread.FirstConnectThread;
import com.ganzhenghao.fight.thread.LandlordThread;
import com.ganzhenghao.fight.utlis.PokerUtils;

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

    private static final List<PokerNoColor> POKER = PokerUtils.getPokerNoColorList();
    private static final List<PlayerInfo> PLAYER_INFOS = new ArrayList<>();
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
                //Object[] info = (Object[]) playerInfo[count];

                PlayerInfo playerInfo = PLAYER_INFOS.get(count);
                String id = UUID.randomUUID().toString().replace("-", "");
                PLAYERS_ID.add(id);
                playerInfo.setId(id);
                pool.submit(new FirstConnectThread(playerInfo, socket, lock));
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

            //从ID列表随机一个ID 作为地主 开启LandlordThread线程 TODO 抢地主
            Random random = new Random();
            int index = random.nextInt(3);
            String landlordId = PLAYERS_ID.get(index);

            //定义出牌顺序 默认为1

            final AtomicInteger sort = new AtomicInteger(1);

            final Flag flag = new Flag();

            pool.submit(new LandlordThread(s1,bottom,landlordId,sort,lock,flag));
            pool.submit(new LandlordThread(s2,bottom,landlordId,sort,lock,flag));
            pool.submit(new LandlordThread(s3,bottom,landlordId,sort,lock,flag));

            //进入打牌环节
            


        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /**
     * 发牌
     */
    private static void deal() {

/*        Object[] playerOne = new Object[2];
        Object[] playerTwo = new Object[2];
        Object[] playerThree = new Object[2];*/

        PlayerInfo playerOne = new PlayerInfo();
        PlayerInfo playerTwo = new PlayerInfo();
        PlayerInfo playerThree = new PlayerInfo();

        bottom.clear();

        //洗牌
        Collections.shuffle(POKER);

        List<PokerNoColor> list1 = new ArrayList<>(POKER.subList(0, 17));
        List<PokerNoColor> list2 = new ArrayList<>(POKER.subList(17, 34));
        List<PokerNoColor> list3 = new ArrayList<>(POKER.subList(34, 51));
        bottom = new ArrayList<>(POKER.subList(51, 54));

        Collections.sort(list1);
        Collections.sort(list2);
        Collections.sort(list3);
        Collections.sort(bottom);


/*        playerOne[1] = list1;
        playerTwo[1] = list2;
        playerThree[1] = list3;*/


        playerOne.setHandCards(list1);
        playerTwo.setHandCards(list2);
        playerThree.setHandCards(list3);


/*        FightServer.playerInfo = new Object[3];
        FightServer.playerInfo[0] = playerOne;
        FightServer.playerInfo[1] = playerTwo;
        FightServer.playerInfo[2] = playerThree;*/

        PLAYER_INFOS.add(playerOne);
        PLAYER_INFOS.add(playerTwo);
        PLAYER_INFOS.add(playerThree);


    }


}
